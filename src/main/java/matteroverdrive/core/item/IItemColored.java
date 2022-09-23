package matteroverdrive.core.item;

import matteroverdrive.client.ClientReferences.Colors;
import net.minecraft.world.item.ItemStack;

public interface IItemColored {

	default int getColor(ItemStack item, int layer) {
		return Colors.WHITE.getColor();
	}
	
	default boolean isColored() {
		return false;
	}

	default int getNumOfLayers() {
		return 0;
	}
	
}
