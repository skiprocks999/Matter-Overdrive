package matteroverdrive.core.packet.type;

import java.util.function.Supplier;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.tile.TileTransporter;
import matteroverdrive.core.tile.utils.TransporterLocationWrapper;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketUpdateTransporterLocationInfo {

	private final BlockPos transporterPos;
	private final int destinationIndex;
	private final PacketType packetType;
	//optional data
	private String newName = null;
	private BlockPos newDestination = null;
	
	public PacketUpdateTransporterLocationInfo(BlockPos transporterPos, int destinationIndex, PacketType packetType) {
		this.transporterPos = transporterPos;
		this.destinationIndex = destinationIndex;
		this.packetType = packetType;
	}
	
	public PacketUpdateTransporterLocationInfo(BlockPos transporterPos, int destinationIndex, PacketType packetType,
			String newName) {
		this(transporterPos, destinationIndex, packetType);
		this.newName = newName;
	}
	
	public PacketUpdateTransporterLocationInfo(BlockPos transporterPos, int destinationIndex, PacketType packetType,
			BlockPos newDestination) {
		this(transporterPos, destinationIndex, packetType);
		this.newDestination = newDestination;
	}
	
	public static void handle(PacketUpdateTransporterLocationInfo message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = ctx.getSender().getLevel();
			if (world != null) {
				BlockEntity tile = world.getBlockEntity(message.transporterPos);
				if (tile instanceof TileTransporter transporter) {
					switch(message.packetType) {
					case UPDATE_INDEX:
						transporter.setDestination(message.destinationIndex);
						break;
					case RESET_DESTINATION:
						TransporterLocationWrapper wrapper = transporter.getServerLocations()[message.destinationIndex];
						wrapper.setDestination(new BlockPos(0, -1000, 0));
						wrapper.setName(TransporterLocationWrapper.DEFAULT_NAME.getContents());
						break;
					case UPDATE_DESTINATION:
						transporter.getServerLocations()[message.destinationIndex].setDestination(message.newDestination);
						break;
					case UPDATE_NAME:
						transporter.getServerLocations()[message.destinationIndex].setName(message.newName);
						break;
					}
					transporter.setChanged();
				}
			}
		});
		ctx.setPacketHandled(true);
	}
	
	public static void encode(PacketUpdateTransporterLocationInfo pkt, FriendlyByteBuf buf) {
		buf.writeEnum(pkt.packetType);
		buf.writeBlockPos(pkt.transporterPos);
		buf.writeInt(pkt.destinationIndex);
		if(pkt.newName != null) {
			UtilsNbt.writeStringToBuffer(buf, pkt.newName);
		}
		if(pkt.newDestination != null) {
			buf.writeBlockPos(pkt.newDestination);
		}
	}
	
	public static PacketUpdateTransporterLocationInfo decode(FriendlyByteBuf buf) {
		PacketType type = buf.readEnum(PacketType.class);
		BlockPos transporter = buf.readBlockPos();
		int index = buf.readInt();
		switch(type) {
		case UPDATE_INDEX, RESET_DESTINATION:
			return new PacketUpdateTransporterLocationInfo(transporter, index, type);
		case UPDATE_NAME:
			return new PacketUpdateTransporterLocationInfo(transporter, index, type, UtilsNbt.readStringFromBuffer(buf));
		case UPDATE_DESTINATION:
			return new PacketUpdateTransporterLocationInfo(transporter, index, type, buf.readBlockPos());
		}
		MatterOverdrive.LOGGER.info("Something has gone horribly wrong.");
		return null;
	}
	
	public enum PacketType {
		UPDATE_NAME, UPDATE_DESTINATION, UPDATE_INDEX, RESET_DESTINATION;
	}
	
}
