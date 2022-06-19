package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.matter_network.TileMatterReplicator;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryMatterReplicator extends GenericInventoryTile<TileMatterReplicator> {

	public static final UpgradeType[] UPGRADES = new UpgradeType[] { UpgradeType.SPEED, UpgradeType.HYPER_SPEED,
			UpgradeType.POWER, UpgradeType.POWER_STORAGE, UpgradeType.MATTER_STORAGE, UpgradeType.FAIL_SAFE,
			UpgradeType.MUFFLER };
	
	public InventoryMatterReplicator(int id, Inventory playerinv, CapabilityInventory invcap,
			ContainerData tilecoords) {
		super(DeferredRegisters.MENU_MATTER_REPLICATOR.get(), id, playerinv, invcap, tilecoords);
	}
	
	public InventoryMatterReplicator(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileMatterReplicator.SLOT_COUNT, true, true), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] {};
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] {};
	}

}
