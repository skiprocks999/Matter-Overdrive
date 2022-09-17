package matteroverdrive.compatibility.jei.screenhandlers.types;

import java.util.List;

import matteroverdrive.client.screen.ScreenMatterReplicator;
import matteroverdrive.compatibility.jei.screenhandlers.AbstractScreenHandler;
import net.minecraft.client.renderer.Rect2i;

public class ScreenHandlerMatterReplicator extends AbstractScreenHandler<ScreenMatterReplicator> {

	@Override
	public List<Rect2i> getGuiExtraAreas(ScreenMatterReplicator screen) {
		List<Rect2i> rectangles = super.getGuiExtraAreas(screen);
		if (screen.menu.isExtended) {
			rectangles.add(new Rect2i(screen.getGuiRight(), screen.getGuiTop() + 33, 37, 143));
		}
		return rectangles;
	}

}
