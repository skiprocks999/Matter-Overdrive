package matteroverdrive.core.config;

import matteroverdrive.core.utils.UtilsText;
import net.minecraftforge.common.ForgeConfigSpec;

public final class MatterOverdriveConfig {

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<Boolean> CRATE_DROP_ITEMS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MACHINES_DROP_ITEMS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ACCURATE_TRANSPORTER;
	public static final ForgeConfigSpec.ConfigValue<Integer> CHUNKLOADER_RANGE;
	//matter generator options
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_DEFAULT_GENERATORS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_SMELTING_GENERATOR;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_CRAFTING_GENERATOR;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_SMITHING_GENERATOR;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_DEFAULT_GENERATOR_CORRECTIONS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_DEFAULT_SMELTING_CORRECTIONS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_DEFAULT_CRAFTING_CORRECTIONS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> USE_DEFAULT_SMITHING_CORRECTIONS;
	//matter event options
	public static final ForgeConfigSpec.ConfigValue<Boolean> POST_DEFAULT_GENERATOR_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> POST_SPECIAL_GENERATOR_EVENT;
	
	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_DECIMALS;
	public static final ForgeConfigSpec.ConfigValue<Integer> TIME_DECIMALS;
	public static final ForgeConfigSpec.ConfigValue<Integer> POWER_DECIMALS;
	public static final ForgeConfigSpec.ConfigValue<Integer> PERCENT_DECIMALS;

