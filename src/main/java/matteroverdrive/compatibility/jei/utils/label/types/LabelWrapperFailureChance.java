package matteroverdrive.compatibility.jei.utils.label.types;

import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.label.LabelWrapperGeneric;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LabelWrapperFailureChance extends LabelWrapperGeneric {

	private final float chance;
	
	public LabelWrapperFailureChance(int color, int xPos, int yPos, float chance) {
		super(color, xPos, yPos, "");
		this.chance = chance;
	}
	
	@Override
	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		return Component.literal(UtilsText.formatPercentage(chance * 100));
	}

}
