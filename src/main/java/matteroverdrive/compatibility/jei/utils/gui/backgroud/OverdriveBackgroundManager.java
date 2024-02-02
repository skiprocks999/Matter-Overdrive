package matteroverdrive.compatibility.jei.utils.gui.backgroud;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper.JeiTexture;

public class OverdriveBackgroundManager {
	
	private static final int MIN_HEIGHT = 19;
	private static final int TOP_HEIGHT = 9;
	private static final int BOTTOM_HEIGHT = 9;
	private static final int STRIP_HEIGHT = 1;
	
	private static final int WIDTH = 132;
	
	private final JeiTexture texture;
	private final int height;
	private final int xStart;
	private final int yStart;
	
	public OverdriveBackgroundManager(JeiTexture texture, int height, int xStart, int yStart) {
		this.texture = texture;
		if(height < MIN_HEIGHT) {
			throw new UnsupportedOperationException("Minimum height needs to be " + MIN_HEIGHT);
		}
		this.height = height;
		this.xStart = xStart;
		this.yStart = yStart;
	}
	
	public ScreenObjectWrapper[] getBackgroundExtras() {
		return getBackgroundExtras(new ScreenObjectWrapper[] {});
	}
	
	public ScreenObjectWrapper[] getBackgroundExtras(@Nullable ScreenObjectWrapper...additional) {
		List<ScreenObjectWrapper> wrappers = new ArrayList<>();
		wrappers.add(new ScreenObjectWrapper(texture, xStart, yStart, 0, 0, WIDTH, TOP_HEIGHT));
		int stripCount = height - TOP_HEIGHT - BOTTOM_HEIGHT;
		for(int i = 0; i < stripCount; i++) {
			wrappers.add(new ScreenObjectWrapper(texture, xStart, yStart + TOP_HEIGHT + i, 0, TOP_HEIGHT, WIDTH, STRIP_HEIGHT));
		}
		int topOffset = TOP_HEIGHT + stripCount;
		wrappers.add(new ScreenObjectWrapper(texture, xStart, yStart + topOffset, 0, TOP_HEIGHT + STRIP_HEIGHT, WIDTH, BOTTOM_HEIGHT));
		
		if(additional != null) {
			for(ScreenObjectWrapper wrapper : additional) {
				wrappers.add(wrapper);
			}
		}
		
		return wrappers.toArray(new ScreenObjectWrapper[0]);
	
	}
	

}
