package matteroverdrive.common.block.type;

import matteroverdrive.core.registers.IBulkRegistryObject;

public enum TypeMatterConduit implements IBulkRegistryObject {

	REGULAR(1000), HEAVY(10000);

	public final int capacity;

	private TypeMatterConduit(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String id() {
		return "matter_conduit_" + this.toString().toLowerCase();
	}

}
