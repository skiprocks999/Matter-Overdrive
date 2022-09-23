package matteroverdrive.core.packet.type.serverbound.misc;

import java.util.function.Supplier;

import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketQueueReplication extends AbstractOverdrivePacket<PacketQueueReplication> {

	private final BlockPos replicatorPos;
	private final ItemPatternWrapper wrapper;
	private final int amt;

	public PacketQueueReplication(BlockPos replicatorPos, ItemPatternWrapper wrapper, int amt) {
		this.replicatorPos = replicatorPos;
		this.wrapper = wrapper;
		this.amt = amt;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().getLevel();
			if (world != null) {
				BlockEntity entity = world.getBlockEntity(replicatorPos);
				if (entity != null && entity instanceof TileMatterReplicator replicator) {
					replicator.orderManager.addOrder(new QueuedReplication(wrapper, amt));

				}
			}
		});
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(replicatorPos);
		wrapper.writeToBuffer(buf);
		buf.writeInt(amt);
	}

	public static  PacketQueueReplication decode(FriendlyByteBuf buf) {
		return new PacketQueueReplication(buf.readBlockPos(), ItemPatternWrapper.readFromBuffer(buf), buf.readInt());
	}

}
