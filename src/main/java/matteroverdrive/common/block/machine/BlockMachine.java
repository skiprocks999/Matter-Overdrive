package matteroverdrive.common.block.machine;

import com.hrznstudio.titanium.block.tile.BasicTile;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.core.block.GenericMachineBlock;
import matteroverdrive.core.block.state.OverdriveBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlockMachine<T extends BasicTile<T>> extends GenericMachineBlock<T> {

	public TypeMachine type;
	private final BlockEntityType.BlockEntitySupplier<T> supplier;

	public BlockMachine(String name, Class<T> tileClass, BlockEntityType.BlockEntitySupplier<T> supplier,
			TypeMachine type) {
		this(OverdriveBlockProperties.Defaults.waterloggableFourway(DEFAULT_MACHINE_PROPERTIES), name, tileClass,
				supplier, type);
	}

	public BlockMachine(OverdriveBlockProperties properties, String name, Class<T> tileClass,
			BlockEntityType.BlockEntitySupplier<T> supplier, TypeMachine type) {
		super(properties, name, tileClass);
		this.type = type;
		this.supplier = supplier;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (type.hasCustomAABB) {
			return type.getShape(state.getValue(this.getRotationType().getProperties()[0]));
		}
		return super.getShape(state, level, pos, context);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		Optional<T> tile = getTile(level, pos);
		ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
		tile.ifPresent(t -> t.saveToItem(stack));
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
			@Nullable Direction direction) {
		return type.isRedstoneConnected;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return type == TypeMachine.TRANSPORTER ? 15 : super.getLightEmission(state, level, pos);
	}

	@Override
	public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
		return supplier;
	}

}
