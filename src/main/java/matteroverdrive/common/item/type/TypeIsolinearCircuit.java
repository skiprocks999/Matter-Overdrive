package matteroverdrive.common.item.type;

import matteroverdrive.core.registers.IBulkRegistryObject;

public enum TypeIsolinearCircuit implements IBulkRegistryObject {

	TIER1,TEIR2,TEIR3,TEIR4;
	
	public String id() {
		return "circuit_" + this.toString().toLowerCase();
	}
	
}
