package matteroverdrive.common.tile.matter_network.matter_replicator;

import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public class QueuedReplication {

	private int remaining = 0;
	private int ordered = 0;
	private ItemPatternWrapper orderedItem = null;
	
	public QueuedReplication(ItemPatternWrapper wrapper, int ordered) {
		orderedItem = wrapper;
		this.ordered = ordered;
		remaining = ordered;
	}
	
	private QueuedReplication(ItemPatternWrapper wrapper, int ordered, int remaining) {
		orderedItem = wrapper;
		this.ordered = ordered;
		this.remaining = remaining;
	}
	
	public Item getItem() {
		return orderedItem.getItem();
	}
	
	public int getPercentage() {
		return orderedItem.getPercentage();
	}
	
	public int getRemaining() {
		return remaining;
	}
	
	public void decRemaining() {
		this.remaining--;
	}
	
	public boolean isFinished() {
		return remaining <= 0;
	}
	
	public int getOrderedCount() {
		return ordered;
	}
	
	public void cancel() {
		remaining = 0;
	}
	
	public void writeToNbt(CompoundTag tag, String key) {
		CompoundTag data = new CompoundTag();
		data.putInt("ordered", ordered);
		data.putInt("remaining", remaining);
		orderedItem.writeToNbt(data, "item");
		tag.put(key, data);
	}
	
	public static QueuedReplication readFromNbt(CompoundTag tag) {
		return new QueuedReplication(ItemPatternWrapper.readFromNbt(tag.getCompound("item")), tag.getInt("ordered"), tag.getInt("remaininig"));
	}
	
}
