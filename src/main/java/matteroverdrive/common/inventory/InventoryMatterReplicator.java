package matteroverdrive.common.inventory;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.inventory.slot.SlotEnergyCharging;
import matteroverdrive.core.inventory.slot.SlotMatterCharging;
import matteroverdrive.core.inventory.slot.SlotRestricted;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
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
		this(id, playerinv, new CapabilityInventory(TileMatterReplicator.SLOT_COUNT, true, true),
				new SimpleContainerData(3));
	}

	@Override
	public void addInvSlots(CapabilityInventory invcap, Inventory playerinv) {
		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 75, new int[] { 0 }, SlotType.BIG,
				IconType.PATTERN_DRIVE_DARK, DeferredRegisters.ITEM_PATTERN_DRIVE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 8, 78, new int[] { 2 }, SlotType.BIG, IconType.SHIELDING_DARK,
				DeferredRegisters.ITEM_LEAD_PLATE.get()));
		addSlot(new SlotRestricted(invcap, nextIndex(), 67, 48, new int[] { 0 }, SlotType.BIG, IconType.NONE));
		addSlot(new SlotRestricted(invcap, nextIndex(), 94, 48, new int[] { 0 }, SlotType.BIG,
				IconType.MATTER_DUST_DARK));
		addSlot(new SlotEnergyCharging(invcap, nextIndex(), 8, 102, new int[] { 0 }));
		addSlot(new SlotMatterCharging(invcap, nextIndex(), 8, 129, new int[] { 0 }));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 81, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 105, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 129, 55, new int[] { 2 }, UPGRADES));
		addSlot(new SlotUpgrade(invcap, nextIndex(), 153, 55, new int[] { 2 }, UPGRADES));
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
