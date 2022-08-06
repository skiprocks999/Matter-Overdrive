package matteroverdrive.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class MatterOverdriveConfig {

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<Boolean> crate_drop_items;
	public static final ForgeConfigSpec.ConfigValue<Boolean> validate_matter_items;
	public static final ForgeConfigSpec.ConfigValue<Boolean> machines_drop_items;

	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<Boolean> accurate_transporter;

	static {
		COMMON_BUILDER.push("Controls whether or not the Tritanium Crate will drop its items");
		crate_drop_items = COMMON_BUILDER.comment("default value = false").define("drop_crate_items", false);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.push(
				"Controls if there will be dupe checks on items with matter values NOTE THIS WILL ALLOW SERIOUS DUPING IF DISABLED");
		validate_matter_items = COMMON_BUILDER.comment("default value = true").define("enable_checking", true);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.push("Controls whether or not machines will drop their items and upgrades");
		machines_drop_items = COMMON_BUILDER.comment("default value = false").define("machines_drop_items", false);
		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

		CLIENT_BUILDER.push("Whether or not the transporter will hold the player still during build-up and arrival");
		accurate_transporter = CLIENT_BUILDER.comment("default value = false").define("accurate_transporter", false);
		CLIENT_BUILDER.pop();

		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

}
