package matteroverdrive.common.item.pill;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.item.utils.OverdriveItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class ItemAndroidPill extends OverdriveItem {

	// For eating or internal damage
	public static final DamageSource NANITES = new DamageSource("nanites").bypassArmor();

	public static final FoodProperties PILLS = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F)
			.alwaysEat().fast().build();

	private final Colors color;

	public ItemAndroidPill(Properties properties, Colors color, boolean hasShiftTip) {
		super(properties, hasShiftTip);
		this.color = color;
	}
	
	@Override
	public boolean isColored() {
		return true;
	}

	@Override
	public int getColor(ItemStack item, int index) {
		return index == 0 ? Colors.WHITE.getColor() : color.getColor();
	}
	
	@Override
	public int getNumOfLayers() {
		return 2;
	}
	
}
