package matteroverdrive.core.block;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class GenericMachineBlock extends GenericEntityBlock {

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
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof GenericTile generic && generic != null) {
			ItemStack stack = player.getItemInHand(hand);
			if (UtilsCapability.hasMatterCap(stack)) {
				if (generic.hasCapability(MatterOverdriveCapabilities.MATTER_STORAGE)) {
					CapabilityMatterStorage matter = generic.exposeCapability(MatterOverdriveCapabilities.MATTER_STORAGE);
					ICapabilityMatterStorage storage = (ICapabilityMatterStorage) stack
							.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).cast().resolve().get();
					if (storage.canReceive() && matter.canExtract()) {
						double accepted = storage.receiveMatter(matter.getMatterStored(), true);
						storage.receiveMatter(accepted, false);
						matter.extractMatter(accepted, false);
						return InteractionResult.CONSUME;
					}
					if (storage.canExtract() && matter.canReceive()) {
						double accepted = matter.receiveMatter(storage.getMatterStored(), true);
						matter.receiveMatter(accepted, false);
						storage.extractMatter(accepted, false);
						return InteractionResult.CONSUME;
					}
				}
			}
			if (generic.hasMenu) {
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
