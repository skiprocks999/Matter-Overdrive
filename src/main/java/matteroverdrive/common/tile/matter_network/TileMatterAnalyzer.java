package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterAnalyzer;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileMatterAnalyzer extends GenericSoundTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 6;
	private static final int ENERGY_STORAGE = 512000;
	private static final int USAGE_PER_TICK = 80;
	
	public CapabilityInventory clientNetworkInv;
	
	public CapabilityInventory clientTileInv;
	
	public TileMatterAnalyzer(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_ANALYZER.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(1).setEnergySlots(1)
				.setUpgrades(4).setOwner(this).setValidUpgrades(InventoryMatterAnalyzer.UPGRADES)
				.setValidator(machineValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryMatterAnalyzer(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.MATTER_ANALYZER.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}

	@Override
	public boolean shouldPlaySound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNotPlaying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		Direction facing = getFacing();
		Direction back = Direction.NORTH;
		Direction relative = UtilsDirection.getRelativeSide(back, handleEastWest(facing));
		return relative == face;
	}
	
	@Override
	@Nullable
	public NetworkMatter getConnectedNetwork() {
		Direction back = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if(entity != null && entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}

	@Override
	public boolean isPowered(boolean client, boolean network) {
		// TODO Auto-generated method stub
		return false;
	}

}
