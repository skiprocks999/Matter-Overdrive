package matteroverdrive.core.cable.types.matter_network;

public record MatterNetworkEMPack(int fe, double matter) {
	
	public static final MatterNetworkEMPack EMPTY = new MatterNetworkEMPack(0, 0);
	
	public MatterNetworkEMPack {
		
	}
	
}
