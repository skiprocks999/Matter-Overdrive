package matteroverdrive.core.android;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import matteroverdrive.References;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.android.api.perk.IAndroidPerk;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class BaseAndroidPerk implements IAndroidPerk {

  private final String perkName;
  private BiPredicate<ICapabilityAndroid, Integer> onAndroidTick = (androidData, integer) -> true;
  private BiConsumer<ICapabilityAndroid, Integer> onUnlock = (androidData, integer) -> {
  };
  private BiConsumer<ICapabilityAndroid, Integer> onUnlearn = (androidData, integer) -> {
  };
  private BiPredicate<ICapabilityAndroid, Integer> canBeUnLocked = (androidData, integer) -> true;
  private BiPredicate<ICapabilityAndroid, Integer> canShowOnHUD = (androidData, integer) -> false;
  private int maxLevel = 1;
  private int xpNeeded = 0;
  private BiFunction<ICapabilityAndroid, Integer, Multimap<Attribute, AttributeModifier>> attributeModifierMultimap = (iAndroid, integer) -> ImmutableMultimap.of();
  private List<ItemStack> requiredItems = new ArrayList<>();
  private IAndroidPerk parent = null;
  private List<IAndroidPerk> child = new ArrayList<>();
  private Point point = new Point(0, 0);
  private boolean canBeToggled = false;
  private BiConsumer<ICapabilityAndroid, Integer> onClientKeyPress = (androidData, integer) -> {
  };

  public BaseAndroidPerk(String perkName) {
    this.perkName = perkName;
    IAndroidPerk.PERKS.put(perkName, this);
  }

  @Override
  public String getName() {
    return perkName;
  }

  @Override
  public boolean onAndroidTick(ICapabilityAndroid player, int statLevel) {
    return onAndroidTick.test(player, statLevel);
  }

  @Override
  public void onUnlock(ICapabilityAndroid player, int statLevel) {
    onUnlock.accept(player, statLevel);
  }

  @Override
  public void onUnlearn(ICapabilityAndroid player, int statLevel) {
    onUnlearn.accept(player, statLevel);
  }

  @Override
  public boolean canBeUnlocked(ICapabilityAndroid player, int statLevel) {
    return canBeUnLocked.test(player, statLevel) && (getParent() != null && player.getPerkManager().hasPerk(getParent()));
  }

  @Override
  public boolean showOnPlayerHUD(ICapabilityAndroid player, int statLevel) {
    return canShowOnHUD.test(player, statLevel);
  }

  @Override
  public int getMaxLevel() {
    return maxLevel;
  }

  public void setMaxLevel(int maxLevel) {
    this.maxLevel = maxLevel;
  }

  @Override
  public int getRequiredXP(ICapabilityAndroid player, int statLevel) {
    return xpNeeded;
  }

  @Override
  public MutableComponent getDisplayName(ICapabilityAndroid player, int statLevel) {
    return Component.translatable("matteroverdrive.perk." + perkName + ".name");
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ICapabilityAndroid player, int stateLevel) {
    return attributeModifierMultimap.apply(player, stateLevel);
  }

  @Override
  public List<ItemStack> getRequiredItems() {
    return requiredItems;
  }

  public void setRequiredItems(List<ItemStack> requiredItems) {
    this.requiredItems = requiredItems;
  }

  @Nullable
  @Override
  public IAndroidPerk getParent() {
    return parent;
  }

  public void setParent(IAndroidPerk parent) {
    this.parent = parent;
  }

  @Override
  public void onKeyPress(ICapabilityAndroid player, int statLevel, int key, boolean down) {
    onClientKeyPress.accept(player, statLevel);
  }

  public void setOnClientKeyPress(BiConsumer<ICapabilityAndroid, Integer> onClientKeyPress) {
    this.onClientKeyPress = onClientKeyPress;
  }

  @Override
  public ResourceLocation getIcon() {
    return new ResourceLocation(References.ID, "textures/gui/perk/biotic_stat_" + perkName + ".png");
  }

  @Override
  public Point getAndroidStationLocation() {
    return point;
  }

  public List<IAndroidPerk> getChild() {
    return child;
  }

  public void setChild(List<IAndroidPerk> child) {
    this.child = child;
  }

  @Override
  public boolean canBeToggled() {
    return canBeToggled;
  }

  public void setCanBeToggled(boolean canBeToggled) {
    this.canBeToggled = canBeToggled;
  }

  public void setOnAndroidTick(BiPredicate<ICapabilityAndroid, Integer> onAndroidTick) {
    this.onAndroidTick = onAndroidTick;
  }

  public void setOnUnlock(BiConsumer<ICapabilityAndroid, Integer> onUnlock) {
    this.onUnlock = onUnlock;
  }

  public void setOnUnlearn(BiConsumer<ICapabilityAndroid, Integer> onUnlearn) {
    this.onUnlearn = onUnlearn;
  }

  public void setCanBeUnLocked(BiPredicate<ICapabilityAndroid, Integer> canBeUnLocked) {
    this.canBeUnLocked = canBeUnLocked;
  }

  public void setCanShowOnHUD(BiPredicate<ICapabilityAndroid, Integer> canShowOnHUD) {
    this.canShowOnHUD = canShowOnHUD;
  }

  public void setXpNeeded(int xpNeeded) {
    this.xpNeeded = xpNeeded;
  }

  public void setAttributeModifierMultimap(BiFunction<ICapabilityAndroid, Integer, Multimap<Attribute, AttributeModifier>> attributeModifierMultimap) {
    this.attributeModifierMultimap = attributeModifierMultimap;
  }

  public void setPoint(Point point) {
    this.point = point;
  }
}