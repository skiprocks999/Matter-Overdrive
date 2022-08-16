package matteroverdrive.core.property.message;

import com.google.common.collect.Lists;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.core.property.IPropertyManaged;
import matteroverdrive.core.property.PropertyManager;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.PropertyTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Supplier;

public class UpdateClientEntityPropertyMessage {

  private final int entityId;
  private final List<Triple<PropertyType<?>, Short, Object>> updates;

  public UpdateClientEntityPropertyMessage(int entityId, List<Triple<PropertyType<?>, Short, Object>> updates) {
    this.entityId = entityId;
    this.updates = updates;
  }

  public void encode(FriendlyByteBuf packetBuffer) {
    packetBuffer.writeInt(entityId);
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

  public boolean consume(Supplier<NetworkEvent.Context> contextSupplier) {
    contextSupplier.get().enqueueWork(() -> {
      LocalPlayer playerEntity = Minecraft.getInstance().player;
      if (playerEntity != null) {
        Entity entity = playerEntity.level.getEntity(entityId);
        if (entity instanceof IPropertyManaged managed) {
          PropertyManager propertyManager = managed.getPropertyManager();
          for (Triple<PropertyType<?>, Short, Object> update: updates) {
            propertyManager.update(update.getLeft(), update.getMiddle(), update.getRight());
          }
        } else {
          MatterOverdrive.LOGGER.info("Entity is not instance of IPropertyManaged");
        }
      }
    });
    return true;
  }

  public static UpdateClientEntityPropertyMessage decode(FriendlyByteBuf packetBuffer) {
    int entityUUID = packetBuffer.readInt();
    short updateAmount = packetBuffer.readShort();
    List<Triple<PropertyType<?>, Short, Object>> updates = Lists.newArrayList();
    for (short i = 0; i < updateAmount; i++) {
      PropertyType<?> propertyType = PropertyTypes.getByIndex(packetBuffer.readShort());
      short propertyLocation = packetBuffer.readShort();
      Object object = propertyType.getReader().apply(packetBuffer);
      updates.add(Triple.of(propertyType, propertyLocation, object));
    }
    return new UpdateClientEntityPropertyMessage(entityUUID, updates);
  }

}