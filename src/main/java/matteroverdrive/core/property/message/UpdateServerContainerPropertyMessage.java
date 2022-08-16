package matteroverdrive.core.property.message;

import matteroverdrive.core.property.IPropertyManaged;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.PropertyTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateServerContainerPropertyMessage {
  private final short windowId;
  private final PropertyType<?> propertyType;
  private final short property;
  private final Object value;

  public UpdateServerContainerPropertyMessage(short windowId, PropertyType<?> propertyType, short property,
                                              Object value) {
    this.windowId = windowId;
    this.propertyType = propertyType;
    this.property = property;
    this.value = value;
  }

  public static UpdateServerContainerPropertyMessage decode(FriendlyByteBuf packetBuffer) {
    short windowId = packetBuffer.readShort();
    PropertyType<?> propertyType = PropertyTypes.getByIndex(packetBuffer.readShort());
    short property = packetBuffer.readShort();
    Object value = propertyType.getReader().apply(packetBuffer);
    return new UpdateServerContainerPropertyMessage(windowId, propertyType, property, value);
  }

  public void encode(FriendlyByteBuf packetBuffer) {
    packetBuffer.writeShort(windowId);
    packetBuffer.writeShort(PropertyTypes.getIndex(propertyType));
    packetBuffer.writeShort(property);
    propertyType.attemptWrite(packetBuffer, value);
  }

  public boolean consume(Supplier<NetworkEvent.Context> contextSupplier) {
    contextSupplier.get().enqueueWork(() -> {
      Player playerEntity = contextSupplier.get().getSender();
      if (playerEntity != null) {
        AbstractContainerMenu container = playerEntity.containerMenu;
        if (container.containerId == windowId) {
          if (container instanceof IPropertyManaged) {
            ((IPropertyManaged) container).getPropertyManager().update(propertyType, property, value);
          }
        }
      }
    });
    return true;
  }
}
