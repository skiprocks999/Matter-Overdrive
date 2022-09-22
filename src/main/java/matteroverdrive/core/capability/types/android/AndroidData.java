package matteroverdrive.core.capability.types.android;

import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.android.api.perk.AndroidPerkManager;
import matteroverdrive.core.android.api.perk.IAndroidPerk;
import matteroverdrive.core.android.packet.s2c.PacketAndroidSyncAll;
import matteroverdrive.core.android.packet.s2c.PacketAndroidTurningTimeSync;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.sound.MatterOverdriveSounds;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AndroidData implements ICapabilityAndroid, ICapabilityProvider {
  private final LazyOptional<ICapabilityAndroid> holder = LazyOptional.of(() -> this);


  /**
   * The time it takes for the Android Transformation animation to take.
   * By default, it's 630 ticks or 31.5 seconds to finish.
   */
  public static final int TURNING_TIME = 630;

  private static final String IS_ANDROID_NBT = "isAndroid";
  private static final String TRANSFORMATION_TIME_NBT = "transformationTime";

  private boolean isAndroid;
  private int transformationTime;
  private boolean needsUpdate;
  private LivingEntity entity;
  private AndroidPerkManager perkManager;

  public AndroidData() {
    this.isAndroid = false;
    this.transformationTime = 0;
    this.needsUpdate = false;
    this.perkManager = new AndroidPerkManager();
  }

  public boolean isAndroid() {
    return this.isAndroid;
  }

  public void setAndroid(boolean android) {
    this.isAndroid = android;
  }

  @Override
  public boolean isTurning() {
    return this.transformationTime > 0 && !this.isAndroid;
  }

  public int getTurningTime() {
    return this.transformationTime;
  }

  public void setTurningTime(int turningTime) {
    this.transformationTime = turningTime;
  }

  @Override
  public void requestUpdate() {
    this.needsUpdate = true;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void tickClient(Entity entity) {
    if (entity instanceof LivingEntity){
      this.entity = (LivingEntity) entity;
    }
    if (isTurning() && transformationTime % 40 == 0) {
      playGlitchSound(entity, entity.level.random, 0.2f);
    }
  }

  private void playGlitchSound(Entity entity, RandomSource random, float amount) {
    if(entity instanceof Player player) {
      player.level.playSound(player, player.blockPosition(), MatterOverdriveSounds.GLITCH.get(), SoundSource.PLAYERS, amount, 0.9f + random.nextFloat() * 0.2f);
    }
  }

  @Override
  public void tickServer(Entity entity) {
    if (entity instanceof LivingEntity){
      this.entity = (LivingEntity) entity;
    }
    tickPerks();
    if (entity instanceof ServerPlayer serverPlayer){
      updatePerkAttributes(serverPlayer);
    }
    if (isTurning()){
      tickTransformationTime(entity);
    }
    if (needsUpdate){
      sync(entity);
    }
  }

  @Override
  public AndroidPerkManager getPerkManager() {
    return perkManager;
  }

  @Override
  public LivingEntity getHolder() {
    return entity;
  }

  public void sync(Entity entity){
    if (entity instanceof ServerPlayer serverEntity){
      NetworkHandler.sendToPlayer(new PacketAndroidSyncAll(serializeNBT()), serverEntity);
      this.needsUpdate = false;
    }
  }

  public void updatePerkAttributes(ServerPlayer player){
    for (String perk : this.getPerkManager().getOwned().keySet()) {
      if (IAndroidPerk.PERKS.containsKey(perk)){
        IAndroidPerk androidPerk = IAndroidPerk.PERKS.get(perk);
        if (!androidPerk.canBeToggled() || this.getPerkManager().hasPerkEnabled(androidPerk)){
          player.getAttributes().addTransientAttributeModifiers(androidPerk.getAttributeModifiers(this, this.getPerkManager().getLevel(androidPerk)));
        }
      }
    }
  }

  public void tickPerks(){
    boolean somethingUpdated = false;
    for (String perk : this.getPerkManager().getOwned().keySet()) {
      if (IAndroidPerk.PERKS.containsKey(perk)) {
        IAndroidPerk androidPerk = IAndroidPerk.PERKS.get(perk);
        if (androidPerk.onAndroidTick(this, this.getPerkManager().getLevel(androidPerk)) && androidPerk.showOnPlayerHUD(this, this.getPerkManager().getLevel(androidPerk))) {
          this.getPerkManager().getPerkActivityTracker().put(perk, this.getHolder().level.getGameTime());
          somethingUpdated = true;
        }
        ;
      }
    }
    if (somethingUpdated) this.requestUpdate();
  }

  private void tickTransformationTime(Entity entity){
    DamageSource fake = new DamageSource("android_transformation");
    fake.bypassInvul();
    fake.bypassArmor();
    --transformationTime;
    if (entity instanceof ServerPlayer serverPlayer){
      NetworkHandler.sendToPlayer(new PacketAndroidTurningTimeSync(transformationTime), serverPlayer);
    }
    if (entity instanceof LivingEntity){
      if (transformationTime > 0) {
        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, false));
        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.HUNGER, 20, 0, false, false));
        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, false, false));
        if (transformationTime % 40 == 0) {
          entity.hurt(fake, 0.1f);
        }
      }
    }
    if (transformationTime <= 0) {
      setAndroid(true);
      entity.getCapability(CapabilityEnergy.ENERGY).ifPresent(iEnergyStorage -> iEnergyStorage.receiveEnergy((int) (AndroidEnergy.DEFAULT_ENERGY * 0.25), false));
      requestUpdate();
      if (entity instanceof Player player && !player.isCreative() && !entity.level.getLevelData().isHardcore()) {
        entity.hurt(fake, Integer.MAX_VALUE);
      }
    }
  }

  @Override
  public CompoundTag serializeNBT() {
    final CompoundTag nbt = new CompoundTag();
    nbt.putBoolean(IS_ANDROID_NBT, this.isAndroid);
    nbt.putInt(TRANSFORMATION_TIME_NBT, this.transformationTime);
    nbt.put("PerkManager", perkManager.serializeNBT());
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    this.isAndroid = nbt.getBoolean(IS_ANDROID_NBT);
    this.transformationTime = nbt.getInt(TRANSFORMATION_TIME_NBT);
    this.perkManager.deserializeNBT(nbt.getCompound("PerkManager"));
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return MatterOverdriveCapabilities.ANDROID_DATA.orEmpty(cap, holder);
  }
}
