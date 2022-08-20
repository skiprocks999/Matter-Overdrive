package matteroverdrive.core.network.utils;

import matteroverdrive.common.network.NetworkMatter;
import net.minecraft.core.Direction;

public interface IMatterNetworkMember {

	boolean canConnectToFace(Direction face);

	NetworkMatter getConnectedNetwork();

	boolean isPowered(boolean client);

	// Work around for a bug that I cannot seem to solve for now
	default Direction handleEastWest(Direction dir) {
		return dir == Direction.EAST || dir == Direction.WEST ? dir.getOpposite() : dir;
	}

}
