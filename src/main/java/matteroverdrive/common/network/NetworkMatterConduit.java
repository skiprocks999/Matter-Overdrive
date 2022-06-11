package matteroverdrive.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.TransferNetwork;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NetworkMatterConduit extends TransferNetwork<Double> {
	
	public NetworkMatterConduit(List<? extends AbstractCableTile<?>> varCables) {
		super(varCables);
	}
	
	public NetworkMatterConduit(Collection<? extends AbstractCableNetwork> networks) {
		super(networks);
	}
	
	@Override
	public Double emit(Double transfer, ArrayList<BlockEntity> ignored, boolean debug) {
		if (transfer > 0) {
			double sent = 0;
			Set<BlockEntity> availableAcceptors = Sets.newHashSet(connected);
			availableAcceptors.removeAll(ignored);
			if (!availableAcceptors.isEmpty()) {
				double perReciever = transfer / availableAcceptors.size();
				for (BlockEntity receiver : availableAcceptors) {
					if (dirsPerConnectionMap.containsKey(receiver)) {
						double perConnection = perReciever / dirsPerConnectionMap.get(receiver).size();
						for (Direction connection : dirsPerConnectionMap.get(receiver)) {
							double rec = UtilsMatter.receiveMatter(receiver, connection, perConnection, false);
							sent += rec;
						}
					}
				}
			}
			return sent;
		}
		return 0.0;
	}

	@Override
	public boolean isCable(BlockEntity tile) {
		return tile instanceof TileMatterConduit;
	}

	@Override
	public ICableType[] getConductorTypes() {
		return TypeMatterConduit.values();
	}

	@Override
	public boolean canConnect(BlockEntity acceptor, Direction orientation) {
		return UtilsMatter.isMatterReceiver(acceptor, orientation.getOpposite());
	}

}
