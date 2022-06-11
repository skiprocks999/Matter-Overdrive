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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import matteroverdrive.common.block.cable.ICableType;
import matteroverdrive.common.tile.cable.AbstractCableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
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
	
	// split the network when a cable is removed
	public void split(@Nonnull AbstractCableTile<?> cableToRemove) {
		//Step 1. Remove the cable from the network
		removeFromNetwork(cableToRemove);
		
		//Step 2. Determine the cables surrounding the removed cable
		BlockEntity[] connectedTiles = new BlockEntity[6];
		boolean[] tilesToHandle = { false, false, false, false, false, false };
		Level world = cableToRemove.getLevel();
		BlockPos splitPos = cableToRemove.getBlockPos();
		for (Direction dir : Direction.values()) {
			BlockPos adjacent = splitPos.relative(dir);
			if (world.hasChunkAt(adjacent)) {
				BlockEntity sideTile = world.getBlockEntity(adjacent);
				if (sideTile != null) {
					connectedTiles[dir.ordinal()] = sideTile;
				}
			}
		}
		
		//Step 3. Create networks from the surrounds cables found
		for (int i = 0; i < 6; i++) {
			BlockEntity currentBlock = connectedTiles[i];
			if (currentBlock != null && isCable(currentBlock) && !tilesToHandle[i]) {
				
				// Step 3.1 keep checking adjacent blocks and see if they are cables
				// Store them if they are
				
				AbstractCableTile<?> currentCable = (AbstractCableTile<?>) currentBlock;
				BlockPos currentPos = currentCable.getBlockPos();
				
				List<AbstractCableTile<?>> checked = Arrays.asList(currentCable);
				List<BlockPos> posToIgnore = Arrays.asList(splitPos);
				
				for (Direction dir : Direction.values()) {
					BlockPos adjacent = currentPos.relative(dir);
					if (!posToIgnore.contains(adjacent) && world.hasChunkAt(adjacent)) {
						BlockEntity adjacentTile = world.getBlockEntity(adjacent);
						if (!checked.contains(adjacentTile) && isCable(adjacentTile)) {
							checkSurroundingBlocks((AbstractCableTile<?>) adjacentTile, checked, posToIgnore);
						}
					}
				}
				
				//Check if the blocks we checked included the surrounding tiles we initially 
				//need to check
				
				for (int j = i + 1; j < 6; j++) {
					BlockEntity nextBlock = connectedTiles[j];
					if (isCable(nextBlock) && !tilesToHandle[j] && checked.contains(nextBlock)) {
						tilesToHandle[j] = true;
					}
				}
				
				//safety check
				checked.remove(cableToRemove);
				
				AbstractCableNetwork newNetwork = newInstance(checked);
				newNetwork.refresh();
			}
		}
		
		//Step 4. Remove this network
		deregister();
	}
	
	private void checkSurroundingBlocks(AbstractCableTile<?> cable, List<AbstractCableTile<?>> checked, List<BlockPos> posToIgnore) {
		checked.add(cable);
		for (BlockEntity adjConnected : cable.getAdjacentConnections()) {
			if (adjConnected != null) {
				BlockPos adjPos = adjConnected.getBlockPos();
				//Keep calling recursively until we stop finding cables
				if (!checked.contains(adjConnected) && !posToIgnore.contains(adjPos) && isCable(adjConnected)) {
					checkSurroundingBlocks((AbstractCableTile<?>) adjConnected, checked, posToIgnore);
				}
			}
		}
	}

	// combine cable networks when more are placed
	public void merge(AbstractCableNetwork network) {
		if (network != null && network != this) {
			Set<AbstractCableNetwork> networks = new HashSet<>();
			networks.add(this);
			networks.add(network);
			//need this because abstract classes 
			AbstractCableNetwork newNetwork = newInstance(networks);
			newNetwork.refresh();
		}
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
	
	public abstract AbstractCableNetwork newInstance(List<? extends AbstractCableTile<?>> cables);
	
	public abstract AbstractCableNetwork newInstance(Collection<? extends AbstractCableNetwork> networks);

}