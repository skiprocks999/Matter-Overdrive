package matteroverdrive.core.capability.types.item_pattern;

import matteroverdrive.core.utils.UtilsItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemPatternWrapper {

	public static ItemPatternWrapper EMPTY = new ItemPatternWrapper(Items.AIR, 0);

	private int percentage = 0;
	private Item item = Items.AIR;

	public static final int MAX = 100;

	public ItemPatternWrapper(Item item, int percentage) {
		this.item = item;
		this.percentage = percentage;
	}

	public int getPercentage() {
		return percentage;
	}

	public void increasePercentage(int amt) {
		percentage = Math.min(percentage + amt, MAX);
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public boolean isItem(Item item) {
		return UtilsItem.compareItems(item, this.item);
	}

	public boolean isMax() {
		return percentage >= MAX;
	}

	public boolean isAir() {
		ItemStack stack = new ItemStack(item);
		return stack.isEmpty();
	}

	public boolean isNotAir() {
		return !isAir();
	}

	public boolean isSame(ItemPatternWrapper other) {
		if (other == null)
			return false;
		return isItem(other.getItem()) && other.getPercentage() == getPercentage();
	}

	public void writeToNbt(CompoundTag tag, String key) {
		CompoundTag data = new CompoundTag();
		data.putString("item", ForgeRegistries.ITEMS.getKey(item).toString().toLowerCase());
		data.putInt("percentage", percentage);
		tag.put(key, data);
	}

	public static ItemPatternWrapper readFromNbt(CompoundTag data) {
		return new ItemPatternWrapper(ForgeRegistries.ITEMS.getValue(new ResourceLocation(data.getString("item"))),
				data.getInt("percentage"));
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeItem(new ItemStack(item));
		buffer.writeInt(percentage);
	}

	public static ItemPatternWrapper readFromBuffer(FriendlyByteBuf buffer) {
		return new ItemPatternWrapper(buffer.readItem().getItem(), buffer.readInt());
	}

}
