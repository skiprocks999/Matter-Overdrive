package matteroverdrive.core.utils;

import java.text.DecimalFormat;

import matteroverdrive.References;
import net.minecraft.network.chat.TranslatableComponent;

public class UtilsText {

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

	public static String formatMatterValue(double matterValue) {
		if (matterValue < 1000) {
			return FORMAT.format(matterValue) + " kM";
		}
		if (matterValue < 1000000) {
			return FORMAT.format(matterValue / 1000.0) + "k kM";
		} else {
			return FORMAT.format(matterValue / 1000000.0) + "M kM";
		}
	}

	public static String formatTimeValue(double time) {
		if (time > 0.1) {
			return FORMAT.format(time) + " s";
		}

		time = time * 1000;
		return FORMAT.format(time) + " ms";
	}

	public static String formatPowerValue(double power) {
		if (power < 1000) {
			return FORMAT.format(power) + " FE";
		}
		if (power < 1000000) {
			return FORMAT.format(power / 1000.0) + "k FE";
		} else {
			return FORMAT.format(power / 1000000.0) + "M FE";
		}
	}

	public static String formatPercentage(double percentage) {
		return FORMAT.format(percentage) + "%";
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

	public static String getFormattedBig(double val, int base) {
		return FORMAT.format(val / Math.pow(1000, base));
	}

	public static TranslatableComponent tooltip(String key, Object... additional) {
		return translated("tooltip", key, additional);
	}

	public static TranslatableComponent gui(String key, Object... additional) {
		return translated("gui", key, additional);
	}

	public static TranslatableComponent translated(String base, String key, Object... additional) {
		return new TranslatableComponent(base + "." + References.ID + "." + key, additional);
	}

}
