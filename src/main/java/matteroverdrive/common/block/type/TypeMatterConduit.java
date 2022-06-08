package matteroverdrive.common.block.type;

import matteroverdrive.common.block.cable.ICableType;

public enum TypeMatterConduit implements ICableType {

	REGULAR(1000, 2.5), HEAVY(10000, 3);

	public final int capacity;
	public final double width;

	private TypeMatterConduit(int capacity, double width) {
		this.capacity = capacity;
		this.width = width;
	}

	@Override
	public String id() {
		return "matter_conduit_" + this.toString().toLowerCase();
	}

	@Override
	public int getOrdinal() {
		return ordinal();
	}

	@Override
	public double getWidth() {
		return width;
	}

}
