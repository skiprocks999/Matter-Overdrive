package matteroverdrive.common.block;

import java.util.Arrays;
import java.util.List;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.core.block.GenericMachineBlock;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockMachine extends GenericMachineBlock {

	public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

	public TypeMachine type;

	public BlockMachine(BlockEntitySupplier<BlockEntity> supplier, TypeMachine type) {
		super(supplier);
		this.type = type;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (type.hasCustomAABB) {
			return type.getShape(state.getValue(FACING));
		}
		return super.getShape(state, level, pos, context);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof GenericTile generic) {
			CapabilityInventory inv = generic.exposeCapability(CapabilityType.Item);
			if (MatterOverdriveConfig.machines_drop_items.get()) {
				Containers.dropContents(generic.getLevel(), generic.getBlockPos(), inv.getItems());
				return Arrays.asList(new ItemStack(this));
			}
			builder = builder.withDynamicDrop(CONTENTS, (context, consumer) -> {
				for (ItemStack stack : inv.getItems()) {
					consumer.accept(stack);
				}
			});
		}
		return super.getDrops(state, builder);
	}

}
