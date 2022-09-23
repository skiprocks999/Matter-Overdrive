package matteroverdrive.core.packet.type.serverbound.misc;

import java.util.UUID;
import java.util.function.Supplier;

import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketToggleMatterScanner extends AbstractOverdrivePacket<PacketToggleMatterScanner> {

	private final UUID id;
	private final InteractionHand hand;

	public PacketToggleMatterScanner(UUID id, InteractionHand hand) {
		this.id = id;
		this.hand = hand;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = context.get().getSender().getLevel();
			if (world != null) {
				Player player = world.getPlayerByUUID(id);
				if (player != null) {
					ItemStack stack = player.getItemInHand(hand);
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
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(id);
		buf.writeEnum(hand);
	}

	public static  PacketToggleMatterScanner decode(FriendlyByteBuf buf) {
		return new PacketToggleMatterScanner(buf.readUUID(), buf.readEnum(InteractionHand.class));
	}

}
