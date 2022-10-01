package matteroverdrive.common.item.tools.electric;

import java.util.Arrays;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.clientbound.misc.PacketPlayMatterScannerSound;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.core.utils.UtilsWorld;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemMatterScanner extends ItemElectric {

	public static final int MAX_STORAGE = 20000;
	public static final int BASE_SCAN_TIME = 60;

	public static final int USAGE_PER_TICK = 1;

	public static final String RAY_TRACE_POS = "ray_trace";

	private static final int AMT_PER_SCAN = 10;

	public ItemMatterScanner() {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN), true, MAX_STORAGE, true, false);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {

			if (!world.isClientSide)
				stack.getOrCreateTag().remove(UtilsNbt.BLOCK_POS);

			return InteractionResultHolder.pass(player.getItemInHand(hand));

		} else if (isOn(stack) && isPowered(stack)) {

			BlockPos pos = UtilsWorld.getPosFromTraceNoFluid(player);

			if (pos == null) {
				setNotHolding(stack);
				return InteractionResultHolder.fail(player.getItemInHand(hand));
			}

			BlockState state = world.getBlockState(pos);
			if (state.isAir()) {
				setNotHolding(stack);
				return InteractionResultHolder.fail(player.getItemInHand(hand));
			}

			// Store block and start playing sound
			if (!hasStoredBlock(stack)) {
				saveBlockToStack(stack, state, pos);
				if (!isHeld(stack)) {
					setHolding(stack);
					if (!world.isClientSide)
						NetworkHandler.sendToClientEntityAndSelf(player,
								new PacketPlayMatterScannerSound(player.getUUID(), hand));
				}
				player.startUsingItem(hand);
				return InteractionResultHolder.pass(player.getItemInHand(hand));
			}

			if (!doesStoredMatch(state, stack) || !isSamePos(stack, pos)) {
				return InteractionResultHolder.fail(player.getItemInHand(hand));
			}

			return InteractionResultHolder.pass(player.getItemInHand(hand));

		}
		setNotHolding(stack);
		return InteractionResultHolder.fail(player.getItemInHand(hand));
	}

	@Override
	public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int remaining) {
		if (entity instanceof Player player) {
			if (isOn(stack) && isPowered(stack)) {
				BlockPos pos = UtilsWorld.getPosFromTraceNoFluid(player);

				if (pos == null) {
					player.releaseUsingItem();
					return;
				}

				BlockState state = world.getBlockState(pos);
				if (state.isAir() || !doesStoredMatch(state, stack) || !isSamePos(stack, pos)) {
					player.releaseUsingItem();
					return;
				}

				if (!world.isClientSide) {
					UtilsItem.getEnergyStorageCap(stack).removeEnergy(USAGE_PER_TICK);
					decrementTimer(stack);
				}

				return;
			}
			player.releaseUsingItem();
			return;
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
		if (entity instanceof Player player && isOn(stack) && isPowered(stack)) {

			BlockPos pos = UtilsWorld.getPosFromTraceNoFluid(player);

			if (pos == null) {
				playFailureSound(player);
				wipeStoredBlocks(stack);
				return stack;
			}

			BlockState state = world.getBlockState(pos);
			if (state.isAir() || !doesStoredMatch(state, stack) || !isSamePos(stack, pos)) {
				playFailureSound(player);
				wipeStoredBlocks(stack);
				return stack;
			}

			if (!world.isClientSide)
				scanBlockToDrive(world, stack);

			wipeStoredBlocks(stack);
			playSuccessSound(player);

		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return stack.getOrCreateTag().getInt(UtilsNbt.USE_TIME);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int charged) {
		if (entity instanceof Player player) {
			playFailureSound(player);
			setNotHolding(stack);
			wipeStoredBlocks(stack);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
		if (!level.isClientSide && entity instanceof Player player) {

			if (!isSelected || !isOn(stack) || !isBound(stack)) {
				setStoredPercentage(stack, 0);
				return;
			}

			BlockPos pos = UtilsWorld.getPosFromTraceNoFluid(player);

			if (pos == null) {
				setStoredPercentage(stack, 0);
				return;
			}

			BlockState state = player.level.getBlockState(pos);

			if (state.isAir()) {
				setStoredPercentage(stack, 0);
				return;
			}
			BlockEntity tile = level
					.getBlockEntity(NbtUtils.readBlockPos(stack.getTag().getCompound(UtilsNbt.BLOCK_POS)));
			if (tile != null && tile instanceof TilePatternStorage storage) {
				NetworkMatter network = storage.getConnectedNetwork();
				Item item = state.getBlock().asItem();
				int perc = 0;
				if (network != null) {
					perc = network.getHighestStorageLocationForItem(item, false)[3];
				} else {
					perc = storage.getHighestStorageLocForItem(item)[2];
				}
				if (perc < 0) {
					perc = 0;
				}
				setStoredPercentage(stack, perc);
				return;
			}

			setStoredPercentage(stack, 0);
		}

	}

	@Override
	public void appendPostSuperTooltip(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		super.appendPostSuperTooltip(stack, level, tooltips, advanced);
		if (stack.hasTag() && stack.getTag().contains(UtilsNbt.BLOCK_POS)) {
			tooltips.add(UtilsText
					.tooltip("has_storage_loc",
							NbtUtils.readBlockPos(stack.getTag().getCompound(UtilsNbt.BLOCK_POS)).toShortString())
					.withStyle(ChatFormatting.GRAY));
		} else {
			tooltips.add(UtilsText.tooltip("no_storage_loc").withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	/*
	 * Attempts to save to the Network first; falls back to bound tile if Network
	 * save fails
	 */
	private void scanBlockToDrive(Level world, ItemStack stack) {
		BlockPos blockLoc = NbtUtils.readBlockPos(stack.getTag().getCompound(RAY_TRACE_POS));
		Item item = world.getBlockState(blockLoc).getBlock().asItem();
		world.destroyBlock(blockLoc, false);
		if (isBound(stack)) {
			BlockEntity entity = world
					.getBlockEntity(NbtUtils.readBlockPos(stack.getTag().getCompound(UtilsNbt.BLOCK_POS)));
			if (entity != null && entity instanceof TilePatternStorage storage) {
				NetworkMatter network = storage.getConnectedNetwork();
				if (network != null) {
					int[] driveData = network.getHighestStorageLocationForItem(item, false);
					if (driveData[0] > -1) {
						TilePatternStorage found = network.getStorageFromIndex(driveData[0]);
						if (found == null) {
							spawnMatterDust(world, stack, blockLoc);
						} else {
							handlePatternStorage(world, found, Arrays.copyOfRange(driveData, 1, 4), stack, item,
									blockLoc);
						}
					} else if (network.storeItemFirstChance(item, AMT_PER_SCAN, false)) {
						storage.setChanged();
					} else {
						spawnMatterDust(world, stack, blockLoc);
					}
				} else {
					int[] index = storage.getHighestStorageLocForItem(item);
					handlePatternStorage(world, storage, index, stack, item, blockLoc);
				}
			} else {
				spawnMatterDust(world, stack, blockLoc);
			}
		} else {
			spawnMatterDust(world, stack, blockLoc);
		}
	}

	private void handlePatternStorage(Level world, TilePatternStorage storage, int[] index, ItemStack stack, Item item,
			BlockPos blockLoc) {
		if (index[0] > -1) {
			if (storage.storeItem(item, AMT_PER_SCAN, index)) {
				storage.setChanged();
			} else {
				spawnMatterDust(world, stack, blockLoc);
			}
		} else if (storage.isFull()) {
			spawnMatterDust(world, stack, blockLoc);
		} else {
			if (storage.storeItemFirstChance(item, AMT_PER_SCAN)) {
				storage.setChanged();
			} else {
				spawnMatterDust(world, stack, blockLoc);
			}
		}
	}

	private boolean hasStoredBlock(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(UtilsNbt.ITEM);
	}

	private void setHolding(ItemStack stack) {
		updateHoldingStatus(stack, true);
	}

	public void setNotHolding(ItemStack stack) {
		updateHoldingStatus(stack, false);
	}

	private void updateHoldingStatus(ItemStack stack, boolean status) {
		stack.getOrCreateTag().putBoolean(UtilsNbt.HELD, status);
	}

	private void playFailureSound(Player player) {
		player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundRegistry.SOUND_MATTER_SCANNER_FAIL.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
	}

	private void playSuccessSound(Player player) {
		player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundRegistry.SOUND_MATTER_SCANNER_SUCCESS.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
	}

	private boolean doesStoredMatch(BlockState state, ItemStack stack) {
		Item item = UtilsNbt.getItemFromString(stack.getTag().getString(UtilsNbt.ITEM));
		return item != null && UtilsItem.compareItems(item, state.getBlock().asItem());
	}

	private void saveBlockToStack(ItemStack stack, BlockState state, BlockPos pos) {
		double value = MatterRegister.INSTANCE.getServerMatterValue(new ItemStack(state.getBlock()));
		CompoundTag tag = stack.getOrCreateTag();
		if (value > 0.0) {
			tag.putString(UtilsNbt.ITEM,
					ForgeRegistries.ITEMS.getKey(state.getBlock().asItem()).toString().toLowerCase());
			tag.putDouble(UtilsNbt.STORED_MATTER_VAL, value);
			tag.put(RAY_TRACE_POS, NbtUtils.writeBlockPos(pos));
			tag.putInt(UtilsNbt.USE_TIME, (int) (BASE_SCAN_TIME + Math.ceil(value)));
			resetTimer(stack);
		} else {
			wipeStoredBlocks(stack);
		}
	}

	public boolean isOn(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean(UtilsNbt.ON);
	}

	public boolean isPowered(ItemStack stack) {
		CapabilityEnergyStorage storage = UtilsItem.getEnergyStorageCap(stack);
		if (storage != null) {
			return storage.getEnergyStored() > USAGE_PER_TICK;
		}
		return false;
	}

	public boolean isHeld(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean(UtilsNbt.HELD);
	}

	private boolean isBound(ItemStack stack) {
		return stack.getOrCreateTag().contains(UtilsNbt.BLOCK_POS);
	}

	private boolean isSamePos(ItemStack stack, BlockPos pos) {
		if (stack.getOrCreateTag().contains(RAY_TRACE_POS)) {
			return pos.equals(NbtUtils.readBlockPos(stack.getTag().getCompound(RAY_TRACE_POS)));
		}
		return false;
	}

	public int getTimeRemaining(ItemStack stack) {
		return stack.getOrCreateTag().getInt(UtilsNbt.TIMER);
	}

	private void resetTimer(ItemStack stack) {
		stack.getOrCreateTag().putInt(UtilsNbt.TIMER, getUseDuration(stack));
	}

	private void decrementTimer(ItemStack stack) {
		stack.getOrCreateTag().putInt(UtilsNbt.TIMER, getTimeRemaining(stack) - 1);
	}

	private void spawnMatterDust(Level world, ItemStack stack, BlockPos blockLoc) {
		ItemStack dust = new ItemStack(ItemRegistry.ITEM_RAW_MATTER_DUST.get());
		dust.getOrCreateTag().putDouble(UtilsNbt.STORED_MATTER_VAL,
				stack.getTag().getDouble(UtilsNbt.STORED_MATTER_VAL));
		world.addFreshEntity(
				new ItemEntity(world, blockLoc.getX() + 0.5, blockLoc.getY() + 0.5, blockLoc.getZ() + 0.5, dust));
	}

	private void setStoredPercentage(ItemStack stack, int percentage) {
		stack.getOrCreateTag().putInt(UtilsNbt.PERCENTAGE, percentage);
	}

	private void wipeStoredBlocks(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.remove(UtilsNbt.ITEM);
		tag.remove(UtilsNbt.STORED_MATTER_VAL);
		tag.remove(RAY_TRACE_POS);
		tag.remove(UtilsNbt.USE_TIME);
		tag.remove(UtilsNbt.TIMER);
	}

}
