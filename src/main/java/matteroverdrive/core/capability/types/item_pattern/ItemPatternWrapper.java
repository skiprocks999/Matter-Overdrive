package matteroverdrive.core.capability.types.item_pattern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemPatternWrapper {

	public static ItemPatternWrapper EMPTY = new ItemPatternWrapper(Items.AIR, 0);
	
	public double percentage = 0;
	public Item item = Items.AIR;
	
	public ItemPatternWrapper(Item item, double percentage) {
		this.item = item;
		this.percentage = percentage;
	}
	
	public boolean isNotAir() {
		ItemStack stack = new ItemStack(item);
		return !stack.isEmpty();
	}
	
	public void writeToNbt(CompoundTag tag, String key) {
		CompoundTag data = new CompoundTag();
		data.putString("item", item.getRegistryName().toString().toLowerCase());
		data.putDouble("percentage", percentage);
		tag.put(key, data);
	}
	
	public static ItemPatternWrapper readFromNbt(CompoundTag data) {
		return new ItemPatternWrapper(ForgeRegistries.ITEMS.getValue(new ResourceLocation(data.getString("item"))), data.getDouble("percentage"));
	}
	
}
