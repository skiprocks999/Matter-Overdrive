package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.clientbound.PacketClientMNData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.network.NetworkDirection;

public class InventoryPatternMonitor extends GenericInventoryTile<TilePatternMonitor> {

	public InventoryPatternMonitor(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(DeferredRegisters.MENU_PATTERN_MONITOR.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryPatternMonitor(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(0, false, false), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		// unused
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] {};
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] {};
	}

	@Override
	public void sendAdditional(TilePatternMonitor tile, ServerPlayer player) {
		NetworkMatter network = tile.getConnectedNetwork();
		if (network != null && tile.getTicks() % 2 == 0) {
			NetworkHandler.CHANNEL.sendTo(new PacketClientMNData(network.serializeNetworkNbt(), tile.getBlockPos()),
					player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}

}
