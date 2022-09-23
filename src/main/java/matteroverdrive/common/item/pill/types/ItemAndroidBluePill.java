package matteroverdrive.common.item.pill.types;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.item.pill.ItemAndroidPill;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAndroidBluePill extends ItemAndroidPill {
  public ItemAndroidBluePill(Properties properties, Colors pillColor, boolean hasShiftTip) {
    super(properties, pillColor, hasShiftTip);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
    ItemStack itemstack = super.finishUsingItem(stack, level, entityLiving);
    if (!level.isClientSide()) {
      if (entityLiving instanceof Player) {
        Player player = (Player) entityLiving;
        player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(androidData -> {
          androidData.setAndroid(false);
          androidData.getPerkManager().getEnabled().clear();
          androidData.getPerkManager().getOwned().clear();
          DamageSource fake = new DamageSource("android_transformation");
          fake.bypassInvul();
          fake.bypassArmor();
          player.hurt(fake, Integer.MAX_VALUE);
        });
      } else {
        // In case a fox or something eats it >:)
        entityLiving.hurt(ItemAndroidPill.NANITES, 30);
      }
    }
    return itemstack;
  }
}
