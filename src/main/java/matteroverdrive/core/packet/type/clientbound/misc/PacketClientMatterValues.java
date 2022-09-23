package matteroverdrive.core.packet.type.clientbound.misc;

import java.util.HashMap;
import java.util.function.Supplier;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketClientMatterValues extends AbstractOverdrivePacket<PacketClientMatterValues> {

	private final HashMap<Item, Double> values;

	public PacketClientMatterValues(HashMap<Item, Double> values) {
		this.values = values;
	}

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			PacketBarrierMethods.handlePacketClientMatterValues(values);
		});
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(values.size());
		values.forEach((item, val) -> {
			buf.writeItem(new ItemStack(item));
			buf.writeDouble(val);
		});
	}

	public static  PacketClientMatterValues decode(FriendlyByteBuf buf) {
		HashMap<Item, Double> vals = new HashMap<>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			vals.put(buf.readItem().getItem(), buf.readDouble());
		}
		return new PacketClientMatterValues(vals);
	}

}
