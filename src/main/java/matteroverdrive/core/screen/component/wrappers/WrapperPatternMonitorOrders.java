package matteroverdrive.core.screen.component.wrappers;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.client.screen.ScreenPatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
import matteroverdrive.core.screen.component.wrappers.utils.AbstractWrapperReplicationQueue;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.MutableComponent;

public class WrapperPatternMonitorOrders extends AbstractWrapperReplicationQueue {

	private static final MutableComponent TITLE = UtilsText.gui("systemqueue");

	public WrapperPatternMonitorOrders(ScreenPatternMonitor screen, int x, int y, int[] screenNumbers) {
		super(screen, x, y, screenNumbers);
	}

	@Override
	public ScreenComponentVerticalSlider getSlider() {
		return ((ScreenPatternMonitor) screen).ordersSlider;
	}

	@Override
	public List<QueuedReplication> getOrders() {
		TilePatternMonitor tile = ((ScreenPatternMonitor) screen).getMenu().getTile();
		if (tile != null) {
			return tile.getGlobalOrders(true, true);
		}
		return new ArrayList<>();
	}

	@Override
	public MutableComponent getCatagoryName() {
		return TITLE;
	}

}
