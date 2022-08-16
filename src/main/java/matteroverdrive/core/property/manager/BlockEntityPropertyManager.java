package matteroverdrive.core.property.manager;

import com.google.common.collect.Lists;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyManager;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.property.message.UpdateClientBlockEntityPropertyMessage;
import matteroverdrive.core.property.message.UpdateServerBlockEntityPropertyMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class BlockEntityPropertyManager extends PropertyManager {

  /**
   * The {@link BlockPos} of the {@link BlockEntity}.
   */
  private final BlockPos blockPos;

  /**
   * BlockEntity PropertyManager Constructor
   *
   * @param blockPos The BlockPos of the BlockEntity
   */
  public BlockEntityPropertyManager(BlockPos blockPos) {
    super(Lists.newArrayList());
    this.blockPos = blockPos;
  }

  /**
   * Client -> Server update method.
   *
   * @param property Property to update.
   * @param value The value to update the property with.
   * @param <T> The T value type.
   */
  public <T> void updateServerBlockEntity(Property<T> property, T value) {
    short propertyId = -1;
    for (short i = 0; i < properties.size(); i++) {
      if (properties.get(i) == property) {
        propertyId = i;
      }
    }
    property.set(value);
    NetworkHandler.sendUpdateServerBlockEntityProperties(new UpdateServerBlockEntityPropertyMessage(blockPos, property.getPropertyType(), propertyId, value));
  }

  /**
   * Server -> Client update method.
   *
   * @param blockPos The {@link BlockPos} of the {@link BlockEntity} in the world.
   */
  public void sendBlockEntityChanges(ServerPlayer player, BlockPos blockPos) {
    List<Triple<PropertyType<?>, Short, Object>> dirtyProperties = Lists.newArrayList();
    for (short i = 0; i < properties.size(); i++) {
      Property<?> property = properties.get(i);
      if (property.isDirty()) {
        dirtyProperties.add(Triple.of(property.getPropertyType(), i, property.get()));
      }
    }

    if (!dirtyProperties.isEmpty()) {
      NetworkHandler.sendUpdateClientBlockEntityProperties(player, new UpdateClientBlockEntityPropertyMessage(blockPos, dirtyProperties));
    }
  }
}
