package matteroverdrive.core.packet.type;

import java.util.function.Supplier;

import matteroverdrive.core.tile.utils.IRedstoneModeTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketUpdateRedstoneMode {

	private final BlockPos pos;

	public PacketUpdateRedstoneMode(BlockPos pos) {
		this.pos = pos;
	}

	public static void handle(PacketUpdateRedstoneMode message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = ctx.getSender().getLevel();
			if (world != null) {
				BlockEntity tile = world.getBlockEntity(message.pos);
				if (tile instanceof IRedstoneModeTile mode) {
					int nextMode = mode.getCurrMod() + 1;
					if (nextMode > mode.getMaxMode()) {
						mode.setMode(0);
					} else {
						mode.setMode(nextMode);
					}
					tile.setChanged();
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateRedstoneMode pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
	}

	public static PacketUpdateRedstoneMode decode(FriendlyByteBuf buf) {
		return new PacketUpdateRedstoneMode(buf.readBlockPos());
	}

}
