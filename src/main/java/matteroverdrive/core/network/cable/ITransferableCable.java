package matteroverdrive.core.network.cable;

import matteroverdrive.core.network.AbstractNetwork;

public interface ITransferableCable<T extends AbstractNetwork<?, ?, ?>> extends IAbstractCable<T> {
	
	double getMaxTransfer();

}
