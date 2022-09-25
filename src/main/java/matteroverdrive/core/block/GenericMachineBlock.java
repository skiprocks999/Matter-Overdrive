package matteroverdrive.core.block;

import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public abstract class GenericMachineBlock extends GenericEntityBlock {

	public static final Properties DEFAULT_MACHINE_PROPERTIES = Properties.of(Material.METAL).strength(3.5F)
			.sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops();

	protected BlockEntitySupplier<BlockEntity> blockEntitySupplier;

	protected GenericMachineBlock(OverdriveBlockProperties properties, BlockEntitySupplier<BlockEntity> supplier) {
		super(properties);
		blockEntitySupplier = supplier;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		BlockEntity tile = level.getBlockEntity(pos);
		if(tile != null && tile instanceof GenericTile generic) {
			if(level.isClientSide()) {
				return generic.useClient(player, hand, hit);
			} else {
				return generic.useServer(player, hand, hit);
			}
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return blockEntitySupplier.create(pos, state);
	}

}
