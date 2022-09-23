package matteroverdrive.common.tile.transporter.utils;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.core.property.Property;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class TransporterEntityDataManager {

	private List<EntityDataWrapper> entities;

	private TileTransporter owner;
	private Property<CompoundTag> dataProp;

	public TransporterEntityDataManager() {
		entities = new ArrayList<>();
	}

	public void setVars(Property<CompoundTag> property, TileTransporter owner) {
		this.owner = owner;
		dataProp = property;
	}

	public void setEntities(List<Entity> entities) {
		this.entities.clear();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			EntityDataWrapper wrapper = new EntityDataWrapper(entity.getBbHeight(), entity.getBbWidth(), entity.getX(),
					entity.getZ());
			this.entities.add(wrapper);
		}
		setChanged();
	}

	public boolean wipe() {
		entities.clear();
		dataProp.set(serializeNbt());
		return dataProp.isDirtyNoUpdate();
	}

	private void setChanged() {
		dataProp.set(serializeNbt());
		owner.setShouldSaveData(true);
	}

	public List<EntityDataWrapper> getEntityData() {
		return entities;
	}

	public CompoundTag serializeNbt() {
		CompoundTag data = new CompoundTag();
		data.putInt("entities", entities.size());
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).serializeNbt(data, "entity" + i);
		}
		return data;
	}

	public void deserializeNbt(CompoundTag tag) {
		entities.clear();
		int size = tag.getInt("entities");
		for (int i = 0; i < size; i++) {
			entities.add(EntityDataWrapper.fromNbt(tag.getCompound("entity" + i)));
		}
	}

}
