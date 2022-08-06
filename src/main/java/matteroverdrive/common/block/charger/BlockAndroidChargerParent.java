package matteroverdrive.common.block.charger;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.machine.BlockMachine;
import matteroverdrive.common.block.states.OverdriveBlockStates;
import matteroverdrive.common.block.states.OverdriveBlockStates.ChargerBlockPos;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.tile.TileCharger;
import matteroverdrive.core.block.GenericStateVariableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BlockAndroidChargerParent extends BlockMachine<TileCharger> {

	public BlockAndroidChargerParent() {
		super("charger_parent", TileCharger.class, TileCharger::new, TypeMachine.CHARGER);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockState middle = world.getBlockState(pos.offset(0, 1, 0));
		if (middle.isAir() || middle.getFluidState().isEmpty() && !middle.getFluidState().isEmpty()) {
			BlockState top = world.getBlockState(pos.offset(0, 2, 0));
			if (top.isAir() || top.getFluidState().isEmpty() && !top.getFluidState().isEmpty()) {
				return super.canSurvive(state, world, pos);
			}
		}
		return false;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		if (!world.isClientSide) {
			Direction facing = state.getValue(getRotationProperty());
			BlockState middle = DeferredRegisters.BLOCK_CHARGER_CHILD.get().defaultBlockState();
			if (middle.getBlock() instanceof GenericStateVariableBlock<?> variableBlock) {
				middle = middle.setValue(variableBlock.getRotationProperty(), facing);
				middle = middle.setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.MIDDLE);
			}
			BlockState top = DeferredRegisters.BLOCK_CHARGER_CHILD.get().defaultBlockState();
			if (top.getBlock() instanceof GenericStateVariableBlock<?> variableBlock) {
				top = top.setValue(variableBlock.getRotationProperty(), facing);
				top = top.setValue(OverdriveBlockStates.CHARGER_POS, ChargerBlockPos.TOP);
			}
			world.setBlockAndUpdate(pos.offset(0, 1, 0), middle);
			world.setBlockAndUpdate(pos.offset(0, 2, 0), top);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {

		if (!newState.hasProperty(getRotationProperty()) && !level.isClientSide) {
			level.setBlockAndUpdate(pos.offset(0, 1, 0), Blocks.AIR.defaultBlockState());
			level.setBlockAndUpdate(pos.offset(0, 2, 0), Blocks.AIR.defaultBlockState());
		}

		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rot) {
		if (state.hasProperty(getRotationProperty())) {
			BlockPos first = pos.offset(0, 1, 0);
			BlockPos second = pos.offset(0, 2, 0);
			BlockState firstState = level.getBlockState(first);
			if (firstState.getBlock() instanceof GenericStateVariableBlock<?> variable) {
				level.setBlock(first, firstState.setValue(variable.getRotationProperty(),
						rot.rotate(firstState.getValue(getRotationProperty()))), 3);

			}
			BlockState secondState = level.getBlockState(second);
			if (secondState.getBlock() instanceof GenericStateVariableBlock<?> variable) {
				level.setBlock(second, secondState.setValue(variable.getRotationProperty(),
						rot.rotate(secondState.getValue(getRotationProperty()))), 3);
			}
			return state.setValue(getRotationProperty(), rot.rotate(state.getValue(getRotationProperty())));
		}
		return super.rotate(state, level, pos, rot);
	}

}