	static {
		
		//Common Config File
		
		CRATE_DROP_ITEMS = COMMON_BUILDER.comment(
				"Common configuration values for Matter Overdrive. These configurations affect all players and the server.",
				" ",
				" ",
				" ",
				"Controls whether or not the Tritanium Crate will drop its items when broken.", 
				" ", 
				"default value = false"
				).define("drop_crate_items", false);
		MACHINES_DROP_ITEMS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not machines will drop their inventories when broken.",
				" ",
				"default value = false").define("machines_drop_items", false);
		ACCURATE_TRANSPORTER = COMMON_BUILDER.comment(
				" ",
				" ",
				"Whether or not the transporter will hold the player still during build-up and arrival.",
				" ",
				"default value = false"
				).define("accurate_transporter", false);
		CHUNKLOADER_RANGE = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls the maximum radius of the chunkloader. A radius of zero means it will only load a single",
				"chunk. A radius of 1 means is will load a 3x3 area of chunks. A radius of 2 means it will load a",
				"5x5 area of chunks and so on.",
				" ",
				"default value = false"
				).defineInRange("chunkloader_range", 5, 0, 16);
		USE_DEFAULT_GENERATORS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not all of the default matter value generator consumers found in the DefaultGeneratorConsumers",
				"class will be used. NOTE: if you disable them without replacing them, things may not work!",
				" ",
				"default value = true"
				).define("use_default_generators", true);
		USE_SMELTING_GENERATOR = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default smelting matter value generator consumer found in the DefaultGeneratorConsumers",
				"class will be used. NOTE: if you disable this without replacing it, things may not work!",
				" ",
				"default value = true"
				).define("use_smelting_generator", true);
		USE_CRAFTING_GENERATOR = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default crafting matter value generator consumers found in the DefaultGeneratorConsumers",
				"class will be used. NOTE: if you disable this without replacing it, things may not work!",
				" ",
				"default value = true"
				).define("use_crafting_generator", true);
		USE_SMITHING_GENERATOR = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default smithing matter value generator consumers found in the DefaultGeneratorConsumers",
				"class will be used. NOTE: if you disable this without replacing it, things may not work!",
				" ",
				"default value = true"
				).define("use_smithing_generator", true);
		USE_DEFAULT_GENERATOR_CORRECTIONS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default corrections applied to the matter value generator consumers found",
				"in the DefaultGeneratorConsumers class will be applied. NOTE: if they are removed, and are not replced",
				"properly, things may not work!",
				" ",
				"default value = true"
				).define("use_default_corrections", true);
		USE_DEFAULT_SMELTING_CORRECTIONS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default corrections applied to the smelting matter value generator consumer found",
				"in the DefaultGeneratorConsumers class will be applied. NOTE: if they are removed, and are not replced",
				"properly, things may not work!",
				" ",
				"default value = true"
				).define("use_default_corrections", true);
		USE_DEFAULT_CRAFTING_CORRECTIONS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default corrections applied to the crafting matter value generator consumer found",
				"in the DefaultGeneratorConsumers class will be applied. NOTE: if they are removed, and are not replced",
				"properly, things may not work!",
				" ",
				"default value = true"
				).define("use_default_corrections", true);
		USE_DEFAULT_SMITHING_CORRECTIONS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls whether or not the default corrections applied to the smithing matter value generator consumer found",
				"in the DefaultGeneratorConsumers class will be applied. NOTE: if they are removed, and are not replced",
				"properly, things may not work!",
				" ",
				"default value = true"
				).define("use_default_corrections", true);
		POST_DEFAULT_GENERATOR_EVENT = COMMON_BUILDER.comment(
				" ",
				" ",
				"THIS SHOULD BE TRUE IN MOST CIRCUMSTANES!",
				" ",
				"Controls whether or not the RegisterMatterGeneratorsEvent will be posted to the event bus, there by",
				"preventing all AbstractMatterValueGenerator instances collected by it from being added. All mods creating",
				"a generator should add their generators using the event. ",
				" ",
				"DO NOT SET THIS TO FALSE UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING!",
				" ",
				"This feature is intended for modpack authors who indend to do heavy custom progression with the matter",
				"system and need to ensure only their generators are registered.",
				" ",
				"default value = true"
				).define("post_default_generator_event", true);
		POST_SPECIAL_GENERATOR_EVENT = COMMON_BUILDER.comment(
				" ",
				" ",
				"THIS SHOULD BE FALSE IN MOST CIRCUMSTANCES!",
				" ",
				"Controls whether or not the RegisterSpecialGeneratorsEvent will be posted to the event bus, there by giving",
				"a way to still register AbstractMatterValueGenerator instances when \"post_default_generator_event\" is false. If",
				"you are a mod author looking to register an instance, then do not use this event!",
				" ",
				"DO NOT SET THIS TO TRUE UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING!",
				" ",
				"This feature is intended for modpack authors who indend to do heavy custom progression with the matter",
				"system and need to ensure only their generators are registered.",
				" ",
				"Note this event will only post when \"post_default_generator_event\" is false,",
				" ",
				"default value = false"
				).define("post_special_generator_event", false);
		
		COMMON_CONFIG = COMMON_BUILDER.build();
		
		//Client Config File
		
		MATTER_DECIMALS = CLIENT_BUILDER.comment(
				"Client only configurations for Matter Overdrive. These configurations will only affect you and will not",
				"affect the server or other players",
				" ",
				" ",
				" ",
				"The number of decimal places a matter value will show (purely visual). NOTE additional places will only",
				"render when applicable.",
				" ",
				"default value = 2"
				).defineInRange("matter_decimals", 2, UtilsText.MIN_DECIMAL_PLACES, UtilsText.MAX_DECIMAL_PLACES);
		TIME_DECIMALS = CLIENT_BUILDER.comment(
				" ",
				" ",
				"The number of decimal places a time tooltip will show (purely visual). NOTE additional places will only",
				"render when applicable.",
				" ",
				"default value = 2"
				).defineInRange("time_decimals", 2, UtilsText.MIN_DECIMAL_PLACES, UtilsText.MAX_DECIMAL_PLACES);
		POWER_DECIMALS = CLIENT_BUILDER.comment(
				" ",
				" ",
				"The number of decimal places a power tooltip will show (purely visual). NOTE additional places will only",
				"render when applicable.",
				" ",
				"default value = 2"
				).defineInRange("power_decimals", 2, UtilsText.MIN_DECIMAL_PLACES, UtilsText.MAX_DECIMAL_PLACES);	
		PERCENT_DECIMALS = CLIENT_BUILDER.comment(
				" ",
				" ",
				"The number of decimal places a percentage tooltip will show (purely visual) NOTE additional places will only",
				"render when applicable.",
				" ",
				"default value = 2"
				).defineInRange("percent_decimals", 2, UtilsText.MIN_DECIMAL_PLACES, UtilsText.MAX_DECIMAL_PLACES);
		
		CLIENT_CONFIG = CLIENT_BUILDER.build();
		
	}
	
}
