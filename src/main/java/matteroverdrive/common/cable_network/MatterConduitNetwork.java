package matteroverdrive.common.cable_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.core.network.AbstractNetwork;
import matteroverdrive.core.network.AbstractTransferNetwork;
import matteroverdrive.core.network.CableNetworkRegistry;
import matteroverdrive.core.network.cable.utils.IMatterConduit;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MatterConduitNetwork extends AbstractTransferNetwork<IMatterConduit, TypeMatterConduit, BlockEntity, Double> {
	
	public MatterConduitNetwork() {
		this(new HashSet<IMatterConduit>());
	}

	public MatterConduitNetwork(Collection<? extends IMatterConduit> varCables) {
		conductorSet.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public MatterConduitNetwork(Set<AbstractNetwork<IMatterConduit, TypeMatterConduit, BlockEntity>> networks) {
		for (AbstractNetwork<IMatterConduit, TypeMatterConduit, BlockEntity> net : networks) {
			if (net != null) {
				conductorSet.addAll(net.conductorSet);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}

	public MatterConduitNetwork(Set<MatterConduitNetwork> networks, boolean special) {
		for (MatterConduitNetwork net : networks) {
			if (net != null) {
				conductorSet.addAll(net.conductorSet);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
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
		return tile instanceof IMatterConduit;
	}

	@Override
	public boolean isAcceptor(BlockEntity acceptor, Direction orientation) {
		return UtilsMatter.isMatterReceiver(acceptor);
	}

	@Override
	public AbstractNetwork<IMatterConduit, TypeMatterConduit, BlockEntity> createInstance() {
		return new MatterConduitNetwork();
	}

	@Override
	public AbstractNetwork<IMatterConduit, TypeMatterConduit, BlockEntity> createInstanceConductor(
			Set<IMatterConduit> conductors) {
		return new MatterConduitNetwork(conductors);
	}

	@Override
	public AbstractNetwork<IMatterConduit, TypeMatterConduit, BlockEntity> createInstance(
			Set<AbstractNetwork<IMatterConduit, TypeMatterConduit, BlockEntity>> networks) {
		return new MatterConduitNetwork(networks);

	}

	@Override
	public TypeMatterConduit[] getConductorTypes() {
		return TypeMatterConduit.values();
	}

	@Override
	public boolean canConnect(BlockEntity acceptor, Direction orientation) {
		return UtilsMatter.isMatterReceiver(acceptor, orientation.getOpposite());
	}

}
