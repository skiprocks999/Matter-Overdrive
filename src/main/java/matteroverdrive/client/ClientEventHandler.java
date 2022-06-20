package matteroverdrive.client;

import java.util.UUID;

import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketToggleMatterScanner;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

	@SubscribeEvent
	public static void matterTooltipApplier(ItemTooltipEvent event) {
		if (Screen.hasShiftDown()) {
			event.getToolTip().add(getMatterTooltip(event.getItemStack()));
		}
	}

	private static Component getMatterTooltip(ItemStack stack) {
		if (UtilsMatter.isRawDust(stack)) {
			double val = UtilsNbt.readMatterVal(stack);
			return val > 0
					? UtilsText.tooltip("potmatterval",
							new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.LIGHT_PURPLE))
							.withStyle(ChatFormatting.BLUE)
					: UtilsText.tooltip("potmatterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
							.withStyle(ChatFormatting.BLUE);
		} else if (UtilsMatter.isRefinedDust(stack)) {
			double val = UtilsNbt.readMatterVal(stack);
			return val > 0
					? UtilsText
							.tooltip("matterval",
									new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.GOLD))
							.withStyle(ChatFormatting.BLUE)
					: UtilsText.tooltip("matterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
							.withStyle(ChatFormatting.BLUE);
		} else {
			Double val = MatterRegister.INSTANCE.getClientMatterValue(stack);
			return val == null
					? UtilsText.tooltip("matterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
							.withStyle(ChatFormatting.BLUE)
					: UtilsText
							.tooltip("matterval",
									new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.GOLD))
							.withStyle(ChatFormatting.BLUE);
		}

	}
	
	@SubscribeEvent
	public static void handlerTransporterArrival(MovementInputUpdateEvent event) {
		Entity entity = event.getEntity();
		if(MatterOverdriveConfig.accurate_transporter.get()) {
			entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
				if(h.getTransporterTimer() > 0) {
					Input input = event.getInput();
					input.down = false;
					input.forwardImpulse = 0.0F;
					input.jumping = false;
					input.left = false;
					input.leftImpulse = 0.0F;
					input.right = false;
					input.shiftKeyDown = false;
					input.up = false;
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void keyPressEvents(KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		
		if (KeyBinds.toggleMatterScanner.matches(event.getKey(), event.getScanCode()) && KeyBinds.toggleMatterScanner.isDown()) {
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner) {
				handleScannerToggle(stack, InteractionHand.MAIN_HAND, player.getUUID());
			} else {
				stack = player.getItemInHand(InteractionHand.OFF_HAND);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner) {
					handleScannerToggle(stack, InteractionHand.OFF_HAND, player.getUUID());
				}
			}
			
		}
	}

	private static void handleScannerToggle(ItemStack stack, InteractionHand hand, UUID uuid) {
		CapabilityEnergyStorage storage = UtilsItem.getEnergyStorageCap(stack);
		if(storage != null && storage.getEnergyStored() > 0) {
			NetworkHandler.CHANNEL.sendToServer(new PacketToggleMatterScanner(uuid, hand));
		}
	}
	
	

}
