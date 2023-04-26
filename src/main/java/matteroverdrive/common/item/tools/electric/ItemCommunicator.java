package matteroverdrive.common.item.tools.electric;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.common.event.ServerEventHandler;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.common.tile.transporter.utils.ActiveTransportDataWrapper;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.utils.UtilsMath;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ItemCommunicator extends ItemElectric {

	//Important note: The BlockPos stored is the block above the Transporter!
	
	public static final int USAGE_PER_TRANSPORT = 1000;
	public static final int STORAGE = 4000;
	
	public static final String CHAT_MESSAGE = "communicator_fail";

	public ItemCommunicator(Properties properties) {
		super(properties, true, 4000, true, false);
	}
	
	@Override
	protected void appendPreSuperTooltip(ItemStack stack, Level world, List<Component> tooltips, TooltipFlag flag) {
		if (stack.hasTag() && stack.getTag().contains(UtilsNbt.BLOCK_POS)) {
			CompoundTag tag = stack.getTag();
			tooltips.add(Component.literal(NbtUtils.readBlockPos(tag.getCompound(UtilsNbt.BLOCK_POS)).toShortString())
					.withStyle(ChatFormatting.GRAY));
			MutableComponent name;
			String key = UtilsNbt.readDimensionFromTag(tag.getCompound(UtilsNbt.DIMENSION)).location().getPath();
			if (UtilsText.dimensionExists(key)) {
				name = UtilsText.dimension(key);
			} else {
				name = Component.literal(key);
			}
			tooltips.add(name.withStyle(ChatFormatting.GRAY));
		}
		super.appendPreSuperTooltip(stack, world, tooltips, flag);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide) {
			return super.use(level, player, hand);
		}

		ItemStack stack = player.getItemInHand(hand);

		if (stack.isEmpty()) {
			playFailureSound(player);
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		if (isInUse(stack)) {
			playFailureSound(player);
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		CapabilityEnergyStorage storage = getEnergyCap(stack);

		if (storage == null || storage.getEnergyStored() < USAGE_PER_TRANSPORT || !hasCoordiantes(stack)) {
			playFailureSound(player);
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		BlockPos pos = getCoordinates(stack);

		if (pos == null) {
			playFailureSound(player);
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}
		ServerLevel dimLevel = ServerLifecycleHooks.getCurrentServer().getLevel(getDimension(stack));

		BlockEntity entity = dimLevel.getBlockEntity(pos.below());

		if (entity == null || !(entity instanceof TileTransporter)) {
			playFailureSound(player);
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		TileTransporter transporter = (TileTransporter) entity;

		if (!transporter.recieverProp.get()
				|| transporter.cooldownProp.get() > MatterOverdriveConfig.TRANSPORTER_COOLDOWN.get()
				|| transporter.getMatterStorageCap().getMatterStored() < TileTransporter.MATTER_USAGE) {
			playFailureSound(player);
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		storage.removeEnergy(USAGE_PER_TRANSPORT);
		playSucessSound(player);
		setTimer(stack, TileTransporter.BUILD_UP_TIME);
		setInUse(stack);

		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slot, isSelected);
		if (level.isClientSide) {
			return;
		}
		int timer = getTimer(stack);
		BlockPos pos = getCoordinates(stack);
		if (timer > 0) {
			if (timer == TileTransporter.BUILD_UP_TIME) {
				level.playSound(null, entity.blockPosition(), SoundRegistry.SOUND_TRANSPORTER.get(),
						SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			double progress = 1.0D - ((double) timer / (double) TileTransporter.BUILD_UP_TIME);
			int particlesPerTick = (int) (progress * 20);
			for (int i = 0; i < particlesPerTick; i++) {
			
				spawnSendingParticles((ServerLevel) level, entity, progress);
			
			}
			decrementTimer(stack);
			return;
		}
		if (isInUse(stack)) {
			setNotInUse(stack);
			ServerLevel dimLevel = ServerLifecycleHooks.getCurrentServer().getLevel(getDimension(stack));
			TileTransporter transporter = (TileTransporter) dimLevel.getBlockEntity(pos.below());
			if (transporter == null) {
				playFailureSound(entity);
				if(entity instanceof Player player) {
					player.sendSystemMessage(UtilsText.chatMessage(CHAT_MESSAGE).withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
				}
				//TODO death penalty?
				queueArrivalParticles(level, entity);
			
				return;
			}
			entity.changeDimension(dimLevel, TileTransporter.MANAGER);
			entity.teleportToWithTicket(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			transporter.getMatterStorageCap().removeMatter(TileTransporter.MATTER_USAGE);
			transporter.cooldownProp.set(MatterOverdriveConfig.TRANSPORTER_COOLDOWN.get());
			transporter.setChanged();
			queueArrivalParticles(dimLevel, entity);
		}
	}

	public void spawnSendingParticles(ServerLevel level, Entity entity, double progress) {
		Vector3f vec = new Vector3f((float) entity.getX(), (float) entity.getY() - 1, (float) entity.getZ());
		double entityArea = Math.max(entity.getBbWidth() * entity.getBbHeight(), 0.3);
		Random random = MatterOverdrive.RANDOM;
		double time = Math.min(progress / (double) (TileTransporter.BUILD_UP_TIME), 1);
		float gravity = 0.015f;
		int age = (int) Math.round(UtilsMath.easeIn(time, 5, 15, 1));
		int count = (int) Math.round(UtilsMath.easeIn(time, 2, entityArea * 15, 1));

		for (int i = 0; i < count; i++) {
			float speed = 0.5F;
			float height = vec.y() + random.nextFloat() * entity.getBbHeight();

			Vector3f origin = new Vector3f(vec.x(), height, vec.z());
			Vector3f pos = UtilsMath.randomSpherePoint(origin.x(), origin.y(), origin.z(),
					new Vector3d(entity.getBbWidth(), 0, entity.getBbWidth()), random);
			origin.sub(pos);
			origin.mul(speed);

			level.sendParticles(new ParticleOptionReplicator().setGravity(gravity).setScale(0.1F).setAge(age), pos.x(),
					pos.y(), pos.z(), 0, 0, speed, 0, 0);
		}
	}
	
	public void queueArrivalParticles(Level level, Entity entity) {
		entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
			h.setTransporterTimer(TileTransporter.BUILD_UP_TIME);
		});
		level.getCapability(MatterOverdriveCapabilities.OVERWORLD_DATA).ifPresent(h -> {
			h.addActiveTransport(new ActiveTransportDataWrapper(entity.getUUID(), TileTransporter.BUILD_UP_TIME, level.dimension()));
		});
		ServerEventHandler.TASK_HANDLER.queueTask(() -> {
			level.playSound(null, entity.getOnPos(), SoundRegistry.SOUND_TRANSPORTER_ARRIVE.get(),
					SoundSource.BLOCKS, 1.0F, 1.0F);
		});
	}

	public void playFailureSound(Entity player) {
		player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundRegistry.SOUND_COMMUNICATOR_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	public void playSucessSound(Entity player) {
		player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundRegistry.SOUND_COMMUNICATOR_SUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	public void bindCoordinates(ItemStack stack, BlockPos pos) {
		stack.getOrCreateTag().put(UtilsNbt.BLOCK_POS, NbtUtils.writeBlockPos(pos));
	}

	@Nullable
	public BlockPos getCoordinates(ItemStack stack) {
		if (hasCoordiantes(stack)) {
			return NbtUtils.readBlockPos(stack.getTag().getCompound(UtilsNbt.BLOCK_POS));
		}
		return null;
	}

	public boolean hasCoordiantes(ItemStack stack) {
		return stack.getOrCreateTag().contains(UtilsNbt.BLOCK_POS);
	}

	public int getTimer(ItemStack stack) {
		return stack.getOrCreateTag().getInt(UtilsNbt.TIMER);
	}

	public void setTimer(ItemStack stack, int time) {
		stack.getOrCreateTag().putInt(UtilsNbt.TIMER, time);
	}

	public void decrementTimer(ItemStack stack) {
		stack.getOrCreateTag().putInt(UtilsNbt.TIMER, Math.max(stack.getOrCreateTag().getInt(UtilsNbt.TIMER) - 1, 0));
	}

	public void setInUse(ItemStack stack) {
		stack.getOrCreateTag().putBoolean(UtilsNbt.ON, true);
	}

	public void setNotInUse(ItemStack stack) {
		stack.getOrCreateTag().putBoolean(UtilsNbt.ON, false);
	}

	public boolean isInUse(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean(UtilsNbt.ON);
	}

	public boolean hasDimension(ItemStack stack) {
		return stack.getOrCreateTag().contains(UtilsNbt.DIMENSION);
	}

	public ResourceKey<Level> getDimension(ItemStack stack) {
		return UtilsNbt.readDimensionFromTag(stack.getOrCreateTag().getCompound(UtilsNbt.DIMENSION));
	}

	public void writeDimension(ItemStack stack, ResourceKey<Level> dimension) {
		stack.getOrCreateTag().put(UtilsNbt.DIMENSION, UtilsNbt.writeDimensionToTag(dimension));
	}

}
