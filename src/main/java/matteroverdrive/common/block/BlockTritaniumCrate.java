package matteroverdrive.common.block;

import java.util.Arrays;
import java.util.List;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.core.block.WaterloggableEntityBlock;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockTritaniumCrate extends WaterloggableEntityBlock {

	private static final VoxelShape NS = Block.box(0.0D, 0.0D, 2.0D, 16.0D, 12.0D, 14.0D);
	private static final VoxelShape EW = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 12.0D, 16.0D);

	public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

	public BlockTritaniumCrate(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
		case NORTH, SOUTH:
			return NS;
		case EAST, WEST:
			return EW;
		default:
			return super.getShape(state, level, pos, context);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new TileTritaniumCrate(pPos, pState);
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
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof GenericTile generic && generic != null) {
			if (generic.hasMenu) {
				player.openMenu(generic.getMenuProvider());
				generic.getLevel().playSound(null, tile.getBlockPos(), SoundRegister.SOUND_CRATEOPEN.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
			}
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof TileTritaniumCrate crate) {
			CapabilityInventory inv = crate.exposeCapability(CapabilityType.Item);
			if(MatterOverdriveConfig.crate_drop_items.get()) {
				Containers.dropContents(crate.getLevel(), crate.getBlockPos(), inv.getItems());
				return Arrays.asList(new ItemStack(this));
			}
			builder = builder.withDynamicDrop(CONTENTS, (context, consumer) -> {
				for(ItemStack stack :  inv.getItems()) {
					consumer.accept(stack);
				}
			});
		}
		return super.getDrops(state, builder);
	}
	
	

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pPos, BlockState pState) {
		ItemStack stack = super.getCloneItemStack(level, pPos, pState);
		level.getBlockEntity(pPos, DeferredRegisters.TILE_TRITANIUMCRATE.get()).ifPresent(crate -> {
			crate.saveToItem(stack);
		});
		return stack;
	}

}
