package matteroverdrive.core.packet.type.serverbound;

import java.util.function.Supplier;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketCancelReplication {

	private final BlockPos replicatorPos;
	private final int index;

	public PacketCancelReplication(BlockPos replicatorPos, int index) {
		this.replicatorPos = replicatorPos;
		this.index = index;
	}

	public static void handle(PacketCancelReplication message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().getLevel();
			if (world != null) {
				BlockEntity entity = world.getBlockEntity(message.replicatorPos);
				if (entity != null && entity instanceof TileMatterReplicator replicator) {
					try {
						replicator.orderManager.cancelOrder(message.index);
					} catch (Exception e) {
						MatterOverdrive.LOGGER
								.warn("Attempted to remove order from " + message.replicatorPos.toShortString()
										+ " at index " + message.index + " and failed!");
					}
				}
			}
		});
	}

	public static void encode(PacketCancelReplication pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.replicatorPos);
		buf.writeInt(pkt.index);
	}

	public static PacketCancelReplication decode(FriendlyByteBuf buf) {
		return new PacketCancelReplication(buf.readBlockPos(), buf.readInt());
	}

}
