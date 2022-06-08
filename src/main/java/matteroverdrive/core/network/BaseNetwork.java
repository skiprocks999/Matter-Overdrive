package matteroverdrive.core.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import matteroverdrive.core.network.utils.NetworkLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BaseNetwork {
	
	public HashSet<AbstractCableTile<?>> conductorSet = new HashSet<>();
	public HashSet<BlockEntity> acceptorSet = new HashSet<>();
	public HashMap<BlockEntity, HashSet<Direction>> acceptorInputMap = new HashMap<>();
	public HashMap<ICableType, HashSet<AbstractCableTile<?>>> conductorTypeMap = new HashMap<>();
	public boolean fixed;

	public BaseNetwork() {
		this(new HashSet<AbstractCableTile<?>>());
	}

	public BaseNetwork(Collection<? extends AbstractCableTile<?>> varCables) {
		conductorSet.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public BaseNetwork(Set<? extends BaseNetwork> networks) {
		for (BaseNetwork net : networks) {
			if (net != null) {
				conductorSet.addAll(net.conductorSet);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}

	public BaseNetwork(Set<? extends BaseNetwork> networks, boolean special) {
		for (BaseNetwork net : networks) {
			if (net != null) {
				conductorSet.addAll(net.conductorSet);
				net.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}
	
	public void refresh() {
		Iterator<AbstractCableTile<?>> it = conductorSet.iterator();
		acceptorSet.clear();
		acceptorInputMap.clear();
		while (it.hasNext()) {
			AbstractCableTile<?> conductor = it.next();
			if (conductor == null || conductor.isRemoved()) {
				it.remove();
			} else {
				conductor.setNetwork(this);
			}
		}
		for (AbstractCableTile<?> conductor : conductorSet) {
			BlockEntity tileEntity = (BlockEntity) conductor;
			for (Direction direction : Direction.values()) {
				BlockEntity acceptor = tileEntity.getLevel()
						.getBlockEntity(new BlockPos(tileEntity.getBlockPos()).offset(direction.getNormal()));
				if (acceptor != null && !isConductor(acceptor)) {
					if (isAcceptor(acceptor, direction)) {
						if (canConnect(acceptor, direction)) {
							acceptorSet.add(acceptor);
							HashSet<Direction> directions = acceptorInputMap.containsKey(acceptor)
									? acceptorInputMap.get(acceptor)
									: new HashSet<>();
							directions.add(direction.getOpposite());
							acceptorInputMap.put(acceptor, directions);
						}
					}
				}
			}
		}
		updateStatistics();
	}

	public void updateStatistics() {
		conductorTypeMap.clear();
		for (ICableType type : getConductorTypes()) {
			conductorTypeMap.put(type, new HashSet<>());
		}
		for (AbstractCableTile<?> wire : conductorSet) {
			conductorTypeMap.get(wire.getConductorType()).add(wire);
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
					if (isConductor(connectedBlockA) && !dealtWith[countOne]) {
						NetworkLocator finder = new NetworkLocator(splitPoint.getLevel(),
								connectedBlockA.getBlockPos(), this, splitPoint.getBlockPos());
						List<BlockEntity> partNetwork = finder.exploreNetwork();
						for (int countTwo = countOne + 1; countTwo < connectedTiles.length; countTwo++) {
							BlockEntity connectedBlockB = connectedTiles[countTwo];
							if (isConductor(connectedBlockB) && !dealtWith[countTwo]
									&& partNetwork.contains(connectedBlockB)) {
								dealtWith[countTwo] = true;
							}
						}
						BaseNetwork newNetwork = newInstance();

						for (BlockEntity tile : finder.iteratedTiles) {
							if (tile != splitPoint) {
								newNetwork.conductorSet.add((AbstractCableTile<?>) tile);
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
		conductorSet.remove(conductor);
		if (conductorSet.isEmpty()) {
			deregister();
		}
	}

	public void deregister() {
		conductorSet.clear();
		acceptorSet.clear();
		acceptorInputMap.clear();
		conductorTypeMap.clear();
		CableNetworkRegistry.deregister(this);
	}

	public int getSize() {
		return conductorSet.size();
	}
	
	public void tick() {
		
	}

	public abstract boolean isConductor(BlockEntity tile);

	public abstract boolean isAcceptor(BlockEntity acceptor, Direction orientation);

	public abstract boolean canConnect(BlockEntity acceptor, Direction orientation);
	
	public abstract BaseNetwork newInstance();
	
	public abstract BaseNetwork newInstance(Set<? extends BaseNetwork> networks);
	
	public abstract BaseNetwork newInstance(Set<? extends BaseNetwork> networks, boolean special);

	public abstract ICableType[] getConductorTypes();

}