/**
 * Dummy interface for easy instance checking on cables
 * 
 * All conduits that use the existing matter network must
 * implement this interface
 */
package matteroverdrive.core.network.cable.utils;

import matteroverdrive.common.cable_network.MatterConduitNetwork;
import matteroverdrive.core.network.cable.ITransferableCable;

public interface IMatterConduit extends ITransferableCable<MatterConduitNetwork> {

}
