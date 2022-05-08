package matteroverdrive.common.block;

import java.util.HashSet;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.core.block.multiblock.IMultiblockNode;
import matteroverdrive.core.block.multiblock.IMultiblockTileNode;
import matteroverdrive.core.block.multiblock.Subnode;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.RegistryObject;

public class BlockMachineMultiblock<T extends GenericTile> extends BlockMachine<T> implements IMultiblockNode {

	public static final HashSet<Subnode> CHARGER_NODES = new HashSet<>();

	static {
		CHARGER_NODES.add(new Subnode(new BlockPos(0, 1, 0), facing -> {
			switch (facing) {
			case EAST, WEST:
				return Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.90625D);
			case NORTH, SOUTH:
				return Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D);
			default:
				return Shapes.block();
			}
		}));
		CHARGER_NODES.add(new Subnode(new BlockPos(0, 2, 0), facing -> {
			switch (facing) {
			case EAST, WEST:
				return Shapes.box(0.0D, 0.0D, 0.09375D, 1.0D, 0.328125D, 0.90625D);
			case NORTH, SOUTH:
				return Shapes.box(0.09375D, 0.0D, 0.0D, 0.90625D, 0.328125D, 1.0D);
			default:
				return Shapes.block();
			}
		}));
	}

	private HashSet<Subnode> nodes;

	public BlockMachineMultiblock(BlockEntitySupplier<BlockEntity> supplier, TypeMachine type,
			RegistryObject<BlockEntityType<T>> entity, HashSet<Subnode> nodes) {
		super(supplier, type, entity);
		this.nodes = nodes;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return isValidMultiblockPlacement(state, worldIn, pos, nodes);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		BlockEntity tile = worldIn.getBlockEntity(pos);
		if (hasMultiBlock() && tile instanceof IMultiblockTileNode multi) {
			multi.onNodePlaced(worldIn, pos, state, placer, stack);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof IMultiblockTileNode multi) {
			multi.onNodeReplaced(level, pos, false);
		}
		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	public boolean hasMultiBlock() {
		return true;
	}

}
