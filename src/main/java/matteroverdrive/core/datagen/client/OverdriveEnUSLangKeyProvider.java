package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.common.block.utils.BlockColors;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class OverdriveEnUSLangKeyProvider extends LanguageProvider {

	private String locale;
	
	public OverdriveEnUSLangKeyProvider(DataGenerator gen, String locale) {
		super(gen, References.ID, locale);
		this.locale = locale;
	}

	@Override
	protected void addTranslations() {

		switch(locale) {
		case "en_us":
		default:
			
			add("itemGroup.itemgroup" + References.ID + "main", "Matter Overdrive");
			
			add(DeferredRegisters.ITEM_IONSNIPER.get(), "Ion Sniper");
			add(DeferredRegisters.ITEM_PHASERRIFLE.get(), "Phaser Rifle");
			add(DeferredRegisters.ITEM_PHASER.get(), "Phaser");
			add(DeferredRegisters.ITEM_PLASMASHOTGUN.get(), "Plasma Shotgun");
			add(DeferredRegisters.ITEM_OMNITOOL.get(), "Omni Tool");
			
			for(BlockColors color : BlockColors.values()) {
				String name = getNameFromEnum(color.toString());
				add(DeferredRegisters.COLORED_TRITANIUM_PLATING.get(color).get(), name + " Tritanium Plating");
				add(DeferredRegisters.FLOOR_TILE.get(color).get(), name + " Floor Tile");
				add(DeferredRegisters.FLOOR_TILES.get(color).get(), name + " Floor Tiles");
			}
			add(DeferredRegisters.TRITANIUM_PLATING.get(), "Tritanium Plating");
			for(CrateColors color : TileTritaniumCrate.CrateColors.values()) {
				String name = getNameFromEnum(color.toString());
				if(name.equals("Reg")) {
					add(DeferredRegisters.TRITANIUM_CRATES.get(color).get(), "Tritanium Crate");
				} else {
					add(DeferredRegisters.TRITANIUM_CRATES.get(color).get(), name + " Tritanium Crate");
				}
				
			}
			
			add("tooltip." + References.ID + ".energystored", "%1$s / %2$s FE");
			
			add("container.tritanium_crate", "Tritanium Crate");
		}
	}
	
	private static String getNameFromEnum(String baseString) {
		String name = baseString.toLowerCase();
		if(name.contains("_")) {
			String[] split = name.split("_");
			name = "";
			for(String str : split) {
				if(str.length() > 0) {
					name = name + str.substring(0, 1).toUpperCase() + str.substring(1) + " ";
				}
			}
			while(name.charAt(name.length() - 1) == ' ') {
				name = name.substring(0, name.length() - 1);
			}
		} else {
			name = name.substring(0,1).toUpperCase() + name.substring(1);
		}
		return name;
	}

}
