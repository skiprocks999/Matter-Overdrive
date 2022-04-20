package matteroverdrive.common.tile;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterDecomposer extends GenericTile {

	public static final int SLOT_COUNT = 8;
	
	public TileMatterDecomposer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_DECOMPOSER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT).setInputs(1).setOutputs(1).setEnergySlots(1).setMatterSlots(1).setUpgrades(4).setOwner(this));
		addCapability(new CapabilityEnergyStorage(512000, true, false).setOwner(this));
		addCapability(new CapabilityMatterStorage(1024, false, true).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryMatterDecomposer(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName("matter_decomposer")));
		
	}

}
