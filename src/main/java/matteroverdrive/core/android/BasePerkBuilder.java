package matteroverdrive.core.android;

import com.google.common.collect.Multimap;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import matteroverdrive.core.android.api.perk.IAndroidPerk;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class BasePerkBuilder extends BaseAndroidPerk {

  public BasePerkBuilder(String perkName) {
    super(perkName);
  }

  public BasePerkBuilder onAndroidTick(BiPredicate<ICapabilityAndroid, Integer> onAndroidTick) {
    this.setOnAndroidTick(onAndroidTick);
    return this;
  }

  public BasePerkBuilder onUnlock(BiConsumer<ICapabilityAndroid, Integer> onUnlock) {
    this.setOnUnlock(onUnlock);
    return this;
  }

  public BasePerkBuilder onUnlearn(BiConsumer<ICapabilityAndroid, Integer> onUnlearn) {
    this.setOnUnlearn(onUnlearn);
    return this;
  }

  public BasePerkBuilder canBeUnLocked(BiPredicate<ICapabilityAndroid, Integer> canBeUnLocked) {
    this.setCanBeUnLocked(canBeUnLocked);
    return this;
  }

  public BasePerkBuilder canShowOnHUD(BiPredicate<ICapabilityAndroid, Integer> canShowOnHUD) {
    this.setCanShowOnHUD(canShowOnHUD);
    return this;
  }

  public BasePerkBuilder maxLevel(int maxLevel) {
    this.setMaxLevel(maxLevel);
    return this;
  }

  public BasePerkBuilder xpNeeded(int xpNeeded) {
    this.setXpNeeded(xpNeeded);
    return this;
  }

  public BasePerkBuilder attributeModifierMultimap(BiFunction<ICapabilityAndroid, Integer, Multimap<Attribute, AttributeModifier>> attributeModifierMultimap) {
    this.setAttributeModifierMultimap(attributeModifierMultimap);
    return this;
  }

  public BasePerkBuilder requiredItems(List<ItemStack> requiredItems) {
    this.setRequiredItems(requiredItems);
    return this;
  }

  public BasePerkBuilder parent(IAndroidPerk parent) {
    this.setParent(parent);
    return this;
  }

  public BasePerkBuilder child(IAndroidPerk child) {
    child.setParent(this);
    this.getChild().add(child);
    return this;
  }

  public BasePerkBuilder point(Point point) {
    this.setPoint(point);
    return this;
  }

  public BasePerkBuilder canToggle() {
    this.setCanBeToggled(true);
    return this;
  }

}