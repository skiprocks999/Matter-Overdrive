package matteroverdrive.core.utils;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UtilsNbt {

	public static final String STORED_MATTER_VAL = "matter_val";
	public static final String BLOCK_POS = "block_pos";
	public static final String DIMENSION = "dimension";

	/* COMPOUND TAG */
	
	public static void writeMatterVal(ItemStack item, double value) {
		clearMatterVal(item);
		item.getOrCreateTag().putDouble(STORED_MATTER_VAL, value);
	}

	public static double readMatterVal(ItemStack item) {
		if (validateMatterTag(item)) {
			return item.getTag().getDouble(STORED_MATTER_VAL);
		}
		return 0.0;
	}

	public static void clearMatterVal(ItemStack item) {
		if (validateMatterTag(item)) {
			item.getTag().remove(STORED_MATTER_VAL);
		}
	}

	private static boolean validateMatterTag(ItemStack item) {
		return item.hasTag() && item.getTag().contains(STORED_MATTER_VAL);
	}
	
	public static CompoundTag writeDimensionToTag(ResourceKey<Level> level) {
		CompoundTag tag = new CompoundTag();
		tag.putString(DIMENSION, level.location().toString());
		return tag;
	}
	
	public static ResourceKey<Level> readDimensionFromTag(CompoundTag tag) {
		return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString(DIMENSION)));
	}
	
	/* BYTE BUFFER */

	public static void writeStringToBuffer(FriendlyByteBuf buf, String message) {
		buf.writeInt(message.length());
		for (char character : message.toCharArray()) {
			buf.writeChar(character);
		}
	}

	public static String readStringFromBuffer(FriendlyByteBuf buf) {
		String text = "";
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			text += buf.readChar();
		}
		return text;
	}
	
	public static void writeDimensionToBuffer(FriendlyByteBuf buf, ResourceKey<Level> dimension) {
		writeStringToBuffer(buf, dimension.location().toString());
	}
	
	public static ResourceKey<Level> readDimensionFromBuffer(FriendlyByteBuf buf) {
		return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(readStringFromBuffer(buf)));
	}

}
