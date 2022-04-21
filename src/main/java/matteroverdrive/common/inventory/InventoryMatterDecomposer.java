package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotEnergyCharging;
import matteroverdrive.core.inventory.slot.SlotGeneric;
import matteroverdrive.core.inventory.slot.SlotMatterCharging;
import matteroverdrive.core.inventory.slot.SlotRestricted;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class InventoryMatterDecomposer extends GenericInventoryTile<TileMatterDecomposer> {

	public InventoryMatterDecomposer(int id, Inventory playerinv, CapabilityInventory invcap,
			ContainerData tilecoords) {
		super(DeferredRegisters.MENU_MATTER_DECOMPOSER.get(), id, playerinv, invcap, tilecoords);
	}

	public InventoryMatterDecomposer(int id, Inventory playerinv) {
		this(id, playerinv, new CapabilityInventory(TileMatterDecomposer.SLOT_COUNT), new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotGeneric(invcap, nextIndex(), -29, 48, new int[] {0}, SlotType.MAIN, null));
		addSlot(new SlotRestricted(invcap, nextIndex(), 30, 48, new int[] {0}, SlotType.BIG, IconType.MATTER_DUST_DARK));
		addSlot(new SlotEnergyCharging(invcap, nextIndex(), -29, 75, new int[] {0}));
		addSlot(new SlotMatterCharging(invcap, nextIndex(), 84, 48, new int[] {0}));
		// Upgrades: Speed, Hyper Speed, Power Reduction, Power Storage, Matter Storage,
		// Failure Reduction, Muffler
		addSlot(new SlotUpgrade(invcap, nextIndex(), 44, 55, new int[] { 2 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 68, 55, new int[] { 2 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 92, 55, new int[] { 2 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 116, 55, new int[] { 2 }));

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
