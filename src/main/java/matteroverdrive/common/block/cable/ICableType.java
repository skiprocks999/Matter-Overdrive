package matteroverdrive.common.block.cable;

import matteroverdrive.core.registers.IBulkRegistryObject;

public interface ICableType extends IBulkRegistryObject {

	int getOrdinal();

	double getWidth();

}
