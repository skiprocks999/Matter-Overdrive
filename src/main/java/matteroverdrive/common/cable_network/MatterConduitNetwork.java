package matteroverdrive.common.cable_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.core.network.BaseNetwork;
import matteroverdrive.core.network.TransferNetwork;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MatterConduitNetwork extends TransferNetwork<Double> {

	public MatterConduitNetwork() {
		super();
	}
	
	public MatterConduitNetwork(Collection<? extends AbstractCableTile<?>> varCables) {
		super(varCables);
	}
	
	public MatterConduitNetwork(Set<? extends BaseNetwork> networks) {
		super(networks);
	}
	
	public MatterConduitNetwork(Set<? extends BaseNetwork> networks, boolean special) {
		super(networks, special);
	}
	
	@Override
	public Double emit(Double transfer, ArrayList<BlockEntity> ignored, boolean debug) {
		if (transfer > 0) {
			double sent = 0;
			Set<BlockEntity> availableAcceptors = Sets.newHashSet(acceptorSet);
			availableAcceptors.removeAll(ignored);
			if (!availableAcceptors.isEmpty()) {
				double perReciever = transfer / availableAcceptors.size();
				for (BlockEntity receiver : availableAcceptors) {
					if (acceptorInputMap.containsKey(receiver)) {
						double perConnection = perReciever / acceptorInputMap.get(receiver).size();
						for (Direction connection : acceptorInputMap.get(receiver)) {
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
	public boolean isConductor(BlockEntity tile) {
		return tile instanceof TileMatterConduit;
	}

	@Override
	public boolean isAcceptor(BlockEntity acceptor, Direction orientation) {
		return UtilsMatter.isMatterReceiver(acceptor);
	}

	@Override
	public ICableType[] getConductorTypes() {
		return TypeMatterConduit.values();
	}

	@Override
	public boolean canConnect(BlockEntity acceptor, Direction orientation) {
		return UtilsMatter.isMatterReceiver(acceptor, orientation.getOpposite());
	}

	@Override
	public BaseNetwork newInstance() {
		return new MatterConduitNetwork();
	}

	@Override
	public BaseNetwork newInstance(Set<? extends BaseNetwork> networks) {
		return new MatterConduitNetwork(networks);
	}

	@Override
	public BaseNetwork newInstance(Set<? extends BaseNetwork> networks, boolean special) {
		return new MatterConduitNetwork(networks, special);
	}

}
