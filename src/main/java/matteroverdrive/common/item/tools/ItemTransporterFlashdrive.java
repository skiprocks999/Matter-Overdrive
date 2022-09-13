package matteroverdrive.common.item.tools;

import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class ItemTransporterFlashdrive extends OverdriveItem {

	public ItemTransporterFlashdrive() {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN), true);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			if (player.isShiftKeyDown()) {
				stack.getOrCreateTag().remove(UtilsNbt.BLOCK_POS);
			} else {
				HitResult trace = getPlayerPOVHitResult(world, player, net.minecraft.world.level.ClipContext.Fluid.ANY);
				if (trace.getType() != Type.MISS && trace.getType() != Type.ENTITY) {
					BlockHitResult blockTrace = (BlockHitResult) trace;
					BlockPos pos = blockTrace.getBlockPos();
					CompoundTag tag = stack.getOrCreateTag();
					tag.remove(UtilsNbt.BLOCK_POS);
					tag.remove(UtilsNbt.DIMENSION);
					tag.put(UtilsNbt.BLOCK_POS, NbtUtils.writeBlockPos(pos));
					tag.put(UtilsNbt.DIMENSION, UtilsNbt.writeDimensionToTag(world.dimension()));
					return InteractionResultHolder.success(player.getItemInHand(hand));
				}
			}
		}
		return super.use(world, player, hand);
	}

	@Override
	public void appendPostSuperTooltip(ItemStack stack, Level world, List<Component> tooltips, TooltipFlag advanced) {
		if (stack.hasTag() && stack.getTag().contains(UtilsNbt.BLOCK_POS)) {
			CompoundTag tag = stack.getTag();
			tooltips.add(Component.literal(NbtUtils.readBlockPos(tag.getCompound(UtilsNbt.BLOCK_POS)).toShortString())
					.withStyle(ChatFormatting.GRAY));
			MutableComponent name;
			String key = UtilsNbt.readDimensionFromTag(tag.getCompound(UtilsNbt.DIMENSION)).location().getPath();
			if (UtilsText.dimensionExists(key)) {
				name = UtilsText.dimension(key);
			} else {
				name = Component.literal(key);
			}
			tooltips.add(name.withStyle(ChatFormatting.GRAY));
		}
	}

}
