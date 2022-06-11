/**
 * Based on Dynamic Network and various sub and super classes from Mekanism under MIT License 
 * 
 * I suppose I can list them all if asked
 * 
 * https://github.com/mekanism/Mekanism/tree/l10n_1.18.x/src/main/java/mekanism/common/lib/transmitter
 * 
 * 
 */
package matteroverdrive.core.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractCableNetwork {
	
	/*
	 * Need to store:
	 * > Cables
	 * > Blocks connected to cables
	 * > How many cables surround a single tile
	 * > The types of cables in the network
	 */
	//Use a HashSet because it eleminates duplicates and is faster than Arrays with Graph Structures
	public Set<AbstractCableTile<?>> cables = new HashSet<>();
	public Set<BlockEntity> connected = new HashSet<>();
	public Map<BlockEntity, Set<Direction>> dirsPerConnectionMap = new HashMap<>();
	public Map<ICableType, Set<AbstractCableTile<?>>> cableTypes = new HashMap<>();
	public boolean fixed;

	public AbstractCableNetwork(List<? extends AbstractCableTile<?>> varCables) {
		cables.addAll(varCables);
		CableNetworkRegistry.register(this);
	}

	public AbstractCableNetwork(Collection<? extends AbstractCableNetwork> networks) {
		for (AbstractCableNetwork network : networks) {
			if (network != null) {
				cables.addAll(network.cables);
				network.deregister();
			}
		}
		refresh();
		CableNetworkRegistry.register(this);
	}

	//Refresh the network without modifying it
	public void refresh() {
		//Step 1. Check what cables are still valid
		connected.clear();
		dirsPerConnectionMap.clear();
		List<AbstractCableTile<?>> currCables = new ArrayList<>(cables);
		List<AbstractCableTile<?>> toRemove = new ArrayList<>();
		for(AbstractCableTile<?> cable : currCables) {
			if (cable == null || cable.isRemoved()) {
				toRemove.add(cable);
			} else {
				cable.setNetwork(this);
			}
		}
		currCables.removeAll(toRemove);
		//Step 2. Store the surrounding connected cables
		for (AbstractCableTile<?> cable : cables) {
			for (Direction direction : Direction.values()) {
				BlockEntity adjacent = cable.getLevel().getBlockEntity(cable.getBlockPos().relative(direction));
				if (adjacent != null && !isCable(adjacent) && canConnect(adjacent, direction)) {
					connected.add(adjacent);
					Set<Direction> directions = dirsPerConnectionMap.getOrDefault(adjacent, new HashSet<>());
					directions.add(direction.getOpposite());
					dirsPerConnectionMap.put(adjacent, directions);
				}
			}
		}
		//Step 3. Sort the cables into their respective types
		sortCables();
	}

	public void sortCables() {
		cableTypes.clear();
		for (ICableType type : getConductorTypes()) {
			cableTypes.put(type, new HashSet<>());
		}
		for (AbstractCableTile<?> wire : cables) {
			cableTypes.get(wire.getConductorType()).add(wire);
		}
	}

	//TODO implement
	// split the network when a cable is removed
	public void split(@Nonnull AbstractCableTile<?> splitPoint) {
			
		
	}

	//TODO implement
	// combine cable networks when more are placed
	public void merge(AbstractCableNetwork network) {
		
	}

	//remove cable from the network and update it
	public void removeFromNetwork(AbstractCableTile<?> conductor) {
		cables.remove(conductor);
		if (cables.isEmpty()) {
			deregister();
		}
	}

	//Clear out all cables before 
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

	public abstract ICableType[] getConductorTypes();

}