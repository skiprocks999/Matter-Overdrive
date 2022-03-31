package matteroverdrive.common.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matteroverdrive.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class BlockColored extends BlockOverdrive {

	private static final List<BlockColored> BLOCKS = new ArrayList<>();

	private int color;

	public BlockColored(Properties properties, int color, boolean useLootTable) {
		super(properties, useLootTable);
		this.color = color;
		BLOCKS.add(this);
	}

	public int getColor() {
		return color;
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, Builder pBuilder) {
		return Arrays.asList(new ItemStack(this));
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ColorHandler {

		@SubscribeEvent
		public static void registerColoredBlocks(ColorHandlerEvent.Block event) {
			BLOCKS.forEach(block -> event.getBlockColors().register((state, level, pos, tintIndex) -> block.getColor(),
					block));
		}
	}

}
