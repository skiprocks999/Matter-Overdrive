package matteroverdrive.core.cable.types.matter_network;

import matteroverdrive.core.cable.AbstractNetwork;
import matteroverdrive.core.cable.api.IAbstractCable;
import matteroverdrive.core.cable.api.ICableNetwork;

public interface IMatterNetworkCable extends IAbstractCable {

	ICableNetwork getNetwork();

	ICableNetwork getNetwork(boolean createIfNull);

	void refreshNetwork();

	void refreshNetworkIfChange();

	void setNetwork(AbstractNetwork<?, ?, ?, ?> aValueNetwork);
	
	@Override
	default double getMaxTransfer() {
		return -1;
	}
	
}
