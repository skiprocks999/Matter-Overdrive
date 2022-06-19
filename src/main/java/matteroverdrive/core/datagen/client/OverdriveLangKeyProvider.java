package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.common.block.type.BlockColors;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.block.type.TypeMatterConduit;
import matteroverdrive.common.block.type.TypeMatterNetworkCable;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.common.item.tools.ItemMatterContainer.ContainerType;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
import matteroverdrive.common.item.type.TypeIsolinearCircuit;
import matteroverdrive.common.tile.TileTritaniumCrate;
import matteroverdrive.common.tile.TileTritaniumCrate.CrateColors;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class OverdriveLangKeyProvider extends LanguageProvider {

	private String locale;

	public OverdriveLangKeyProvider(DataGenerator gen, String locale) {
		super(gen, References.ID, locale);
		this.locale = locale;
	}

	@Override
	protected void addTranslations() {

		switch (locale) {
		case "en_us":
		default:

			add("itemGroup.itemgroup" + References.ID + "main", "Matter Overdrive");

			addItem(DeferredRegisters.ITEM_RAW_MATTER_DUST, "Raw Matter Dust");
			addItem(DeferredRegisters.ITEM_MATTER_DUST, "Matter Dust");
			addItem(DeferredRegisters.ITEM_BASE_UPGRADE, "Upgrade Shell");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.SPEED), "Speed Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.MATTER_STORAGE), "Matter Storage Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.POWER), "Power Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.POWER_STORAGE), "Power Storage Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.FAIL_SAFE), "Fail-Safe Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.HYPER_SPEED), "Hyper Speed Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.RANGE), "Range Upgrade");
			addItem(DeferredRegisters.ITEM_UPGRADES.get(UpgradeType.MUFFLER), "Muffler Upgrade");

			addItem(DeferredRegisters.ITEM_ION_SNIPER, "Ion Sniper");
			addItem(DeferredRegisters.ITEM_PHASER_RIFLE, "Phaser Rifle");
			addItem(DeferredRegisters.ITEM_PHASER, "Phaser");
			addItem(DeferredRegisters.ITEM_PLASMA_SHOTGUN, "Plasma Shotgun");
			addItem(DeferredRegisters.ITEM_OMNI_TOOL, "Omni Tool");

			addItem(DeferredRegisters.ITEM_BATTERIES.get(BatteryType.REGULAR), "Battery");
			addItem(DeferredRegisters.ITEM_BATTERIES.get(BatteryType.HIGHCAPACITY), "High-Capacity Battery");
			addItem(DeferredRegisters.ITEM_BATTERIES.get(BatteryType.CREATIVE), "Creative Battery");

			addItem(DeferredRegisters.ITEM_MATTER_CONTAINERS.get(ContainerType.REGULAR), "Matter Container");
			addItem(DeferredRegisters.ITEM_MATTER_CONTAINERS.get(ContainerType.CREATIVE), "Creative Matter Container");

			addItem(DeferredRegisters.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER1), "Isolinear Circuit Mk1");
			addItem(DeferredRegisters.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER2), "Isolinear Circuit Mk2");
			addItem(DeferredRegisters.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER3), "Isolinear Circuit Mk3");
			addItem(DeferredRegisters.ITEM_ISOLINEAR_CIRCUITS.get(TypeIsolinearCircuit.TIER4), "Isolinear Circuit Mk4");

			addItem(DeferredRegisters.ITEM_TRANSPORTER_FLASHDRIVE, "Location Flashdrive");
			addItem(DeferredRegisters.ITEM_PATTERN_DRIVE, "Pattern Drive");
			addItem(DeferredRegisters.ITEM_MATTER_SCANNER, "Matter Scanner");

			for (BlockColors color : BlockColors.values()) {
				String name = getNameFromEnum(color.toString());
				addBlock(DeferredRegisters.BLOCK_COLORED_TRITANIUM_PLATING.get(color), name + " Tritanium Plating");
				addBlock(DeferredRegisters.BLOCK_FLOOR_TILE.get(color), name + " Floor Tile");
				addBlock(DeferredRegisters.BLOCK_FLOOR_TILES.get(color), name + " Floor Tiles");
			}
			addBlock(DeferredRegisters.BLOCK_REGULAR_TRITANIUM_PLATING, "Tritanium Plating");
			for (CrateColors color : TileTritaniumCrate.CrateColors.values()) {
				String name = getNameFromEnum(color.toString());
				if (name.equals("Reg")) {
					addBlock(DeferredRegisters.BLOCK_TRITANIUM_CRATES.get(color), "Tritanium Crate");
				} else {
					addBlock(DeferredRegisters.BLOCK_TRITANIUM_CRATES.get(color), name + " Tritanium Crate");
				}

			}
			addBlock(DeferredRegisters.BLOCK_SOLAR_PANEL, "Solar Panel");
			addBlock(DeferredRegisters.BLOCK_MATTER_DECOMPOSER, "Matter Decomposer");
			addBlock(DeferredRegisters.BLOCK_MATTER_RECYCLER, "Matter Recycler");
			addBlock(DeferredRegisters.BLOCK_CHARGER_CHILD, "Charger Child");
			addBlock(DeferredRegisters.BLOCK_CHARGER, "Android Charger");
			addBlock(DeferredRegisters.BLOCK_MICROWAVE, "Microwave");
			addBlock(DeferredRegisters.BLOCK_INSCRIBER, "Inscriber");
			addBlock(DeferredRegisters.BLOCK_TRANSPORTER, "Transporter");
			addBlock(DeferredRegisters.BLOCK_SPACETIME_ACCELERATOR, "Spacetime Accelerator");
			addBlock(DeferredRegisters.BLOCK_CHUNKLOADER, "Chunkloader");
			addBlock(DeferredRegisters.BLOCK_MATTER_ANALYZER, "Matter Analyzer");
			addBlock(DeferredRegisters.BLOCK_PATTERN_STORAGE, "Pattern Storage");
			addBlock(DeferredRegisters.BLOCK_PATTERN_MONITOR, "Pattern Monitor");
			addBlock(DeferredRegisters.BLOCK_MATTER_REPLICATOR, "Matter Replicator");

			addBlock(DeferredRegisters.BLOCK_MATTER_CONDUITS.get(TypeMatterConduit.REGULAR), "Matter Conduit");
			addBlock(DeferredRegisters.BLOCK_MATTER_CONDUITS.get(TypeMatterConduit.HEAVY), "Heavy Matter Conduit");
			addBlock(DeferredRegisters.BLOCK_MATTER_NETWORK_CABLES.get(TypeMatterNetworkCable.REGULAR), "Network Cable");
			
			addTooltip("energystored", "%1$s / %2$s %3$sFE");
			addTooltip("creativeenergystored", "INFINITE");
			addTooltip("matterval", "Matter: %s");
			addTooltip("potmatterval", "Potential Matter: %s");
			addTooltip("nomatter", "NONE");
			addTooltip("openmenu", "Open Menu");
			addTooltip("closemenu", "Close Menu");
			addTooltip("menuhome", "Home");
			addTooltip("menusettings", "Settings");
			addTooltip("menuupgrades", "Upgrades");
			addTooltip("matterstored", "%1$s / %2$s %3$skM");
			addTooltip("usage", "%s");
			addTooltip("usagetick", "%s/t");
			addTooltip("menuio", "I/O");
			addTooltip("ioinput", "Input");
			addTooltip("iooutput", "Output");
			addTooltip("ionone", "None");
			addTooltip("iotop", "Top");
			addTooltip("iobottom", "Bottom");
			addTooltip("ioleft", "Left");
			addTooltip("ioright", "Right");
			addTooltip("iofront", "Front");
			addTooltip("ioback", "Back");
			addTooltip("io", "%1$s (%2$s)");
			addTooltip("upgradeinfo", "Hold %s for Details");
			addTooltip("upgradeshift", "Shift");
			addTooltip("speedbonus", "Speed: %s");
			addTooltip("mattstorebonus", "Matter Storage: %s");
			addTooltip("mattusebonus", "Matter Usage: %s");
			addTooltip("failurebonus", "Failure: %s");
			addTooltip("powstorebonus", "Power Storage: %s");
			addTooltip("powusebonus", "Power Usage: %s");
			addTooltip("rangebonus", "Range: %s");
			addTooltip("mufflerupgrade", "Mutes machine sound");
			addTooltip("invaliddest", "Invalid Destination");
			addTooltip("empty", "Empty");
			addTooltip("storedpattern", "%1$s [%2$s]");

			addGuiLabel("redstonelow", "Low");
			addGuiLabel("redstonehigh", "High");
			addGuiLabel("redstonenone", "None");
			addGuiLabel("redstone", "Redstone");
			addGuiLabel("ioitems", "Items");
			addGuiLabel("ioenergy", "Energy");
			addGuiLabel("iomatter", "Matter");
			addGuiLabel("time", "Time: %s");
			addGuiLabel("usage", "Usage: %s");
			addGuiLabel("usagetick", "Usage: %s/t");
			addGuiLabel("failure", "Failure: %s");
			addGuiLabel("range", "Range: %s Blocks");
			addGuiLabel("storage", "Storage: %s");
			addGuiLabel("soundmuted", "Sound Muffled");
			addGuiLabel("unknown", "Unknown");
			addGuiLabel("xlabel", "X");
			addGuiLabel("ylabel", "Y");
			addGuiLabel("zlabel", "Z");
			addGuiLabel("importpos", "Import");
			addGuiLabel("resetpos", "Reset");
			addGuiLabel("dimensionname", "DIM: %s");
			addGuiLabel("multiplier", "Multiplier: %s");

			addContainer("tritanium_crate", "Tritanium Crate");
			addContainer(TypeMachine.SOLAR_PANEL.id(), "Solar Panel");
			addContainer(TypeMachine.MATTER_DECOMPOSER.id(), "Matter Decomposer");
			addContainer(TypeMachine.MATTER_RECYCLER.id(), "Matter Recycler");
			addContainer(TypeMachine.CHARGER.id(), "Android Charger");
			addContainer(TypeMachine.MICROWAVE.id(), "Microwave");
			addContainer(TypeMachine.INSCRIBER.id(), "Inscriber");
			addContainer(TypeMachine.TRANSPORTER.id(), "Transporter");
			addContainer(TypeMachine.SPACETIME_ACCELERATOR.id(), "Spacetime Accelerator");
			addContainer(TypeMachine.CHUNKLOADER.id(), "Chunkloader");
			addContainer(TypeMachine.PATTERN_MONITOR.id(), "Pattern Monitor");
			addContainer(TypeMachine.PATTERN_STORAGE.id(), "Pattern Storage");
			addContainer(TypeMachine.MATTER_ANALYZER.id(), "Matter Analyzer");
			addContainer(TypeMachine.MATTER_REPLICATOR.id(), "Matter Replicator");

			addCommand("startmattercalc", "Starting Matter calculations...");
			addCommand("endmattercalc", "Finshed Matter calculations. Saved under \"Matter Overdrive/generated.json\"");
			addCommand("manualfailed", "unexpected error");
			addCommand("mainhandempty", "You must be holding an item");
			addCommand("assignedvalue", "Assigned %1$s kM to %2$s");
			addCommand("endmanualassign", "Saved under \"Matter Overdrive/manual.json\"");

			addSubtitle("crate_open", "Tritanium Crate Opens");
			addSubtitle("crate_close", "Tritanium Crate Closes");
			addSubtitle("button_expand", "Button Shifts");
			addSubtitle("button_generic", "Button is pressed");
			addSubtitle("matter_decomposer", "Matter Decomposer running");
			addSubtitle("generic_machine", "Machine runs");
			addSubtitle("transporter", "Transporter Build-up");
			addSubtitle("transporter_arrive", "Transported Entity Appears");
			
			addDimension("overworld", "Overworld");
			addDimension("the_nether", "Nether");
			addDimension("the_end", "End");
		}
	}

	private void addItem(RegistryObject<Item> item, String translation) {
		add(item.get(), translation);
	}

	private void addBlock(RegistryObject<Block> block, String translation) {
		add(block.get(), translation);
	}

	private void addTooltip(String key, String translation) {
		add("tooltip." + References.ID + "." + key, translation);
	}

	private void addContainer(String key, String translation) {
		add("container." + key, translation);
	}

	private void addCommand(String key, String translation) {
		add("command." + References.ID + "." + key, translation);
	}

	private void addSubtitle(String key, String translation) {
		add("subtitles." + References.ID + "." + key, translation);
	}

	private void addGuiLabel(String key, String translation) {
		add("gui." + References.ID + "." + key, translation);
	}
	
	private void addDimension(String key, String translation) {
		add("dimension." + References.ID + "." + key, translation);
	}

	private static String getNameFromEnum(String baseString) {
		String name = baseString.toLowerCase();
		if (name.contains("_")) {
			String[] split = name.split("_");
			name = "";
			for (String str : split) {
				if (str.length() > 0) {
					name = name + str.substring(0, 1).toUpperCase() + str.substring(1) + " ";
				}
			}
			while (name.charAt(name.length() - 1) == ' ') {
				name = name.substring(0, name.length() - 1);
			}
		} else {
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return name;
	}

}
