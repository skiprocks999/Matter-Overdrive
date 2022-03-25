package matteroverdrive.core.tile;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.core.capability.IOverdriveCapability;
import net.minecraft.core.Direction;

// will extend BlockEntity eventually
public class GenericTile {
	
	private List<IOverdriveCapability> capabilities = new ArrayList<>();
	
	public void addCapability(IOverdriveCapability cap) {
		
	}
	
	//TODO implement
	public Direction getFacing() {
		return Direction.UP;
	}
	
}
