package matteroverdrive.common.tile.transporter.utils;

import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.core.property.Property;
import net.minecraft.nbt.CompoundTag;

public class TransporterLocationManager {

	private TransporterLocationWrapper[] locations;
	private TileTransporter owner;
	private Property<CompoundTag> locationProp;

	public TransporterLocationManager(int size) {
		locations = new TransporterLocationWrapper[size];
		for (int i = 0; i < size; i++) {
			locations[i] = new TransporterLocationWrapper();
		}
	}

	public void setVars(Property<CompoundTag> property, TileTransporter owner) {
		locationProp = property;
		this.owner = owner;
	}

	public TransporterLocationWrapper getLocation(int loc) {
		return locations[loc];
	}

	public TransporterLocationWrapper[] getAllLocations() {
		return locations;
	}

	public void setLocation(int loc, TransporterLocationWrapper wrapper) {
		locations[loc] = wrapper;
		onChange();
	}

	private void onChange() {
		locationProp.set(serializeNbt());
		owner.setShouldSaveData(true);
	}

	public CompoundTag serializeNbt() {
		CompoundTag data = new CompoundTag();
		for (int i = 0; i < locations.length; i++) {
			data.put("destination" + i, locations[i].serializeNbt());
		}
		return data;
	}

	public void deserializeNbt(CompoundTag tag) {
		for (int i = 0; i < locations.length; i++) {
			locations[i].deserializeNbt(tag.getCompound("destination" + i));
		}
	}

}
