package matteroverdrive.client;

import matteroverdrive.References;
import matteroverdrive.core.formatting.MatterFormatting;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

	@SubscribeEvent
	public static void matterTooltipApplier(ItemTooltipEvent event) {
		if (Screen.hasShiftDown()) {
			Integer val = MatterRegister.INSTANCE.getClientMatterValue(event.getItemStack().getItem());
			if (val != null) {
				event.getToolTip().add(new TranslatableComponent("tooltip." + References.ID + ".matterval",
						new TextComponent(MatterFormatting.formatMatterValue(val)).withStyle(ChatFormatting.GOLD))
								.withStyle(ChatFormatting.BLUE));
			} else {
				event.getToolTip()
						.add(new TranslatableComponent("tooltip." + References.ID + ".matterval",
								new TranslatableComponent("tooltip." + References.ID + ".nomatter")
										.withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.BLUE));
			}
		}
	}

}
