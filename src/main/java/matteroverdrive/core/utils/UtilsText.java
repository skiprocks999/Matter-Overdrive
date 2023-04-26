package matteroverdrive.core.utils;

import java.text.DecimalFormat;

import matteroverdrive.References;
import matteroverdrive.core.config.MatterOverdriveConfig;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilsText {
	
	public static final int MIN_DECIMAL_PLACES = 1;
	public static final int MAX_DECIMAL_PLACES = 10;

	public static DecimalFormat MATTER_FORMAT;
	public static DecimalFormat TIME_FORMAT;
	public static DecimalFormat POWER_FORMAT;
	public static DecimalFormat PERCENTAGE_FORMAT;

	public static final String GUI_BASE = "gui";
	public static final String TOOLTIP_BASE = "tooltip";
	public static final String DIMENSION_BASE = "dimension";
	public static final String JEI_BASE = "jei";
	public static final String CHAT_BASE = "chat";

	public static void init() {
		MATTER_FORMAT = new DecimalFormat("0." + getAdditionalDigits(MatterOverdriveConfig.MATTER_DECIMALS.get()));
		TIME_FORMAT = new DecimalFormat("0." + getAdditionalDigits(MatterOverdriveConfig.TIME_DECIMALS.get()));
		POWER_FORMAT = new DecimalFormat("0." + getAdditionalDigits(MatterOverdriveConfig.POWER_DECIMALS.get()));
		PERCENTAGE_FORMAT = new DecimalFormat("0." + getAdditionalDigits(MatterOverdriveConfig.PERCENT_DECIMALS.get()));
	}
	
	private static String getAdditionalDigits(int configVal) {
		if(configVal < MIN_DECIMAL_PLACES) {
			configVal = MIN_DECIMAL_PLACES;
		}
		String digits = "0";
		for(int i = 0; i < (configVal - MIN_DECIMAL_PLACES); i++) {
			digits += "#";
		}
		return digits;
	}
	
	public static String formatMatterValue(double matterValue) {
		if (matterValue < 1000) {
			return MATTER_FORMAT.format(matterValue) + " kM";
		}
		if (matterValue < 1000000) {
			return MATTER_FORMAT.format(matterValue / 1000.0) + "k kM";
		} else {
			return MATTER_FORMAT.format(matterValue / 1000000.0) + "M kM";
		}
	}

	public static String formatTimeValue(double time) {
		if (time > 0.1) {
			return TIME_FORMAT.format(time) + " s";
		}

		time = time * 1000;
		return TIME_FORMAT.format(time) + " ms";
	}

	public static String formatPowerValue(double power) {
		if (power < 1000) {
			return POWER_FORMAT.format(power) + " FE";
		}
		if (power < 1000000) {
			return POWER_FORMAT.format(power / 1000.0) + "k FE";
		} else {
			return POWER_FORMAT.format(power / 1000000.0) + "M FE";
		}
	}

	public static String formatPercentage(double percentage) {
		return PERCENTAGE_FORMAT.format(percentage) + "%";
	}

	public static int getBigBase(double val) {
		int base = 0;
		while (val > 1000) {
			val = val / 1000;
			base++;
		}
		return base;
	}

	public static String getPrefixForBase(int val) {
		switch (val) {
		case -3:
			return "n";
		case -2:
			return "u";
		case -1:
			return "m";
		case 0:
			return "";
		case 1:
			return "k";
		case 2:
			return "M";
		case 3:
			return "G";
		default:
			return "";
		}
	}
	
	public static String getFormattedBigMatter(double val, int base) {
		return getFormattedBig(val, base, MATTER_FORMAT);
	}
	
	public static String getFormattedBigPower(double val, int base) {
		return getFormattedBig(val, base, POWER_FORMAT);
	}

	private static String getFormattedBig(double val, int base, DecimalFormat format) {
		return format.format(val / Math.pow(1000, base));
	}
	
	public static MutableComponent itemTooltip(Item item) {
		return tooltip(ForgeRegistries.ITEMS.getKey(item).getPath() + ".desc");
	}

	public static MutableComponent tooltip(String key, Object... additional) {
		return translated(TOOLTIP_BASE, key, additional);
	}

	public static MutableComponent gui(String key, Object... additional) {
		return translated(GUI_BASE, key, additional);
	}

	public static MutableComponent dimension(String key, Object... additional) {
		return translated(DIMENSION_BASE, key, additional);
	}
	
	public static MutableComponent jeiTranslated(String key, Object... additional) {
		return translated(JEI_BASE, key, additional);
	}
	
	public static MutableComponent chatMessage(String key, Object...additional) {
		return translated(CHAT_BASE, key, additional);
	}

	public static MutableComponent translated(String base, String key, Object... additional) {
		return Component.translatable(base + "." + References.ID + "." + key, additional);
	}

	public static boolean guiExists(String key) {
		return translationExists(GUI_BASE, key);
	}

	public static boolean tooltipExists(String key) {
		return translationExists(TOOLTIP_BASE, key);
	}

	public static boolean dimensionExists(String key) {
		return translationExists(DIMENSION_BASE, key);
	}

	public static boolean translationExists(String base, String key) {
		return I18n.exists(base + "." + References.ID + "." + key);
	}

}
