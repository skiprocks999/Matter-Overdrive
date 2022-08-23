package matteroverdrive.core.android.api.perk;

import com.google.common.collect.Multimap;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IAndroidPerk {

  public static Map<String, IAndroidPerk> PERKS = new HashMap<>();

  String getName();

  boolean onAndroidTick(ICapabilityAndroid player, int statLevel);

  void onUnlock(ICapabilityAndroid player, int statLevel);

  void onUnlearn(ICapabilityAndroid player, int statLevel);

  boolean canBeUnlocked(ICapabilityAndroid player, int statLevel);

  boolean showOnPlayerHUD(ICapabilityAndroid player, int statLevel);

  int getMaxLevel();

  int getRequiredXP(ICapabilityAndroid player, int statLevel);

  MutableComponent getDisplayName(ICapabilityAndroid player, int statLevel);

  Multimap<Attribute, AttributeModifier> getAttributeModifiers(ICapabilityAndroid player, int stateLevel);

  List<ItemStack> getRequiredItems();

  @Nullable
  IAndroidPerk getParent();

  void setParent(IAndroidPerk perk);

  @OnlyIn(Dist.CLIENT)
  void onKeyPress(ICapabilityAndroid player, int statLevel, int key, boolean down);

  ResourceLocation getIcon();

  @OnlyIn(Dist.CLIENT)
  Point getAndroidStationLocation();

  List<IAndroidPerk> getChild();

  boolean canBeToggled();

}
