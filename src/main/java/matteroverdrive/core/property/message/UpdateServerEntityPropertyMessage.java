package matteroverdrive.core.property.message;

import matteroverdrive.core.property.IPropertyManaged;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.PropertyTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateServerEntityPropertyMessage {
	private final int entityId;
	private final PropertyType<?> propertyType;
	private final short property;
	private final Object value;

	public UpdateServerEntityPropertyMessage(int entityId, PropertyType<?> propertyType, short property, Object value) {
		this.entityId = entityId;
		this.propertyType = propertyType;
		this.property = property;
		this.value = value;
	}

	public static UpdateServerEntityPropertyMessage decode(FriendlyByteBuf packetBuffer) {
		int entityId = packetBuffer.readInt();
		PropertyType<?> propertyType = PropertyTypes.getByIndex(packetBuffer.readShort());
		short property = packetBuffer.readShort();
		Object value = propertyType.getReader().apply(packetBuffer);
		return new UpdateServerEntityPropertyMessage(entityId, propertyType, property, value);
	}

	public void encode(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeInt(entityId);
		packetBuffer.writeShort(PropertyTypes.getIndex(propertyType));
		packetBuffer.writeShort(property);
		propertyType.attemptWrite(packetBuffer, value);
	}

	public boolean consume(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player playerEntity = contextSupplier.get().getSender();
			if (playerEntity != null) {
				Entity entity = playerEntity.level.getEntity(entityId);
				if (entity instanceof IPropertyManaged managed) {
					managed.getPropertyManager().update(propertyType, property, value);
				}
			}
		});
		return true;
	}
}
