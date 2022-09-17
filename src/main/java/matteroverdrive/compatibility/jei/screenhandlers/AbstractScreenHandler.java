package matteroverdrive.compatibility.jei.screenhandlers;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import matteroverdrive.core.screen.GenericScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

public class AbstractScreenHandler<T extends GenericScreen<?>> implements IGuiContainerHandler<T> {

	@Override
	public List<Rect2i> getGuiExtraAreas(T screen) {
		return Lists.newArrayList();
	}

}
