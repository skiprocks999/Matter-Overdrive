package matteroverdrive.core.screen.component;

import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentIcon extends ScreenComponent {

	private IconType type;
	
	public ScreenComponentIcon(final IconType type, final IScreenWrapper gui, final int x, final int y) {
		super(new ResourceLocation(BASE_TEXTURE_LOC  + type.getName()), gui, x, y);
		this.type = type;
	}
	
	public enum IconType {
		
	}

}
