package matteroverdrive.core.android.api;

import matteroverdrive.core.android.api.perk.AndroidPerkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface ICapabilityAndroid extends ICapabilitySerializable<CompoundTag> {

  /**
   * Checks if the {@link net.minecraft.world.entity.Entity} is currently and android or not
   *
   * @return true if the player is an android
   */
  boolean isAndroid();

  /**
   * Sets if the {@link net.minecraft.world.entity.Entity} is currently and android or not
   *
   * @return true if the player is an android
   */
  void setAndroid(boolean android);

  /**
   * Checks if the {@link net.minecraft.world.entity.Entity} is turning into an android
   *
   * @return true if the player is currently turning into an android
   */
  boolean isTurning();

  /**
   * Gets the remaining turning time of an android
   *
   * @return the remaining time
   */
  int getTurningTime();

  /**
   * Sets the remaining turning time of an android
   *
   * @return the remaining time
   */
  void setTurningTime(int time);

  /**
   * Requests for the full capability to be updated to the client
   */
  void requestUpdate();

  /**
   * Ticks the client of an entity
   * @param entity to be ticked
   */
  void tickClient(Entity entity);

  /**
   * Ticks the server of an entity
   * @param entity to be ticked
   */
  void tickServer(Entity entity);

  AndroidPerkManager getPerkManager();

  LivingEntity getHolder();
}
