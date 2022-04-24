package matteroverdrive.client;

import matteroverdrive.References;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.matter.MatterUtils;
import matteroverdrive.core.utils.UtilsFormatting;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

	@SubscribeEvent
	public static void matterTooltipApplier(ItemTooltipEvent event) {
		if (Screen.hasShiftDown()) {
			ItemStack stack = event.getItemStack();
			Double val = MatterRegister.INSTANCE.getClientMatterValue(stack);
			if (val != null) {
				event.getToolTip().add(new TranslatableComponent("tooltip." + References.ID + ".matterval",
						new TextComponent(UtilsFormatting.formatMatterValue(val)).withStyle(ChatFormatting.GOLD))
								.withStyle(ChatFormatting.BLUE));
			} else if (MatterUtils.isRawDust(stack)) {
				val = UtilsNbt.readMatterVal(stack);
				if (val > 0) {
					event.getToolTip().add(new TranslatableComponent("tooltip." + References.ID + ".potmatterval",
							new TextComponent(UtilsFormatting.formatMatterValue(val)).withStyle(ChatFormatting.LIGHT_PURPLE))
									.withStyle(ChatFormatting.BLUE));
				} else {
					event.getToolTip()
							.add(new TranslatableComponent("tooltip." + References.ID + ".potmatterval",
									new TranslatableComponent("tooltip." + References.ID + ".nomatter")
											.withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.BLUE));
				}
			} else if(MatterUtils.isRefinedDust(stack)) {
				val = UtilsNbt.readMatterVal(stack);
				if (val > 0) {
					event.getToolTip().add(new TranslatableComponent("tooltip." + References.ID + ".matterval",
							new TextComponent(UtilsFormatting.formatMatterValue(val)).withStyle(ChatFormatting.LIGHT_PURPLE))
									.withStyle(ChatFormatting.BLUE));
				} else {
					event.getToolTip()
							.add(new TranslatableComponent("tooltip." + References.ID + ".matterval",
									new TranslatableComponent("tooltip." + References.ID + ".nomatter")
											.withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.BLUE));
				}
			} else {
				event.getToolTip()
						.add(new TranslatableComponent("tooltip." + References.ID + ".matterval",
								new TranslatableComponent("tooltip." + References.ID + ".nomatter")
										.withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.BLUE));
			}
		}
	}

}
