package matteroverdrive.common.item.type;

import matteroverdrive.core.registers.IBulkRegistryObject;

public enum TypeIsolinearCircuit implements IBulkRegistryObject {

	TIER1, TIER2, TIER3, TIER4;

	public String id() {
		return "isolinear_circuit_" + this.toString().toLowerCase();
	}

}
