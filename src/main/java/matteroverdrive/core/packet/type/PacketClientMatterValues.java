package matteroverdrive.core.packet.type;

import java.util.HashMap;
import java.util.function.Supplier;

import matteroverdrive.core.matter.MatterRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketClientMatterValues {

	private final HashMap<Item, Integer> values;

	public PacketClientMatterValues(HashMap<Item, Integer> values) {
		this.values = values;
	}

	public static void handle(PacketClientMatterValues message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
				MatterRegister.INSTANCE.setClientValues(message.values);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketClientMatterValues pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.values.size());
		pkt.values.forEach((item, val) -> {
			buf.writeItem(new ItemStack(item));
			buf.writeInt(val);
		});
	}

	public static PacketClientMatterValues decode(FriendlyByteBuf buf) {
		HashMap<Item, Integer> vals = new HashMap<>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			vals.put(buf.readItem().getItem(), buf.readInt());
		}
		return new PacketClientMatterValues(vals);
	}

}
