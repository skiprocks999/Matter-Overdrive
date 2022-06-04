package matteroverdrive.common.block.type;

import matteroverdrive.core.registers.IBulkRegistryObject;

public enum TypeMatterConduit implements IBulkRegistryObject {

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

}
