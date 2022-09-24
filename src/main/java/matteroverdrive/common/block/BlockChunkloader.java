package matteroverdrive.common.block;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.tile.TileChunkloader;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class BlockChunkloader<T extends GenericTile> extends BlockMachine<T> {

	public BlockChunkloader(BlockEntitySupplier<BlockEntity> supplier, TypeMachine type,
			RegistryObject<BlockEntityType<T>> entity) {
		super(supplier, type, entity);
	}
	
	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean moving) {
		if(newState.isAir()){
			BlockEntity entity = level.getBlockEntity(pos);
			if(entity != null && entity instanceof TileChunkloader chunkloader) {
				chunkloader.updateChunks(false);
			}
		}
		super.onRemove(oldState, level, pos, newState, moving);
	}

}
