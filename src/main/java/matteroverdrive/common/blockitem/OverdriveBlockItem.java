package matteroverdrive.common.blockitem;

import java.util.List;

import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class OverdriveBlockItem extends BlockItem {

	private final boolean hasShiftTip;
	
	public OverdriveBlockItem(Block block, Properties properties, boolean hasShiftTip) {
		super(block, properties);
		this.hasShiftTip = hasShiftTip;
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

}
