package matteroverdrive.common.item.pill;

import matteroverdrive.core.android.item.ItemAndroidPill;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.android.AndroidData;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAndroidRedPill extends ItemAndroidPill {
  public ItemAndroidRedPill(Item.Properties properties, TextColor pillColor) {
    super(properties, pillColor);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
    ItemStack itemstack = super.finishUsingItem(stack, worldIn, entityLiving);
    if (!worldIn.isClientSide) {
      if (entityLiving instanceof Player) {
        Player player = (Player) entityLiving;
        player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(androidData -> {
          androidData.setTurningTime(AndroidData.TURNING_TIME);
        });
      } else {
        // In case a fox or something eats it >:)
        entityLiving.hurt(ItemAndroidPill.NANITES, 30);
      }
    }

    return itemstack;
  }
}
