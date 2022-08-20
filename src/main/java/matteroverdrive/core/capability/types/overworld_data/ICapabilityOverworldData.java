package matteroverdrive.core.capability.types.overworld_data;

import java.util.List;

import matteroverdrive.common.tile.transporter.utils.ActiveTransportDataWrapper;

public interface ICapabilityOverworldData {

	/* Transporter Data */

	List<ActiveTransportDataWrapper> getTransporterData();

	void addActiveTransport(ActiveTransportDataWrapper wrapper);

	void removeTransportData(ActiveTransportDataWrapper wrapper);

}
