package matteroverdrive.core.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.core.network.utils.NetworkLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BaseNetwork {
	
	public Set<AbstractCableTile<?>> cables = new HashSet<>();
	public Set<BlockEntity> connected = new HashSet<>();
	public Map<BlockEntity, Set<Direction>> dirsPerConnectionMap = new HashMap<>();
	public Map<ICableType, Set<AbstractCableTile<?>>> cableTypes = new HashMap<>();
	public boolean fixed;

	public BaseNetwork() {
		this(new HashSet<AbstractCableTile<?>>());
	}

	public BaseNetwork(Collection<? extends AbstractCableTile<?>> varCables) {
		cables.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public BaseNetwork(Set<? extends BaseNetwork> networks) {
		for (BaseNetwork net : networks) {
			if (net != null) {
				cables.addAll(net.cables);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}

	public BaseNetwork(Set<? extends BaseNetwork> networks, boolean special) {
		for (BaseNetwork net : networks) {
			if (net != null) {
				cables.addAll(net.cables);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}
	
	public void refresh() {
		Iterator<AbstractCableTile<?>> it = cables.iterator();
		connected.clear();
		dirsPerConnectionMap.clear();
		while (it.hasNext()) {
			AbstractCableTile<?> conductor = it.next();
			if (conductor == null || conductor.isRemoved()) {
				it.remove();
			} else {
				conductor.setNetwork(this);
			}
		}
		for (AbstractCableTile<?> conductor : cables) {
			BlockEntity tileEntity = (BlockEntity) conductor;
			for (Direction direction : Direction.values()) {
				BlockEntity acceptor = tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().relative(direction));
				if (acceptor != null && !isCable(acceptor) && canConnect(acceptor, direction)) {
					connected.add(acceptor);
					Set<Direction> directions = dirsPerConnectionMap.getOrDefault(acceptor, new HashSet<>());
					directions.add(direction.getOpposite());
					dirsPerConnectionMap.put(acceptor, directions);
				}
			}
		}
		updateStatistics();
	}

	public void updateStatistics() {
		cableTypes.clear();
		for (ICableType type : getConductorTypes()) {
			cableTypes.put(type, new HashSet<>());
		}
		for (AbstractCableTile<?> wire : cables) {
			cableTypes.get(wire.getConductorType()).add(wire);
		}
	}

	public void split(@Nonnull AbstractCableTile<?> splitPoint) {
			removeFromNetwork(splitPoint);
			BlockEntity[] connectedTiles = new BlockEntity[6];
			boolean[] dealtWith = { false, false, false, false, false, false };
			for (Direction direction : Direction.values()) {
				BlockPos ex = splitPoint.getBlockPos().offset(direction.getNormal());
				if (splitPoint.getLevel().hasChunkAt(ex)) {
					BlockEntity sideTile = splitPoint.getLevel().getBlockEntity(ex);
					if (sideTile != null) {
						connectedTiles[Arrays.asList(Direction.values()).indexOf(direction)] = sideTile;
					}
				}
			}
			for (int countOne = 0; countOne < connectedTiles.length; countOne++) {
				BlockEntity connectedBlockA = connectedTiles[countOne];
				if (connectedBlockA != null) {
					if (isCable(connectedBlockA) && !dealtWith[countOne]) {
						NetworkLocator finder = new NetworkLocator(splitPoint.getLevel(),
								connectedBlockA.getBlockPos(), this, splitPoint.getBlockPos());
						List<BlockEntity> partNetwork = finder.exploreNetwork();
						for (int countTwo = countOne + 1; countTwo < connectedTiles.length; countTwo++) {
							BlockEntity connectedBlockB = connectedTiles[countTwo];
							if (isCable(connectedBlockB) && !dealtWith[countTwo]
									&& partNetwork.contains(connectedBlockB)) {
								dealtWith[countTwo] = true;
							}
						}
						BaseNetwork newNetwork = newInstance();

						for (BlockEntity tile : finder.iteratedTiles) {
							if (tile != splitPoint) {
								newNetwork.cables.add((AbstractCableTile<?>) tile);
							}
						}
						newNetwork.refresh();
					}
				}
			}
			deregister();
		
	}

	public void merge(BaseNetwork network) {
		if (network != null && network != this) {
			Set<BaseNetwork> networks = new HashSet<>();
			networks.add(this);
			networks.add(network);
			BaseNetwork newNetwork = newInstance(networks);
			newNetwork.refresh();
		}
	}

	public void removeFromNetwork(AbstractCableTile<?> conductor) {
		cables.remove(conductor);
		if (cables.isEmpty()) {
			deregister();
		}
	}

	public void deregister() {
		cables.clear();
		connected.clear();
		dirsPerConnectionMap.clear();
		cableTypes.clear();
		CableNetworkRegistry.deregister(this);
	}

	public int getSize() {
		return cables.size();
	}
	
	public void tick() {
		
	}

	public abstract boolean isCable(BlockEntity tile);

	public abstract boolean canConnect(BlockEntity acceptor, Direction orientation);
	
	public abstract BaseNetwork newInstance();
	
	public abstract BaseNetwork newInstance(Set<? extends BaseNetwork> networks);
	
	public abstract BaseNetwork newInstance(Set<? extends BaseNetwork> networks, boolean special);

	public abstract ICableType[] getConductorTypes();

}