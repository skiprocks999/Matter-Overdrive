package matteroverdrive.core.packet.type.serverbound.property;

import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.core.property.IPropertyManaged;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.PropertyTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateServerTileProperty extends AbstractOverdrivePacket<PacketUpdateServerTileProperty> {

	private final BlockPos blockPos;
	private final PropertyType<?> propertyType;
	private final short property;
	private final Object value;

	public PacketUpdateServerTileProperty(BlockPos blockPos, PropertyType<?> propertyType, short property,
			Object value) {
		this.blockPos = blockPos;
		this.propertyType = propertyType;
		this.property = property;
		this.value = value;
	}

	public static PacketUpdateServerTileProperty decode(FriendlyByteBuf packetBuffer) {
		BlockPos pos = packetBuffer.readBlockPos();
		PropertyType<?> propertyType = PropertyTypes.getByIndex(packetBuffer.readShort());
		short property = packetBuffer.readShort();
		Object value = propertyType.getReader().apply(packetBuffer);
		return new PacketUpdateServerTileProperty(pos, propertyType, property, value);
	}

	@Override
	public void encode(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeBlockPos(blockPos);
		packetBuffer.writeShort(PropertyTypes.getIndex(propertyType));
		packetBuffer.writeShort(property);
		propertyType.attemptWrite(packetBuffer, value);
	}

	@Override
	public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player playerEntity = contextSupplier.get().getSender();
			if (playerEntity != null) {
				BlockEntity entity = playerEntity.level.getBlockEntity(blockPos);
				if (entity instanceof IPropertyManaged managed) {
					managed.getPropertyManager().update(propertyType, property, value);
				}
			}
		});
		return true;
	}

}
