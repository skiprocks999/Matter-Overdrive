package matteroverdrive.common.tile.matter_network.matter_replicator.utils;

import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;

public class QueuedReplication {

	private int remaining = 0;
	private int ordered = 0;
	private ItemPatternWrapper orderedItem = null;
	// only set for client
	private BlockPos ownerLoc = new BlockPos(0, -1000, 0);
	private int queuePos = -1;

	public static final QueuedReplication EMPTY = new QueuedReplication(ItemPatternWrapper.EMPTY, 0, 0, BlockPos.ZERO,
			0);

	public QueuedReplication(ItemPatternWrapper wrapper, int ordered) {
		orderedItem = wrapper;
		this.ordered = ordered;
		remaining = ordered;
	}

	private QueuedReplication(ItemPatternWrapper wrapper, int ordered, int remaining, BlockPos ownerPos, int queue) {
		orderedItem = wrapper;
		this.ordered = ordered;
		this.remaining = remaining;
		this.ownerLoc = ownerPos;
		this.queuePos = queue;
	}

	public void setOwnerLoc(BlockPos pos) {
		ownerLoc = pos;
	}

	public void setQueuePos(int pos) {
		queuePos = pos;
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

	public BlockPos getOwnerPos() {
		return ownerLoc;
	}

	public int getQueuePos() {
		return queuePos;
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public CompoundTag writeToNbt() {
		CompoundTag data = new CompoundTag();
		data.putInt("ordered", ordered);
		data.putInt("remaining", remaining);
		orderedItem.writeToNbt(data, "item");
		data.putInt("queue", queuePos);
		data.put("pos", NbtUtils.writeBlockPos(ownerLoc));
		return data;
	}

	public static QueuedReplication readFromNbt(CompoundTag tag) {
		return new QueuedReplication(ItemPatternWrapper.readFromNbt(tag.getCompound("item")), tag.getInt("ordered"),
				tag.getInt("remaining"), NbtUtils.readBlockPos(tag.getCompound("pos")), tag.getInt("queue"));
	}

}
