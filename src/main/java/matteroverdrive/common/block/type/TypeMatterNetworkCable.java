package matteroverdrive.common.block.type;

import matteroverdrive.common.block.cable.ICableType;

public enum TypeMatterNetworkCable implements ICableType {

	REGULAR(5);

	public final double width;

	private TypeMatterNetworkCable(double width) {
		this.width = width;
	}

	@Override
	public String id() {
		return "network_cable_" + this.toString().toLowerCase();
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
