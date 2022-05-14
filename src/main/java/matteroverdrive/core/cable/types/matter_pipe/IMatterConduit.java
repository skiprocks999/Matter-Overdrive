package matteroverdrive.core.cable.types.matter_pipe;

import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.core.cable.AbstractNetwork;
import matteroverdrive.core.cable.api.IAbstractCable;
import matteroverdrive.core.cable.api.ICableNetwork;

public interface IMatterConduit extends IAbstractCable {

	ICableNetwork getNetwork();

	ICableNetwork getNetwork(boolean createIfNull);

	void refreshNetwork();

	void refreshNetworkIfChange();

	TypeMatterConduit getMatterConduitType();

	@Override
	void setNetwork(AbstractNetwork<?, ?, ?, ?> aValueNetwork);

	@Override
	default Object getConductorType() {
		return getMatterConduitType();
	}

	@Override
	default double getMaxTransfer() {
		return getMatterConduitType().capacity;
	}

}
