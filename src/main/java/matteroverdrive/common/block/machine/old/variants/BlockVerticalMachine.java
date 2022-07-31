package matteroverdrive.common.block.machine.old.variants;

import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.VerticalFacing;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.core.tile.types.old.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

public class BlockVerticalMachine<T extends GenericTile> extends BlockLightableMachine<T> {

	public BlockVerticalMachine(BlockEntitySupplier<BlockEntity> supplier, TypeMachine type,
			RegistryObject<BlockEntityType<T>> entity) {
		super(supplier, type, entity);
		registerDefaultState(stateDefinition.any().setValue(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.NONE));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(OverdriveBlockStates.VERTICAL_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState superState = super.getStateForPlacement(context);
		
		float viewRot = context.getPlayer().getViewXRot(1.0F);
		
		Direction vertical = null;
		
		if(viewRot < -50.0F) {
			vertical = Direction.DOWN;
		} else if (viewRot > 50.0F) {
			vertical = Direction.UP;
		}
		
		superState = superState.setValue(OverdriveBlockStates.VERTICAL_FACING, VerticalFacing.fromDirection(vertical));
		
		return superState;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (type.hasCustomAABB) {
			VerticalFacing facing = state.getValue(OverdriveBlockStates.VERTICAL_FACING);
			VoxelShape[] shape = type.shapes;
			if(facing.mapped == Direction.DOWN) {
				return shape[0];
			} else if (facing.mapped == Direction.UP) {
				return shape[1];
			}
		}
		return super.getShape(state, level, pos, context);
	}

}
