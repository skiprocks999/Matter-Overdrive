package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.common.block.utils.BlockColors;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class OverdriveLangKeyProvider extends LanguageProvider {

	public OverdriveLangKeyProvider(DataGenerator gen, String locale) {
		super(gen, References.ID, locale);
	}

	@Override
	protected void addTranslations() {
		for(BlockColors color : BlockColors.values()) {
			String capName = color.toString().toLowerCase().substring(0, 1).toUpperCase() +  color.toString().toLowerCase().substring(1);
			add(DeferredRegisters.FLOOR_TILE.get(color).get(), capName + " Floor Tile");
			add(DeferredRegisters.FLOOR_TILES.get(color).get(), capName + " Floor Tiles");
		}
	}

}
