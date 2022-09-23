package matteroverdrive.compatibility.jei.utils.label.types;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.categories.pseudo.RecipeCategoryMatterDecomposer.PseudoRecipeMatterDecomposer;
import matteroverdrive.compatibility.jei.utils.label.LabelWrapperGeneric;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LabelWrapperMatterDecomposer extends LabelWrapperGeneric {

	public LabelWrapperMatterDecomposer(int xPos, int yPos) {
		super(Colors.HOLO_GREEN.getColor(), xPos, yPos, "");
	}
	
	@Override
	public MutableComponent getComponent(AbstractOverdriveRecipeCategory<?> category, Object recipe) {
		PseudoRecipeMatterDecomposer pseudo = (PseudoRecipeMatterDecomposer) recipe;
		return Component.literal("+" + UtilsText.formatMatterValue(pseudo.value()));
	}

}
