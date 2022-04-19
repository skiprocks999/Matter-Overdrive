package matteroverdrive.common.block;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.RegistryObject;

public class BlockLightableMachine<T extends GenericTile> extends BlockMachine<T> {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	
	public BlockLightableMachine(BlockEntitySupplier<BlockEntity> supplier, TypeMachine type, RegistryObject<BlockEntityType<T>> entity) {
		super(supplier, type, entity);
		registerDefaultState(stateDefinition.any().setValue(LIT, Boolean.valueOf(false)));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LIT);
	}

}
