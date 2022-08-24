package matteroverdrive.core.config;

import matteroverdrive.core.utils.UtilsText;
import net.minecraftforge.common.ForgeConfigSpec;

public final class MatterOverdriveConfig {

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<Boolean> CRATE_DROP_ITEMS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> VALIDATE_MATTER_ITEMS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MACHINES_DROP_ITEMS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ACCURATE_TRANSPORTER;
	
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
		VALIDATE_MATTER_ITEMS = COMMON_BUILDER.comment(
				" ",
				" ",
				"Controls if there will be dupe checks on items with matter values.", 
				"NOTE THIS WILL ALLOW SERIOUS DUPING IF DISABLED!", 
				" ", 
				"default value = true"
				).define("enable_checking", true);
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
