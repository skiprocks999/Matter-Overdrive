package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotEnergyCharging;
import matteroverdrive.core.inventory.slot.SlotGeneric;
import matteroverdrive.core.inventory.slot.SlotRestricted;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryMicrowave extends GenericInventoryTile<TileMicrowave> {

	public static final UpgradeType[] UPGRADES = new UpgradeType[] { UpgradeType.SPEED, UpgradeType.HYPER_SPEED,
			UpgradeType.POWER, UpgradeType.POWER_STORAGE, UpgradeType.MUFFLER };
	
	public InventoryMicrowave(int id, Inventory playerinv, CapabilityInventory invcap, ContainerData tilecoords) {
		super(DeferredRegisters.MENU_MICROWAVE.get(), id, playerinv, invcap, tilecoords);
	}
	
	public InventoryMicrowave(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileMicrowave.SLOT_COUNT, true, true),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotGeneric(invcap, nextIndex(), -29, 48, new int[] { 0 }, SlotType.MAIN, null));
		addSlot(new SlotRestricted(invcap, nextIndex(), 30, 48, new int[] { 0 }, SlotType.BIG, null));
		addSlot(new SlotEnergyCharging(invcap, nextIndex(), -29, 75, new int[] { 0 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 44, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 68, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 92, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 116, 55, new int[] { 2 }, UPGRADES));
	}

	@Override
	public int[] getHotbarNumbers() {
		return new int[] { 0, 1, 2, 3 };
	}

	@Override
	public int[] getPlayerInvNumbers() {
		return new int[] { 0 };
	}

}
