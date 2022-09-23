package matteroverdrive.core.packet.type.serverbound.misc;

import java.util.function.Supplier;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketCancelReplication extends AbstractOverdrivePacket<PacketCancelReplication> {

	private final BlockPos replicatorPos;
	private final int index;

	public PacketCancelReplication(BlockPos replicatorPos, int index) {
		this.replicatorPos = replicatorPos;
		this.index = index;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().getLevel();
			if (world != null) {
				BlockEntity entity = world.getBlockEntity(replicatorPos);
				if (entity != null && entity instanceof TileMatterReplicator replicator) {
					try {
						replicator.orderManager.cancelOrder(index);
					} catch (Exception e) {
						MatterOverdrive.LOGGER
								.warn("Attempted to remove order from " + replicatorPos.toShortString()
										+ " at index " + index + " and failed!");
					}
				}
			}
		});
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(replicatorPos);
		buf.writeInt(index);
	}

	public static PacketCancelReplication decode(FriendlyByteBuf buf) {
		return new PacketCancelReplication(buf.readBlockPos(), buf.readInt());
	}

}
