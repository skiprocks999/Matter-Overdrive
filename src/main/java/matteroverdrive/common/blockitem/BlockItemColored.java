package matteroverdrive.common.blockitem;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class BlockItemColored extends OverdriveBlockItem {

	private static final List<BlockItemColored> BLOCK_ITEMS = new ArrayList<>();

	private int color;

	public BlockItemColored(Block block, Properties properties, boolean hasShiftTip, int color) {
		super(block, properties, hasShiftTip);
		this.color = color;
		BLOCK_ITEMS.add(this);
	}

	public int getColor() {
		return color;
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ColorHandler {

		@SubscribeEvent
		public static void registerColoredBlocks(RegisterColorHandlersEvent.Item event) {
			BLOCK_ITEMS.forEach(item -> event.register((stack, index) -> item.getColor(), item));
		}
	}

}
