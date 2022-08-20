package matteroverdrive.common.block.charger;

import matteroverdrive.common.block.BlockMachine;
import matteroverdrive.common.block.OverdriveBlockStates;
import matteroverdrive.common.block.OverdriveBlockStates.ChargerBlockPos;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class BlockAndroidChargerParent<T extends GenericTile> extends BlockMachine<T> {

	public BlockAndroidChargerParent(BlockEntitySupplier<BlockEntity> supplier, TypeMachine type,
			RegistryObject<BlockEntityType<T>> entity) {
		super(supplier, type, entity);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockState middle = world.getBlockState(pos.offset(0, 1, 0));
		if (middle.isAir() || middle.getFluidState() != null && !middle.getFluidState().isEmpty()) {
			BlockState top = world.getBlockState(pos.offset(0, 2, 0));
			if (top.isAir() || top.getFluidState() != null && !top.getFluidState().isEmpty()) {
				return super.canSurvive(state, world, pos);
			}
		}
		return false;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		if (!world.isClientSide) {
			Direction facing = state.getValue(FACING);
			BlockState middle = BlockRegistry.BLOCK_CHARGER_CHILD.get().defaultBlockState();
			BlockState top = BlockRegistry.BLOCK_CHARGER_CHILD.get().defaultBlockState();
			middle = middle.setValue(FACING, facing);
			middle = middle.setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.MIDDLE);
			top = top.setValue(FACING, facing);
			top = top.setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.TOP);
			world.setBlockAndUpdate(pos.offset(0, 1, 0), middle);
			world.setBlockAndUpdate(pos.offset(0, 2, 0), top);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {

		if (!newState.hasProperty(FACING) && !level.isClientSide) {
			level.setBlockAndUpdate(pos.offset(0, 1, 0), Blocks.AIR.defaultBlockState());
			level.setBlockAndUpdate(pos.offset(0, 2, 0), Blocks.AIR.defaultBlockState());
		}

		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rot) {
		if (state.hasProperty(FACING)) {
			BlockPos first = pos.offset(0, 1, 0);
			BlockPos second = pos.offset(0, 2, 0);
			BlockState firstState = level.getBlockState(first);
			BlockState secondState = level.getBlockState(second);
			level.setBlock(first, firstState.setValue(FACING, rot.rotate(firstState.getValue(FACING))), 3);
			level.setBlock(second, secondState.setValue(FACING, rot.rotate(secondState.getValue(FACING))), 3);

			return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
		}
		return super.rotate(state, level, pos, rot);
	}

}
