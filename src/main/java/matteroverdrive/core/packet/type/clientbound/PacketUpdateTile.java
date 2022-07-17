package matteroverdrive.core.packet.type.clientbound;

import java.util.function.Supplier;

import matteroverdrive.core.packet.PacketBarrierMethods;
import matteroverdrive.core.tile.utils.IUpdatableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketUpdateTile {

	private final CompoundTag data;
	private final BlockPos pos;
	private final boolean isGui;

	public PacketUpdateTile(BlockPos pos, IUpdatableTile tile, boolean isGui) {
		CompoundTag data = new CompoundTag();
		if(isGui) {
			tile.getMenuData(data);
		} else {
			tile.getRenderData(data);
		}
		this.pos = pos;
		this.data = data;
		this.isGui = isGui;
	}

	private PacketUpdateTile(BlockPos pos, CompoundTag data, boolean isGui) {
		this.pos = pos;
		this.data = data;
		this.isGui = isGui;
	}

	public static void handle(PacketUpdateTile message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketUpdateTile(message.data, message.isGui, message.pos);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateTile pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeNbt(pkt.data);
		buf.writeBoolean(pkt.isGui);
	}

	public static PacketUpdateTile decode(FriendlyByteBuf buf) {
		return new PacketUpdateTile(buf.readBlockPos(), buf.readNbt(), buf.readBoolean());
	}

}
