package matteroverdrive.common.block.type;

import matteroverdrive.core.registers.IBulkRegistryObject;

public enum TypeMatterNetworkCable implements IBulkRegistryObject {
	
	REGULAR(2.5);

	public final double width;
	
	private TypeMatterNetworkCable(double width) {
		this.width = width;
	}
	
	@Override
	public String id() {
		return "network_cable_" + this.toString().toLowerCase();
	}

}
