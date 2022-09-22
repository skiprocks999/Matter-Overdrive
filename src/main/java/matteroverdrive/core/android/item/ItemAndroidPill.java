package matteroverdrive.core.android.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class ItemAndroidPill extends Item implements IHasColor {

  // For eating or internal damage
  public static final DamageSource NANITES = new DamageSource("nanites").bypassArmor();

  public static final FoodProperties PILLS = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F).alwaysEat().fast().build();

  private final int pillColor;

  private final int pillWhite = TextColor.fromLegacyFormat(ChatFormatting.WHITE).getValue();

  public ItemAndroidPill(Properties properties, TextColor pillColor) {
    super(properties);
    this.pillColor = pillColor.getValue();
  }

  @Override
  public int getColor(int i) {
    return i == 0 ? pillWhite : this.pillColor;
  }
}
