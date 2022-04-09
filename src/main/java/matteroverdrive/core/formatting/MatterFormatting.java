package matteroverdrive.core.formatting;

import java.text.DecimalFormat;

public class MatterFormatting {
	
	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	
	public static String formatMatterValue(int matterValue) {
		if(matterValue < 1000) {
			return FORMAT.format(matterValue) + " kM";
		} else if (matterValue < 1000000) {
			return FORMAT.format((double) matterValue / 1000.0) + "k kM";
		} else {
			return FORMAT.format((double) matterValue / 1000000.0) + "M kM";
		}
	}

}
