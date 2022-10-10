package matteroverdrive.compatibility.jei.screenhandlers.types;

import java.util.List;

import matteroverdrive.client.screen.ScreenDiscManipulator;
import matteroverdrive.compatibility.jei.screenhandlers.AbstractScreenHandler;
import net.minecraft.client.renderer.Rect2i;

public class ScreenHandlerDiscManipulator extends AbstractScreenHandler<ScreenDiscManipulator> {

	@Override
	public List<Rect2i> getGuiExtraAreas(ScreenDiscManipulator screen) {
		List<Rect2i> rectangles = super.getGuiExtraAreas(screen);
		if (screen.menu.isExtended) {
			rectangles.add(new Rect2i(screen.getGuiRight(), screen.getGuiTop() + 33, 37, 143));
		}
		return rectangles;
	}
	
}
