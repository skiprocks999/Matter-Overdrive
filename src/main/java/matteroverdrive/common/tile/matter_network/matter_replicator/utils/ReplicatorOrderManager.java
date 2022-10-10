package matteroverdrive.common.tile.matter_network.matter_replicator.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.property.Property;
import net.minecraft.nbt.CompoundTag;

public class ReplicatorOrderManager {

	private List<QueuedReplication> orders;

	private TileMatterReplicator owner;
	private Property<CompoundTag> orderProp;

	public ReplicatorOrderManager() {
		orders = new ArrayList<>();
	}

	public void setVars(TileMatterReplicator owner, Property<CompoundTag> property) {
		this.owner = owner;
		orderProp = property;
	}

	public QueuedReplication getOrder(int index) {
		return orders.get(index);
	}

	public List<QueuedReplication> getAllOrders() {
		return orders;
	}

	public void addOrder(QueuedReplication order) {
		orders.add(order);
		setChanged();
		owner.updateTickable(true);
	}

	public void decRemaining(int index) {
		orders.get(index).decRemaining();
		setChanged();
	}

	public void removeCompletedOrders() {
		Iterator<QueuedReplication> it = orders.iterator();
		int oldSize = orders.size();
		QueuedReplication queued;
		while (it.hasNext()) {
			queued = it.next();
			if (queued.isFinished()) {
				it.remove();
			}
		}
		if (oldSize > orders.size()) {
			setChanged();
		}
	}

	public void cancelOrder(int index) {
		orders.get(index).cancel();
		setChanged();
	}

	public void wipeOrders() {
		orders.clear();
		setChanged();
	}

	private void setChanged() {
		orderProp.set(serializeNbt());
		owner.setShouldSaveData(true);
	}

	public int size() {
		return orders.size();
	}

	public boolean isEmpty() {
		return orders.isEmpty();
	}

	public CompoundTag serializeNbt() {
		CompoundTag data = new CompoundTag();
		int size = orders.size();
		data.putInt("orderCount", size);
		QueuedReplication queued;
		for (int i = 0; i < size; i++) {
			queued = orders.get(i);
			queued.setOwnerLoc(owner.getBlockPos());
			queued.setQueuePos(i);
			data.put("order" + i, queued.writeToNbt());
		}
		return data;
	}

	public void deserializeNbt(CompoundTag tag) {
		orders.clear();
		int orderSize = tag.getInt("orderCount");
		for (int i = 0; i < orderSize; i++) {
			orders.add(QueuedReplication.readFromNbt(tag.getCompound("order" + i)));
		}
	}

}
