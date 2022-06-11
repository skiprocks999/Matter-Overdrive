package matteroverdrive.core.network;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BaseNetwork {
	
	public Set<AbstractCableTile<?>> cables = new HashSet<>();
	public Set<BlockEntity> connected = new HashSet<>();
	public Map<BlockEntity, Set<Direction>> dirsPerConnectionMap = new HashMap<>();
	public Map<ICableType, Set<AbstractCableTile<?>>> cableTypes = new HashMap<>();
	public boolean fixed;

	public BaseNetwork(List<? extends AbstractCableTile<?>> varCables) {
		cables.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public BaseNetwork(Collection<? extends BaseNetwork> networks) {
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
			boolean[] tilesToHandle = { false, false, false, false, false, false };
			Level world = splitPoint.getLevel();
			BlockPos splitPos = splitPoint.getBlockPos();
			for (Direction dir : Direction.values()) {
				BlockPos adjacent = splitPos.relative(dir);
				if (world.hasChunkAt(adjacent)) {
					BlockEntity sideTile = world.getBlockEntity(adjacent);
					if (sideTile != null) {
						connectedTiles[dir.ordinal()] = sideTile;
					}
				}
			}
			for (int i = 0; i < 6; i++) {
				BlockEntity currentBlock = connectedTiles[i];
				if (currentBlock != null) {
					if (isCable(currentBlock) && !tilesToHandle[i]) {
						NetworkLocator finder = new NetworkLocator(world, currentBlock.getBlockPos(), this, splitPos);
						List<BlockEntity> partNetwork = finder.exploreNetwork();
						for (int j = i + 1; j < 6; j++) {
							BlockEntity nextBlock = connectedTiles[j];
							if (isCable(nextBlock) && !tilesToHandle[j] && partNetwork.contains(nextBlock)) {
								tilesToHandle[j] = true;
							}
						}
						BaseNetwork newNetwork = newInstance(new HashSet<>());

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
	
	public abstract BaseNetwork newInstance(Collection<? extends BaseNetwork> networks);

	public abstract ICableType[] getConductorTypes();

}