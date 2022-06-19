package matteroverdrive.core.network.utils;

import matteroverdrive.common.network.NetworkMatter;
import net.minecraft.core.Direction;

public interface IMatterNetworkMember {
	
	boolean canConnectToFace(Direction face);
	
	NetworkMatter getConnectedNetwork();

}
