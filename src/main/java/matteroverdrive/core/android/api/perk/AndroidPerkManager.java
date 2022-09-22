package matteroverdrive.core.android.api.perk;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidPerkManager implements INBTSerializable<CompoundTag> {

  public static String NBT_ENABLED = "Enabled";
  public static String NBT_OWNED = "Owned";
  public static String NBT_TRACKED = "Tracker";

  private Map<String, Integer> owned;
  private List<String> enabled;
  private Map<String, Long> perkActivityTracker;

  public AndroidPerkManager() {
    this.owned = new HashMap<>();
    this.enabled = new ArrayList<>();
    this.perkActivityTracker = new HashMap<>();
  }

  public boolean hasPerk(IAndroidPerk perk){
    return owned.containsKey(perk.getName());
  }

  public boolean hasPerkEnabled(IAndroidPerk perk){
    return enabled.contains(perk.getName());
  }

  public Map<String, Integer> getOwned() {
    return owned;
  }

  public List<String> getEnabled() {
    return enabled;
  }

  public Map<String, Long> getPerkActivityTracker() {
    return perkActivityTracker;
  }

  public void buyPerk(IAndroidPerk perk) {
    if (this.owned.containsKey(perk.getName())) {
      this.owned.put(perk.getName(), this.owned.get(perk.getName()) + 1);
    } else {
      this.owned.put(perk.getName(), 1);
    }
  }

  public void togglePerk(String perk) {
    if (this.owned.containsKey(perk)) {
      if (this.enabled.contains(perk)) {
        this.enabled.remove(perk);
      } else {
        this.enabled.add(perk);
      }
    }
  }

  public int getLevel(IAndroidPerk perk) {
    return hasPerk(perk) ? this.owned.get(perk.getName()) : 0;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag compoundTag = new CompoundTag();
    CompoundTag owned = new CompoundTag();
    for (String s : this.owned.keySet()) {
      owned.putInt(s, this.owned.get(s));
    }
    CompoundTag enabled = new CompoundTag();
    for (int i = 0; i < this.enabled.size(); i++) {
      enabled.putString(i + "", this.enabled.get(i));
    }
    CompoundTag tracker = new CompoundTag();
    for (String s : this.perkActivityTracker.keySet()) {
      tracker.putLong(s, this.perkActivityTracker.get(s));
    }
    compoundTag.put(NBT_ENABLED, enabled);
    compoundTag.put(NBT_OWNED, owned);
    compoundTag.put(NBT_TRACKED, tracker);
    return compoundTag;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    this.owned.clear();
    this.enabled.clear();
    CompoundTag owned = nbt.getCompound(NBT_OWNED);
    for (String s : owned.getAllKeys()) {
      this.owned.put(s, owned.getInt(s));
    }
    CompoundTag enabled = nbt.getCompound(NBT_ENABLED);
    for (String s : enabled.getAllKeys()) {
      this.enabled.add(enabled.getString(s));
    }
    CompoundTag tracked = nbt.getCompound(NBT_TRACKED);
    for (String s : tracked.getAllKeys()) {
      this.perkActivityTracker.put(s, tracked.getLong(s));
    }
  }
}
