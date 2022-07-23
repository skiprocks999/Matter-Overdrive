package matteroverdrive.core.screen.component.wrappers;

import java.util.Collections;
import java.util.List;

import matteroverdrive.client.screen.ScreenMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.QueuedReplication;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
import matteroverdrive.core.screen.component.wrappers.utils.AbstractWrapperReplicationQueue;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.TranslatableComponent;

public class WrapperMatterReplicatorOrders extends AbstractWrapperReplicationQueue {
	
	private static final TranslatableComponent TITLE = UtilsText.gui("replicatorqueue");
	
	public WrapperMatterReplicatorOrders(ScreenMatterReplicator screen, int x, int y, int[] screenNumbers) {
		super(screen, x, y, screenNumbers);
		
	}

	@Override
	public ScreenComponentVerticalSlider getSlider() {
		return ((ScreenMatterReplicator) screen).slider;
	}

	@Override
	public List<QueuedReplication> getOrders() {
		TileMatterReplicator replicator = ((ScreenMatterReplicator) screen).getMenu().getTile();
		if(replicator == null || replicator.clientOrders == null) {
			return Collections.emptyList();
		}
		return replicator.clientOrders;
	}

	@Override
	public TranslatableComponent getCatagoryName() {
		return TITLE;
	}

}
