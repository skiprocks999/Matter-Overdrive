package matteroverdrive.core.packet.type.clientbound.property;

import com.google.common.collect.Lists;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.packet.type.clientbound.PacketBarrierMethods;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.PropertyTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateClientTileProperty extends AbstractOverdrivePacket<PacketUpdateClientTileProperty> {

	private final BlockPos blockPos;
	private final List<Triple<PropertyType<?>, Short, Object>> updates;

	public PacketUpdateClientTileProperty(BlockPos blockPos,
			List<Triple<PropertyType<?>, Short, Object>> updates) {
		this.blockPos = blockPos;
		this.updates = updates;
	}

	@Override
	public void encode(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeBlockPos(blockPos);
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
			PacketBarrierMethods.handlePacketUpdateClientTileProperties(updates, blockPos);
		});
		return true;
	}

	public static PacketUpdateClientTileProperty decode(FriendlyByteBuf packetBuffer) {
		BlockPos pos = packetBuffer.readBlockPos();
		short updateAmount = packetBuffer.readShort();
		List<Triple<PropertyType<?>, Short, Object>> updates = Lists.newArrayList();
		for (short i = 0; i < updateAmount; i++) {
			PropertyType<?> propertyType = PropertyTypes.getByIndex(packetBuffer.readShort());
			short propertyLocation = packetBuffer.readShort();
			Object object = propertyType.getReader().apply(packetBuffer);
			updates.add(Triple.of(propertyType, propertyLocation, object));
		}
		return new PacketUpdateClientTileProperty(pos, updates);
	}

}
