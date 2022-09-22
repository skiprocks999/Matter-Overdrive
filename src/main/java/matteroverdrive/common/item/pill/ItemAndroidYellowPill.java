package matteroverdrive.common.item.pill;

import matteroverdrive.core.android.item.ItemAndroidPill;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAndroidYellowPill extends ItemAndroidPill {
  public ItemAndroidYellowPill(Properties properties, TextColor pillColor) {
    super(properties, pillColor);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
    ItemStack itemstack = super.finishUsingItem(stack, level, entityLiving);
    if (!level.isClientSide()) {
      entityLiving.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> {
        iAndroid.getPerkManager().getOwned().clear();
        iAndroid.getPerkManager().getEnabled().clear();
        iAndroid.requestUpdate();
      });
    }
    return itemstack;
  }
}
