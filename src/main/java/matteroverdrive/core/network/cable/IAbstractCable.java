package matteroverdrive.core.network.cable;

import matteroverdrive.core.network.AbstractNetwork;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IAbstractCable<T extends AbstractNetwork<?, ?, ?>> {

	void removeFromNetwork();

	default T getNetwork() {
		return getNetwork(true);
	}
	
	T getNetwork(boolean createIfNull);

	//Why can't intellijank be normal?
	void setNetwork(AbstractNetwork<?, ?, ?> network);

	BlockEntity[] getAdjacentConnections();

	Object getConductorType();
	
	void refreshNetwork();

	void refreshNetworkIfChange();

}
