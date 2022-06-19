package matteroverdrive.core.packet.type;

import java.util.function.Supplier;

import matteroverdrive.common.tile.matter_network.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketClientMNData {

	private final CompoundTag data;
	
	public PacketClientMNData(CompoundTag data) {
		this.data = data;
	}
	
	public static void handle(PacketClientMNData message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			Level world = Minecraft.getInstance().level;
			if(world != null) {
				CompoundTag data = message.data;
				BlockEntity entity;
				for(int i = 0; i < data.getInt("drivesize"); i++) {
					entity = world.getBlockEntity(NbtUtils.readBlockPos(data.getCompound("drivepos" + i)));
					if(entity != null && entity instanceof TilePatternStorage storage) {
						storage.handleNetworkData(data.getCompound("drivedata" + i));
						data.remove("drivepos" + i);
						data.remove("drivedata" + i);
					}
				}
				for(int i = 0; i < data.getInt("replicatorsize"); i++) {
					entity = world.getBlockEntity(NbtUtils.readBlockPos(data.getCompound("reppos" + i)));
					if(entity != null && entity instanceof TileMatterReplicator replicator) {
						replicator.handleNetworkData(data.getCompound("repdata" + i));
						data.remove("reppos" + i);
						data.remove("repdata" + i);
					}
				}
			}
		});
		ctx.setPacketHandled(true);
	}
	
	public static void encode(PacketClientMNData pkt, FriendlyByteBuf buf) {
		buf.writeNbt(pkt.data);
	}
	
	public static PacketClientMNData decode(FriendlyByteBuf buf) {
		return new PacketClientMNData(buf.readNbt());
	}
	
}
