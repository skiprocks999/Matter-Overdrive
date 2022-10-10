package matteroverdrive.core.utils;

import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilsNbt {

	public static final String STORED_MATTER_VAL = "matter_val";
	public static final String BLOCK_POS = "block_pos";
	public static final String DIMENSION = "dimension";
	public static final String ON = "on";
	public static final String ITEM = "item";
	public static final String HELD = "help";
	public static final String PERCENTAGE = "percentage";
	public static final String TIMER = "timer";
	public static final String USE_TIME = "use_time";
	public static final String DURABILITY = "durability";
	public static final String INDEX = "index";

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

	@Nullable
	public static Item getItemFromString(String resource) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resource));
	}

}
