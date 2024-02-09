package matteroverdrive.common.block.cable.types;

import java.util.HashSet;
import java.util.Objects;

import matteroverdrive.common.block.cable.AbstractCableBlock;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.tile.matter_network.TileMatterNetworkCable;
import matteroverdrive.core.block.OverdriveBlockProperties;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BlockMatterNetworkCable extends AbstractCableBlock {

	public BlockMatterNetworkCable(TypeMatterNetworkCable type) {
		super(OverdriveBlockProperties.from(DEFAULT_CABLE_PROPERTIES).setCanBeWaterlogged(), type);
	}

	@Override
	protected void sortDirections(HashSet<Direction> usedDirs, HashSet<Direction> inventory, HashSet<Direction> cable,
																HashSet<Direction> energy, LevelAccessor world, BlockPos pos) {

		BlockEntity entity;
		for (Direction dir : Direction.values()) {
			entity = world.getBlockEntity(pos.relative(dir));
			if (entity instanceof TileMatterNetworkCable) {
				usedDirs.add(dir);
				cable.add(dir);
			} else if (entity instanceof IMatterNetworkMember member && member.canConnectToFace(dir.getOpposite())) {
				usedDirs.add(dir);
				inventory.add(dir);
			} else if (Objects.nonNull(entity) && entity.getCapability(ForgeCapabilities.ENERGY, dir).isPresent()) {
				usedDirs.add(dir);
				energy.add(dir);
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMatterNetworkCable(pos, state);
	}

}
