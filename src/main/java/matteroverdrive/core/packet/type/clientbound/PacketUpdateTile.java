package matteroverdrive.core.packet.type.clientbound;

import java.util.function.Supplier;

import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.utils.IUpdatableTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
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
			ClientLevel world = Minecraft.getInstance().level;
			if (world != null) {
				BlockEntity tile = world.getBlockEntity(message.pos);
				if (tile instanceof GenericTile generic) {
					if (message.isGui && generic.hasMenuData) {
						generic.readMenuData(message.data);
					} else if(!message.isGui && generic.hasRenderData) {
						generic.readRenderData(message.data);
					}
				}
			}
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
