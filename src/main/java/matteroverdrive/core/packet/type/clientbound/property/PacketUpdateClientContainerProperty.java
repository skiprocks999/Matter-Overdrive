package matteroverdrive.core.packet.type.clientbound.property;

import com.google.common.collect.Lists;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.PropertyTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateClientContainerProperty extends AbstractOverdrivePacket<PacketUpdateClientContainerProperty> {
	private final short windowId;
	private final List<Triple<PropertyType<?>, Short, Object>> updates;

	public PacketUpdateClientContainerProperty(short windowId, List<Triple<PropertyType<?>, Short, Object>> updates) {
		this.windowId = windowId;
		this.updates = updates;
	}

	@Override
	public void encode(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeShort(windowId);
		List<Triple<PropertyType<?>, Short, Object>> validUpdates = Lists.newArrayList();
		for (Triple<PropertyType<?>, Short, Object> update : updates) {
			if (update.getLeft().isValid(update.getRight())) {
				validUpdates.add(update);
			}
		}

		packetBuffer.writeShort(validUpdates.size());
		for (Triple<PropertyType<?>, Short, Object> update : validUpdates) {
			packetBuffer.writeShort(PropertyTypes.getIndex(update.getLeft()));
			packetBuffer.writeShort(update.getMiddle());
			update.getLeft().attemptWrite(packetBuffer, update.getRight());
		}
	}

	@Override
	public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PacketBarrierMethods.handlePacketUpdateClientContainerProperties(updates, windowId);
		});
		return true;
	}

	public static PacketUpdateClientContainerProperty decode(FriendlyByteBuf packetBuffer) {
		short windowId = packetBuffer.readShort();
		short updateAmount = packetBuffer.readShort();
		List<Triple<PropertyType<?>, Short, Object>> updates = Lists.newArrayList();
		for (short i = 0; i < updateAmount; i++) {
			PropertyType<?> propertyType = PropertyTypes.getByIndex(packetBuffer.readShort());
			short propertyLocation = packetBuffer.readShort();
			Object object = propertyType.getReader().apply(packetBuffer);
			updates.add(Triple.of(propertyType, propertyLocation, object));
		}
		return new PacketUpdateClientContainerProperty(windowId, updates);
	}
}
