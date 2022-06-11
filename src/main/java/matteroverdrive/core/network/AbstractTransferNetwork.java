package matteroverdrive.core.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.common.tile.cable.AbstractEmittingCable;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractTransferNetwork<EMIT> extends AbstractCableNetwork {

	public double networkMaxTransfer;
	
	public AbstractTransferNetwork(List<? extends AbstractCableTile<?>> varCables) {
		super(varCables);
	}
	
	public AbstractTransferNetwork(Collection<? extends AbstractCableNetwork> networks) {
		super(networks);
	}
	
	@Override
	public void sortCables() {
		cableTypes.clear();
		for (ICableType type : getConductorTypes()) {
			cableTypes.put(type, new HashSet<>());
		}
		for (AbstractCableTile<?> abs : cables) {
			AbstractEmittingCable<?> wire = (AbstractEmittingCable<?>) abs;
			cableTypes.get(wire.getConductorType()).add(wire);
			networkMaxTransfer = Math.min(networkMaxTransfer, wire.getMaxTransfer());
		}
	}
	
	public abstract EMIT emitToConnected(EMIT transfer, ArrayList<BlockEntity> ignored, boolean debug);
	
	public abstract EMIT extractFromConnected(EMIT amount, boolean simulate);
	
	public abstract EMIT getCurrentMemberStorage();
	
	public abstract EMIT getTotalMemberStorage();
	
}
