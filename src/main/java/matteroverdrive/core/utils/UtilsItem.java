package matteroverdrive.core.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UtilsItem {

	public static boolean compareItems(Item a, Item b) {
		return ItemStack.isSame(new ItemStack(a), new ItemStack(b));
	}
	
}
