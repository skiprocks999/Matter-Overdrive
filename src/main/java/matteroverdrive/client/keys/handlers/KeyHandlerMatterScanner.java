package matteroverdrive.client.keys.handlers;

import java.util.UUID;

import matteroverdrive.client.keys.KeyBinds;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.event.handler.client.AbstractKeyPressHandler;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.misc.PacketToggleMatterScanner;
import matteroverdrive.core.utils.UtilsItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class KeyHandlerMatterScanner extends AbstractKeyPressHandler {

	@Override
	public void handleKeyPress(Minecraft minecraft, int scanCode, int key, int action) {
		Player player = minecraft.player;

		if (KeyBinds.TOGGLE_MATTER_SCANNER.matches(key, scanCode) && KeyBinds.TOGGLE_MATTER_SCANNER.isDown()) {
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner) {
				handleScannerToggle(stack, InteractionHand.MAIN_HAND, player.getUUID());
			} else {
				stack = player.getItemInHand(InteractionHand.OFF_HAND);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner) {
					handleScannerToggle(stack, InteractionHand.OFF_HAND, player.getUUID());
				}
			}

		}
	}

	private void handleScannerToggle(ItemStack stack, InteractionHand hand, UUID uuid) {
		CapabilityEnergyStorage storage = UtilsItem.getEnergyStorageCap(stack);
		if (storage != null && storage.getEnergyStored() > 0) {
			NetworkHandler.sendToServer(new PacketToggleMatterScanner(uuid, hand));
		}
	}

}
