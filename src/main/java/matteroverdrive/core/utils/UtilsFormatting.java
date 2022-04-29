package matteroverdrive.core.utils;

import java.text.DecimalFormat;

public class UtilsFormatting {

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

}
