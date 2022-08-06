package matteroverdrive.core.block;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GenericEntityBlock<T extends BasicTile<T>> extends BasicTileBlock<T> {

	/**
	 * Default Constructor for GenericEntityBlock.
	 *
	 * @param properties The blocks BlockBehaviour Properties.
	 * @param name       The "name" of the block (IE. "charger_block")
	 * @param tileClass  The BlockEntity Class for the block.
	 */
	protected GenericEntityBlock(Properties properties, String name, Class<T> tileClass) {
		super(name, properties, tileClass);
	}

	/**
	 * You might be wondering why we do nothing here. Well see the
	 * {@link BaseEntityBlock#getTicker(Level, BlockState, BlockEntityType)} method
	 * is being overridden and handled on the {@link BasicTileBlock} super class.
	 * See: {@link BasicTileBlock#getTicker(Level, BlockState, BlockEntityType)}
	 *
	 * @param level           The {@link Level} of the current {@link Block} being
	 *                        queried.
	 * @param blockState      The current {@link BlockState} of the {@link Block}
	 *                        being queried.
	 * @param blockEntityType The {@link BlockEntityType} being supplied.
	 * @param <R>             The {@link BlockEntity} class definition.
	 * @return Returns the {@link BlockEntityTicker} for the {@link BlockEntity}.
	 *         HOWEVER: We handle our {@link BlockEntityTicker} ticking on the
	 *         actual {@link BlockEntity} itself.
	 */
	@Nullable
	@Override
	public <R extends BlockEntity> BlockEntityTicker<R> getTicker(Level level, BlockState blockState,
			BlockEntityType<R> blockEntityType) {
		return super.getTicker(level, blockState, blockEntityType);
	}

	/**
	 * Override this to change the RenderShape if we need a TESR. INVISIBLE:
	 * {@link RenderShape#INVISIBLE} = No Rendering MODEL: {@link RenderShape#MODEL}
	 * = Model-based Rendering ENTITYBLOCK_ANIMATED:
	 * {@link RenderShape#ENTITYBLOCK_ANIMATED} = Model + TESR
	 *
	 * We are provided the {@link BlockState} of the block for use to decide if the
	 * blocks RenderShape needs to change. Potential optimisation would be if we add
	 * animations to the blocks that only if a "running" state is active. To use
	 * {@link RenderShape#ENTITYBLOCK_ANIMATED} otherwise use
	 * {@link RenderShape#MODEL}
	 * 
	 * @param state The state of the block.
	 * @return returns the {@link RenderShape} of the block, using the state as a
	 *         variable for the shape.
	 */
	@Override
	@SuppressWarnings({ "deprecation" })
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.MODEL;
	}

}
