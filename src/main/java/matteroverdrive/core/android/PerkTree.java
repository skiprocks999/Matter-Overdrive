package matteroverdrive.core.android;

import com.google.common.collect.ImmutableMultimap;
import matteroverdrive.core.android.item.ItemAndroidPill;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.android.AndroidEnergy;
import matteroverdrive.core.eventhandler.manager.EventManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;

import java.awt.*;
import java.util.Collections;
import java.util.UUID;

public class PerkTree {

  //NANOBOTS - Implemented
  //ATTACK BOOST - Implemented
  //FLASH COOLING - Waiting for guns
  //SONIC SHOCKWAVE -
  //NANO ARMOR - Implemented
  //PLASMA SHIELD -
  //EMERGENCY SHIELD -
  //CLOAK -
  //ZERO CALORIES - Implemented
  //RESPIROCYTES - Implemented
  //AIR BAGS -
  //NIGHT VISION - Implemented

  public static BasePerkBuilder NANONOTS = new BasePerkBuilder("nanobots")
          .point(new Point(0, 0))
          .xpNeeded(26)
          .maxLevel(4)
          .canShowOnHUD((iAndroid, integer) -> true)
          .onAndroidTick((iAndroid, integer) -> {
            if (iAndroid.getHolder().getLevel().getGameTime() % 20 == 0 && iAndroid.getHolder().getHealth() < iAndroid.getHolder().getMaxHealth()) {
              return iAndroid.getHolder().getCapability(CapabilityEnergy.ENERGY).map(energyStorage -> {
                if (energyStorage.getEnergyStored() >= 1048) {
                  iAndroid.getHolder().heal(1);
                  energyStorage.extractEnergy(1048, false);
                  if (iAndroid.getHolder() instanceof ServerPlayer player)
                    AndroidEnergy.syncEnergy(player);
                  return true;
                }
                return false;
              }).orElse(false);
            }
            return false;
          })
          .attributeModifierMultimap((iAndroid, integer) -> ImmutableMultimap.of(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a0"), "Nanobots", 5 * integer, AttributeModifier.Operation.ADDITION)))
          .child(new BasePerkBuilder("attack_boost")
                  .maxLevel(4)
                  .xpNeeded(30)
                  .attributeModifierMultimap((iAndroid, integer) -> ImmutableMultimap.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a1"), "Attack Boost", 1 + 0.05 + 0.05 * integer, AttributeModifier.Operation.MULTIPLY_TOTAL)))
                  .child(new BasePerkBuilder("flash_cooling")
                          .xpNeeded(28)
                          .child(new BasePerkBuilder("sonic_shockwave")
                                  .xpNeeded(32)
                          )
                  )
          )
          .child(new BasePerkBuilder("nano_armor")
                  .maxLevel(4)
                  .xpNeeded(30)
                  .canShowOnHUD((iAndroid, integer) -> true)
                  .attributeModifierMultimap((iAndroid, integer) -> ImmutableMultimap.of(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a2"), "Nano Armor", 1 + 0.12 * integer, AttributeModifier.Operation.MULTIPLY_TOTAL)))
                  .child(new BasePerkBuilder("plasma_shield")
                          .xpNeeded(36)
                          .requiredItems(Collections.singletonList(new ItemStack(Items.SHIELD)))
                          .child(new BasePerkBuilder("emergency_shield")
                                  .xpNeeded(26)
                          )
                          .child(new BasePerkBuilder("cloak")
                                  .xpNeeded(36)
                                  .onAndroidTick((iAndroid, integer) -> {
                                    iAndroid.getHolder().getCapability(CapabilityEnergy.ENERGY).ifPresent(iEnergyStorage -> iEnergyStorage.extractEnergy(128, false));
                                    return false;
                                  })
                          )
                  )
          );

  public static BasePerkBuilder RESPIROCYTES;

  public static BasePerkBuilder ZERO_CALORIES = new BasePerkBuilder("zero_calories")
          .point(new Point(0, 3))
          .xpNeeded(18)
          .canShowOnHUD((iAndroid, integer) -> true)
          .onAndroidTick((iAndroid, integer) -> {
            if (iAndroid.getHolder() instanceof ServerPlayer player) {
              if (player.getFoodData().getFoodLevel() < 8) {
                player.getFoodData().setFoodLevel(10);
                return true;
              }
              if (player.getFoodData().getFoodLevel() > 12) {
                player.getFoodData().setFoodLevel(10);
                return false;
              }
            }
            return false;
          })
          .child(RESPIROCYTES = new BasePerkBuilder("respirocytes")
                  .xpNeeded(12)
                  .canShowOnHUD((iAndroid, integer) -> true)
                  .onAndroidTick((iAndroid, integer) -> {
                    if (iAndroid.getHolder().getAirSupply() < iAndroid.getHolder().getMaxAirSupply()) {
                      iAndroid.getHolder().setAirSupply(iAndroid.getHolder().getMaxAirSupply());
                      return true;
                    }
                    return false;
                  })
                  .child(new BasePerkBuilder("air_bags")
                          .xpNeeded(14)
                  )
          );

  public static BasePerkBuilder NIGHT_VISION;

  public static void poke() {
    NIGHT_VISION = new BasePerkBuilder("night_vision")
            .point(new Point(5, 3))
            .xpNeeded(28)
            .canToggle()
            .canShowOnHUD((iAndroid, integer) -> iAndroid.getPerkManager().hasPerkEnabled(NIGHT_VISION))
            .onAndroidTick((iAndroid, integer) -> {
              if (iAndroid.getPerkManager().hasPerkEnabled(NIGHT_VISION)) {
                iAndroid.getHolder().getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
                  if (energyStorage.getEnergyStored() >= 16) {
                    energyStorage.extractEnergy(16, false);
                    iAndroid.getHolder().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0, true, false));
                    if (iAndroid.getHolder() instanceof ServerPlayer player)
                      AndroidEnergy.syncEnergy(player);
                  }
                });
                return true;
              } else {
                iAndroid.getHolder().removeEffect(MobEffects.NIGHT_VISION);
              }
              return false;
            });


    //Cancel healing if they're an android.
    EventManager.forge(LivingHealEvent.class).process(livingHealEvent -> {
      livingHealEvent.getEntity().getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> livingHealEvent.setCanceled(true));
    }).subscribe();

    // Stop Androids from eating if they have the "ZERO_CALORIES" perk.
    EventManager.forge(LivingEntityUseItemEvent.Start.class).process(livingEntityUseItemEvent -> {
      livingEntityUseItemEvent.getEntity().getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> {
        if (iAndroid.getPerkManager().hasPerk(ZERO_CALORIES) && !(livingEntityUseItemEvent.getItem().getItem() instanceof ItemAndroidPill)) {
          livingEntityUseItemEvent.setCanceled(true);
          livingEntityUseItemEvent.setDuration(0);
        }
      });
    }).subscribe();
  }

}