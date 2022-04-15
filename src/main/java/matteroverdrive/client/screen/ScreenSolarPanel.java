package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventorySolarPanel;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class ScreenSolarPanel extends GenericScreen<InventorySolarPanel> {

	private ButtonGeneric close;
	private ButtonMenuBar menu;

	public ScreenSolarPanel(InventorySolarPanel menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
		components.add(new ScreenComponentCharge(() -> 0.3, this, 81, 35));
		components.add(new ScreenComponentIndicator(() -> true, this, -31, 159));
		components.add(new ScreenComponentHotbarBar(this, 3, 143));
	}

	@Override
	protected void init() {
		super.init();
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		close = new ButtonGeneric(guiWidth + 170, guiHeight + 6, ButtonType.CLOSE_SCREEN, button -> onClose());
		menu = new ButtonMenuBar(guiWidth + 175, guiHeight + 33, button -> {
		}, () -> false, (button, stack, xAxis, yAxis) -> {
			ButtonMenuBar bar = (ButtonMenuBar) button;
			if (bar.isExtended) {
				displayTooltip(stack, new TranslatableComponent("tooltip.matteroverdrive.closemenu"), xAxis, yAxis);
			} else {
				displayTooltip(stack, new TranslatableComponent("tooltip.matteroverdrive.openmenu"), xAxis, yAxis);
			}
		});
		addRenderableWidget(close);
		addRenderableWidget(menu);
	}

}
