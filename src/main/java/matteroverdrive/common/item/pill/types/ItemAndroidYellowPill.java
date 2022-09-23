package matteroverdrive.common.item.pill.types;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.item.pill.ItemAndroidPill;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAndroidYellowPill extends ItemAndroidPill {
  public ItemAndroidYellowPill(Properties properties, Colors pillColor, boolean hasShiftTip) {
    super(properties, pillColor, hasShiftTip);
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
