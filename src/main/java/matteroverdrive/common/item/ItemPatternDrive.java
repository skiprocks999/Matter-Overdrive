package matteroverdrive.common.item;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item_pattern.CapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ItemPatternDrive extends OverdriveItem {

	private static final List<ItemPatternDrive> CONTAINERS = new ArrayList<>();
	// any item past 64 km will only get 1 scan
	// idea is high utility for low value items and poor utility for high-value
	// diamonds to dirt but not the other way around in other words
	public static final double MATTER_PER_FUSE = 1000000.0D;

	private static final String FUSED_KEY = "fused";

	public ItemPatternDrive() {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN));
		CONTAINERS.add(this);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new CapabilityItemPatternStorage();
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		super.appendHoverText(stack, level, tooltips, advanced);
		if (isFused(stack)) {
			tooltips.add(UtilsText.tooltip("fused").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
			stack.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS).ifPresent(cap -> {
				if (Screen.hasShiftDown()) {
					ItemPatternWrapper wrapper = cap.getStoredPatterns()[0];
					MutableComponent name = Component.translatable(wrapper.getItem().getDescriptionId());
					ChatFormatting color = ChatFormatting.RED;
					int percentage = wrapper.getPercentage();
					if (percentage >= 100) {
						color = ChatFormatting.GREEN;
					} else if (percentage < 100 && percentage > 50) {
						color = ChatFormatting.YELLOW;
					} else {
						color = ChatFormatting.RED;
					}
					tooltips.add(
							UtilsText.tooltip("storedpattern", name, UtilsText.SINGLE_DECIMAL.format(percentage) + "%")
									.withStyle(color));
					Double value = MatterRegister.INSTANCE.getClientMatterValue(new ItemStack(wrapper.getItem()));
					// datapack fuckery prevention
					if (value != null && value > 0) {
						double decayFactor = getDecayFactor(value);
						double usage = value * decayFactor;
						ChatFormatting warning;
						if (decayFactor <= 128) {
							warning = ChatFormatting.GREEN;
						} else if (decayFactor <= 4096) {
							warning = ChatFormatting.YELLOW;
						} else {
							warning = ChatFormatting.RED;
						}
						int effectiveUses = (int) Math.floor(MATTER_PER_FUSE / usage);
						tooltips.add(UtilsText.tooltip("effectiveuses",
								Component.literal(effectiveUses + "").withStyle(warning)));
					} else {
						tooltips.add(UtilsText.tooltip("effectiveuses",
								Component.literal("0").withStyle(ChatFormatting.RED)));
					}
				}
			});
		} else {
			stack.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS).ifPresent(cap -> {
				if (Screen.hasShiftDown()) {
					for (ItemPatternWrapper wrapper : cap.getStoredPatterns()) {
						if (wrapper.isNotAir()) {
							MutableComponent name = Component.translatable(wrapper.getItem().getDescriptionId());
							ChatFormatting color = ChatFormatting.RED;
							int percentage = wrapper.getPercentage();
							if (percentage >= 100) {
								color = ChatFormatting.GREEN;
							} else if (percentage < 100 && percentage > 50) {
								color = ChatFormatting.YELLOW;
							} else {
								color = ChatFormatting.RED;
							}
							tooltips.add(UtilsText
									.tooltip("storedpattern", name, UtilsText.SINGLE_DECIMAL.format(percentage) + "%")
									.withStyle(color));
						} else {
							tooltips.add(UtilsText.tooltip("empty").withStyle(ChatFormatting.GREEN));
						}
					}
				}
			});
		}
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return (int) ((stack.getOrCreateTag().getDouble(UtilsNbt.DURABILITY) / MATTER_PER_FUSE) * 13.0D);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (stack.hasTag()) {
			return isFused(stack);
		}
		return super.isBarVisible(stack);
	}

	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag tag = super.getShareTag(stack);
		if (tag == null) {
			tag = new CompoundTag();
		}
		LazyOptional<ICapabilityItemPatternStorage> lazyOp = stack
				.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
		if (lazyOp.isPresent()) {
			CapabilityItemPatternStorage storage = (CapabilityItemPatternStorage) lazyOp.resolve().get();
			tag.put("stored_patterns", storage.serializeNBT());
		}
		return tag;
	}

	@Override
	public void readShareTag(ItemStack stack, CompoundTag nbt) {
		if (nbt != null) {
			stack.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS).ifPresent(stor -> {
				CapabilityItemPatternStorage storage = (CapabilityItemPatternStorage) stor;
				storage.deserializeNBT(nbt.getCompound("stored_patterns"));
				nbt.remove("stored_patterns");
			});
		}
		super.readShareTag(stack, nbt);
	}

	public double getDurability(ItemStack stack) {
		return stack.getOrCreateTag().getDouble(UtilsNbt.DURABILITY);
	}

	public boolean isFused(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean(FUSED_KEY);
	}

	public static double getDecayFactor(double matterValue) {
		if (matterValue <= 4.0D) {
			return 1.0D;
		} else if (matterValue <= 8.0D) {
			return Math.pow(2, 2); // 4
		} else if (matterValue <= 12.0D) {
			return Math.pow(2, 7); // 128
		} else if (matterValue <= 16.0D) {
			return Math.pow(2, 12); // 4096
		} else if (matterValue <= 20.0D) {
			return Math.pow(2, 13);
		} else if (matterValue <= 24.0D) {
			return Math.pow(2, 14);
		} else if (matterValue <= 28.0D) {
			return Math.pow(2, 15);
		} else if (matterValue <= 32.0D) {
			return Math.pow(2, 16);
		} else if (matterValue <= 44.0D) {
			return Math.pow(2, 17);
		} else if (matterValue <= 48.0D) {
			return Math.pow(2, 18);
		} else if (matterValue <= 56.0D) {
			return Math.pow(2, 19);
		} else {
			return 1000000.0D;
		}
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ColorHandler {

		private static final int RED = UtilsRendering.getRGBA(1, 255, 0, 0);
		private static final int YELLOW = UtilsRendering.getRGBA(1, 255, 255, 0);
		private static final int GREEN = UtilsRendering.getRGBA(0, 0, 255, 0);
		private static final int NONE = UtilsRendering.getRGBA(1, 35, 45, 48);

		@SubscribeEvent
		public static void registerColoredBlocks(RegisterColorHandlersEvent.Item event) {
			CONTAINERS.forEach(item -> event.register((stack, index) -> {
				switch (index) {
				case 1:
					return handleColor(0, stack);
				case 2:
					return handleColor(1, stack);
				case 3:
					return handleColor(2, stack);
				default:
					return 0xFFFFFF;
				}
			}, item));
		}

		private static int handleColor(int index, ItemStack stack) {
			LazyOptional<ICapabilityItemPatternStorage> lazyOp = stack
					.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
			if (lazyOp.isPresent()) {
				ICapabilityItemPatternStorage storage = lazyOp.resolve().get();
				ItemPatternWrapper pattern = storage.getStoredPatterns()[index];
				if (pattern.isNotAir()) {
					int percentage = pattern.getPercentage();
					if (percentage >= 100) {
						return GREEN;
					} else if (percentage < 100 && percentage > 50) {
						return YELLOW;
					} else {
						return RED;
					}
				}
			}
			return NONE;
		}
	}

}
