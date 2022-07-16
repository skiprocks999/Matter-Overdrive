package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TilePatternMonitor extends GenericTile implements IMatterNetworkMember {

	public TilePatternMonitor(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_PATTERN_MONITOR.get(), pos, state);
		addCapability(new CapabilityInventory(0, false, false));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryPatternMonitor(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.PATTERN_MONITOR.id())));
		setTickable();
		setHasMenuData();
	}
	
	@Override
	public void tickServer() {
		if(getTicks() % 4 == 0) {
			if(getConnectedNetwork() != null) {
				UtilsTile.updateLit(this, true);
			} else {
				UtilsTile.updateLit(this, false);
			}
		}
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		VerticalFacing vertical = getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		if(vertical == null || vertical == VerticalFacing.NONE) {
			Direction relative = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
			return relative == face;
		} else {
			return face == vertical.mapped.getOpposite();
		}
		
	}

	@Override
	@Nullable
	public NetworkMatter getConnectedNetwork() {
		VerticalFacing vertical = getBlockState().getValue(OverdriveBlockStates.VERTICAL_FACING);
		Direction back;
		if(vertical == null || vertical == VerticalFacing.NONE) {
			back = UtilsDirection.getRelativeSide(Direction.NORTH, handleEastWest(getFacing()));
		} else {
			back = vertical.mapped.getOpposite();
		}
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if(entity != null && entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}

	@Override
	public boolean isPowered(boolean client, boolean network) {
		return true;
	}

}
