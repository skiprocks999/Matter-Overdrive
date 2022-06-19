package matteroverdrive.common.item;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item_pattern.CapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ItemPatternDrive extends OverdriveItem {

	private static final List<ItemPatternDrive> CONTAINERS = new ArrayList<>();
	
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
		stack.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS).ifPresent(cap -> {
			if(Screen.hasShiftDown()) {
				for(ItemPatternWrapper wrapper : cap.getStoredPatterns()) {
					if(wrapper.item != null && UtilsItem.compareItems(wrapper.item, Items.AIR)) {
						TranslatableComponent name = new TranslatableComponent(wrapper.item.getDescriptionId());
						ChatFormatting color = ChatFormatting.RED;
						double percentage = wrapper.percentage;
						if(percentage >= 100) {
							color = ChatFormatting.GREEN;
						} else if (percentage < 100 && percentage > 50) {
							color = ChatFormatting.YELLOW;
						} else {
							color = ChatFormatting.RED;
						}
						tooltips.add(UtilsText.tooltip("storedpattern", name, UtilsText.SINGLE_DECIMAL.format(percentage)).withStyle(color));
					} else {
						tooltips.add(UtilsText.tooltip("empty").withStyle(ChatFormatting.GREEN));
					}
				}
			}
		});
	}
	
	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag tag = super.getShareTag(stack);
		if (tag == null) {
			tag = new CompoundTag();
		}
		LazyOptional<ICapabilityItemPatternStorage> lazyOp = stack.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
		if(lazyOp.isPresent()) {
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
	
	
	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ColorHandler {

		private static final int RED = UtilsRendering.getRGBA(1, 255, 0, 0);
		private static final int YELLOW = UtilsRendering.getRGBA(1, 255, 255, 0);
		private static final int GREEN = UtilsRendering.getRGBA(0, 0, 255, 0);
		private static final int NONE = UtilsRendering.getRGBA(1, 35, 45, 48);
		
		@SubscribeEvent
		public static void registerColoredBlocks(ColorHandlerEvent.Item event) {
			CONTAINERS.forEach(item -> event.getItemColors().register((stack, index) -> {
				switch(index) {
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
			LazyOptional<ICapabilityItemPatternStorage> lazyOp = stack.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
			if(lazyOp.isPresent()) {
				ICapabilityItemPatternStorage storage = lazyOp.resolve().get();
				ItemPatternWrapper pattern = storage.getStoredPatterns()[index];
				if(pattern.item != null && pattern.isNotAir()) {
					double percentage = pattern.percentage;
					if(percentage >= 100) {
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
