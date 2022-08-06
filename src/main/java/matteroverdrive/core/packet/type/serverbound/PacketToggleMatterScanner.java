package matteroverdrive.core.packet.type.serverbound;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketToggleMatterScanner {

	private final UUID id;
	private final InteractionHand hand;

	public PacketToggleMatterScanner(UUID id, InteractionHand hand) {
		this.id = id;
		this.hand = hand;
	}

	public static void handle(PacketToggleMatterScanner message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().getLevel();
			if (world != null) {
				Player player = world.getPlayerByUUID(message.id);
				if (player != null) {
					ItemStack stack = player.getItemInHand(message.hand);
					if (!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner scanner) {
						boolean newMode = !stack.getOrCreateTag().getBoolean(UtilsNbt.ON);
						stack.getOrCreateTag().putBoolean(UtilsNbt.ON, newMode);
						if (!newMode) {
							scanner.setNotHolding(stack);
						}
					}
				}
			}
		});
	}

	public static void encode(PacketToggleMatterScanner pkt, FriendlyByteBuf buf) {
		buf.writeUUID(pkt.id);
		buf.writeEnum(pkt.hand);
	}

	public static PacketToggleMatterScanner decode(FriendlyByteBuf buf) {
		return new PacketToggleMatterScanner(buf.readUUID(), buf.readEnum(InteractionHand.class));
	}

}
