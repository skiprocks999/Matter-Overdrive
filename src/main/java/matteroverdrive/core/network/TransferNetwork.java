package matteroverdrive.core.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.common.tile.cable.AbstractEmittingCable;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class TransferNetwork<EMIT> extends BaseNetwork {

	public double networkMaxTransfer;
	
	public TransferNetwork(Collection<? extends AbstractCableTile<?>> varCables) {
		super(varCables);
	}
	
	public TransferNetwork(Set<? extends BaseNetwork> networks) {
		super(networks);
	}
	
	public TransferNetwork(Set<? extends BaseNetwork> networks, boolean special) {
		super(networks, special);
	}
	
	public double getNetworkMaxTransfer() {
		return networkMaxTransfer;
	}
	
	public void updateStatistics() {
		cableTypes.clear();
		for (ICableType type : getConductorTypes()) {
			cableTypes.put(type, new HashSet<>());
		}
		for (AbstractCableTile<?> abs : cables) {
			AbstractEmittingCable<?> wire = (AbstractEmittingCable<?>) abs;
			cableTypes.get(wire.getConductorType()).add(wire);
			networkMaxTransfer = networkMaxTransfer == -1 ? wire.getMaxTransfer() : Math.min(networkMaxTransfer, wire.getMaxTransfer());
			
		}
	}
	
	public abstract EMIT emit(EMIT transfer, ArrayList<BlockEntity> ignored, boolean debug);
	
}
