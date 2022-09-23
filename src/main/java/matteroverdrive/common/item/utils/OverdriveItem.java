package matteroverdrive.common.item.utils;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.item.IItemColored;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class OverdriveItem extends Item implements IItemColored {

	private static final List<OverdriveItem> COLORED_ITEMS = new ArrayList<>();
	
	private final boolean hasShiftTip;
	
	public OverdriveItem(Properties pProperties, boolean hasShiftTip) {
		super(pProperties);
		this.hasShiftTip = hasShiftTip;
		//it will only get added to the list if it's colored; makes the event handling simpler
		if(isColored()) {
			COLORED_ITEMS.add(this);
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltips, TooltipFlag flag) {
		appendPreSuperTooltip(stack, world, tooltips, flag);
		super.appendHoverText(stack, world, tooltips, flag);
		appendPostSuperTooltip(stack, world, tooltips, flag);
		if(hasShiftTip) {
			if(Screen.hasShiftDown()) {
				tooltips.add(UtilsText.itemTooltip(this).withStyle(ChatFormatting.DARK_GRAY));
			} else {
				tooltips.add(UtilsText.tooltip("hasshifttip", UtilsText.tooltip("shiftkey").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.DARK_GRAY));
			}
		}
		
	}
	
	protected void appendPreSuperTooltip(ItemStack stack, Level world, List<Component> tooltips, TooltipFlag flag) {
		
	}
	
	protected void appendPostSuperTooltip(ItemStack stack, Level world, List<Component> tooltips, TooltipFlag flag) {
		
	}
	
	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ColorHandler {
		
		@SubscribeEvent
		public static void registerColoredBlocks(RegisterColorHandlersEvent.Item event) {
			COLORED_ITEMS.forEach(item -> event.register((stack, index) -> {
				if(index < item.getNumOfLayers()) {
					return item.getColor(stack, index);
				}
				return Colors.WHITE.getColor();
			}, item));
		}
		
	}

}
