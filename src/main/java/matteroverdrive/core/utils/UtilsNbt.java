package matteroverdrive.core.utils;

import net.minecraft.world.item.ItemStack;

public class UtilsNbt {

	public static final String STORED_MATTER_VAL = "matter_val";
	
	public static void writeMatterVal(ItemStack item, double value) {
		clearMatterVal(item);
		item.getOrCreateTag().putDouble(STORED_MATTER_VAL, value);
	}
	
	public static double readMatterVal(ItemStack item) {
		if(validateMatterTag(item)) {
			return item.getTag().getDouble(STORED_MATTER_VAL);
		}
		return 0.0;
	}
	
	public static void clearMatterVal(ItemStack item) {
		if(validateMatterTag(item)) {
			item.getTag().remove(STORED_MATTER_VAL);
		}
	}
	
	private static boolean validateMatterTag(ItemStack item) {
		return item.hasTag() && item.getTag().contains(STORED_MATTER_VAL);
	}
	
}
