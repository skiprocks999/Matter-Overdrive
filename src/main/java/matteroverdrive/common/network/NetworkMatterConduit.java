package matteroverdrive.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.tile.TileMatterConduit;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.network.AbstractCableNetwork;
import matteroverdrive.core.network.AbstractTransferNetwork;
import matteroverdrive.core.tile.types.cable.AbstractCableTile;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class NetworkMatterConduit extends AbstractTransferNetwork<Double> {

	public NetworkMatterConduit(List<? extends AbstractCableTile<?>> varCables) {
		super(varCables, false);
	}

	public NetworkMatterConduit(Collection<? extends AbstractCableNetwork> networks) {
		super(networks, false);
	}

	@Override
	public Double emitToConnected(Double transfer, ArrayList<BlockEntity> ignored, boolean debug) {
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
	public Double extractFromConnected(Double amount, boolean simulate) {
		double stored = getCurrentMemberStorage();
		double taken = stored > amount ? amount : stored;
		if (!simulate) {
			double toRemove = taken;
			breakpoint: if (!connected.isEmpty() && toRemove > 0) {
				for (BlockEntity receiver : connected) {
					if (dirsPerConnectionMap.containsKey(receiver)) {
						for (Direction connection : dirsPerConnectionMap.get(receiver)) {
							LazyOptional<ICapabilityMatterStorage> lazy = receiver
									.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, connection).cast();
							if (lazy.isPresent()) {
								ICapabilityMatterStorage matter = lazy.resolve().get();
								if (matter.canExtract()) {
									double capStored = matter.getMatterStored();
									double removed = capStored > toRemove ? toRemove : capStored;
									toRemove -= matter.extractMatter(removed, false);
									if (toRemove <= 0) {
										break breakpoint;
									}
								}
							}
						}
					}
				}
			}
		}
		return taken;
	}

	@Override
	public Double getCurrentMemberStorage() {
		double stored = 0;
		if (!connected.isEmpty()) {
			for (BlockEntity receiver : connected) {
				if (dirsPerConnectionMap.containsKey(receiver)) {
					for (Direction connection : dirsPerConnectionMap.get(receiver)) {
						LazyOptional<ICapabilityMatterStorage> lazy = receiver
								.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, connection).cast();
						if (lazy.isPresent()) {
							ICapabilityMatterStorage matter = lazy.resolve().get();
							if (matter.canExtract() && Double.MAX_VALUE - stored >= matter.getMatterStored()) {
								stored += matter.getMatterStored();
							}
						}
					}
				}
			}
		}
		return stored;
	}

	@Override
	public Double getTotalMemberStorage() {
		double stored = 0;
		if (!connected.isEmpty()) {
			for (BlockEntity receiver : connected) {
				if (dirsPerConnectionMap.containsKey(receiver)) {
					for (Direction connection : dirsPerConnectionMap.get(receiver)) {
						LazyOptional<ICapabilityMatterStorage> lazy = receiver
								.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, connection).cast();
						if (lazy.isPresent()) {
							ICapabilityMatterStorage matter = lazy.resolve().get();
							if (matter.canExtract() && Double.MAX_VALUE - stored >= matter.getMaxMatterStored()) {
								stored += matter.getMaxMatterStored();
							}
						}
					}
				}
			}
		}
		return stored;
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

	@Override
	public AbstractCableNetwork newInstance(Collection<? extends AbstractCableNetwork> networks, boolean client) {
		return new NetworkMatterConduit(networks);
	}

	@Override
	public AbstractCableNetwork newInstance(List<? extends AbstractCableTile<?>> cables, boolean client) {
		return new NetworkMatterConduit(cables);
	}

}
