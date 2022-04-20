package matteroverdrive.core.packet.type;

import java.util.function.Supplier;

import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.tile.utils.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketUpdateTile {

	private final CompoundTag updateTag;
	private final BlockPos pos;
	private final boolean isGui;

	public PacketUpdateTile(PacketHandler component, BlockPos pos, CompoundTag base, boolean isGui) {
		this(pos, base, isGui);
		if (component.getPacketSupplier() != null) {
			component.getPacketSupplier().accept(base);
		}
	}

	private PacketUpdateTile(BlockPos pos, CompoundTag updateTag, boolean isGui) {
		this.pos = pos;
		this.updateTag = updateTag;
		this.isGui = isGui;
	}

	public static void handle(PacketUpdateTile message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ClientLevel world = Minecraft.getInstance().level;
			if (world != null) {
				BlockEntity tile = world.getBlockEntity(message.pos);
				if (tile instanceof GenericTile generic) {
					if (generic.hasMenuPacketHandler && message.isGui) {
						PacketHandler handler = generic.getMenuPacketHandler();
						if (handler.getPacketConsumer() != null) {
							handler.getPacketConsumer().accept(message.updateTag);
						}
					} else if (generic.hasRenderPacketHandler && !message.isGui) {
						PacketHandler handler = generic.getRenderPacketHandler();
						if (handler.getPacketConsumer() != null) {
							handler.getPacketConsumer().accept(message.updateTag);
						}
					}
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateTile pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeNbt(pkt.updateTag);
		buf.writeBoolean(pkt.isGui);
	}

	public static PacketUpdateTile decode(FriendlyByteBuf buf) {
		return new PacketUpdateTile(buf.readBlockPos(), buf.readNbt(), buf.readBoolean());
	}

}
