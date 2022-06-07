/**
 * Dummy interface for easy instance checking on cables
 * 
 * All conduits that use the existing matter network must
 * implement this interface
 */
package matteroverdrive.core.network.cable.utils;

import matteroverdrive.common.cable_network.MatterNetwork;
import matteroverdrive.core.network.cable.IAbstractCable;

public interface INetworkCable extends IAbstractCable<MatterNetwork> {
	
}
