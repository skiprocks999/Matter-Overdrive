package matteroverdrive.common.block.type;

import matteroverdrive.core.registers.IBulkRegistryObject;

public enum TypeMatterNetworkCable implements IBulkRegistryObject {
	REGULAR;

	@Override
	public String id() {
		return "network_cable_" + this.toString().toLowerCase();
	}

}
