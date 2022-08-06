package matteroverdrive.core.block;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import matteroverdrive.core.block.state.OverdriveBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class GenericMachineBlock<T extends BasicTile<T>> extends GenericStateVariableBlock<T> {

	// Defaults
	/**
	 * Default Machine Block Properties
	 */
	public static final Properties DEFAULT_MACHINE_PROPERTIES = Properties.of(Material.METAL).strength(3.5F)
			.sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops();

	/**
	 * Constructor that doesn't use the "Default Machine Properties" Note: This also
	 * does not provide a default StateVariable config!
	 *
	 * @param properties     The blocks BlockBehaviour Properties.
	 * @param stateVariables The state variables for the block.
	 * @param name           The "name" of the block (IE. "charger_block")
	 * @param tileClass      The BlockEntity Class for the block.
	 */
	protected GenericMachineBlock(OverdriveBlockProperties properties, String name, Class<T> tileClass) {
		super(properties, name, tileClass);
	}

	/**
	 * So you might be looking at this in the future asking where the old handling
	 * code went? Well I'll tell you! to consolidate behaviour and handling, this
	 * code now exists **ON** the BlockEntity itself. See the BlockEntity's
	 * implementation of
	 * {@link BasicTile#onActivated(Player, InteractionHand, Direction, double, double, double)}
	 * Also see
	 * {@link BasicTileBlock#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)}
	 * For where it's calling the #onActivated on the Tile!
	 */
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult ray) {
		if (level.isClientSide())
			return InteractionResult.SUCCESS;
		return super.use(state, level, pos, player, hand, ray);
	}

	/**
	 * So this method gets overriden in
	 * {@link BasicTileBlock#newBlockEntity(BlockPos, BlockState)} Which defaults to
	 * a new method called {@link BasicTileBlock#getTileEntityFactory()} This method
	 * is what gets overriden on new blocks and where we provide the BlockEntity
	 * creation!
	 *
	 * @param pos   The position of the block where the BlockEntity is created.
	 * @param state The state of the block which is creating the BlockEntity.
	 * @return Returns the new BlockEntity instance.
	 */
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return super.newBlockEntity(pos, state);
	}
}
