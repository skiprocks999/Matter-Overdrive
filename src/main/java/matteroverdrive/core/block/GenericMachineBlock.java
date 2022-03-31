package matteroverdrive.core.block;

import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class GenericMachineBlock extends WaterloggableEntityBlock {

	protected BlockEntitySupplier<BlockEntity> blockEntitySupplier;

	protected GenericMachineBlock(BlockEntitySupplier<BlockEntity> supplier) {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());
		blockEntitySupplier = supplier;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if (!(state.getBlock() == newState.getBlock() && state.getValue(FACING) != newState.getValue(FACING)) && tile instanceof GenericTile generic && generic.hasCapability(CapabilityType.Item)) {
			CapabilityInventory cap = generic.exposeCapability(CapabilityType.Item);
			Containers.dropContents(worldIn, pos, new SimpleContainer(cap.getItemsArray()));
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof GenericTile generic && generic != null) {
			if(generic.hasMenu) {
				player.openMenu(generic.getMenuProvider());
			}
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return blockEntitySupplier.create(pos, state);
	}

}
