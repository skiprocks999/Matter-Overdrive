package matteroverdrive.client.screen;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.core.screen.component.ScreenComponentFillArea;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentProgress;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.types.GenericMachineScreen;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenPatternStorage extends GenericMachineScreen<InventoryPatternStorage> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption settings;

	private ButtonOverdrive redstone;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;

	public ScreenPatternStorage(InventoryPatternStorage menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title, 224, 176);
	}

	@Override
	protected void init() {
		super.init();
		close = getCloseButton(207, 6);
		menu = new ButtonMenuBar(this, 212, 33, 143, EXTENDED, button -> {
			toggleBarOpen();
			home.visible = !home.visible;
			settings.visible = !settings.visible;
		});
		home = new ButtonMenuOption(this, 217, FIRST_HEIGHT, button -> {
			updateScreen(0);
			settings.isActivated = false;
			redstone.visible = false;
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			home.isActivated = false;
			redstone.visible = true;
		}, MenuButtonType.SETTINGS, menu, false);

		redstone = redstoneButton(48, 32);

		addButton(close);
		addButton(menu);
		addButton(home);
		addButton(settings);
		addButton(redstone);

		redstone.visible = false;

		addScreenComponent(
				new ScreenComponentFillArea(this, 15, 59, 2, 26, new int[] { 0 }, Colors.GUI_STANDARD.getColor()));
		addScreenComponent(new ScreenComponentProgress(() -> 0, this, 8, 61, new int[] { 0 }).vertical());
		addScreenComponent(defaultEnergyBar(167, 35, new int[] { 0 }));
		addScreenComponent(new ScreenComponentHotbarBar(this, 40, 143, 169, new int[] { 0, 1 }));
		addScreenComponent(new ScreenComponentLabel(this, 110, 37, new int[] { 1 }, UtilsText.gui("redstone"),
				Colors.HOLO.getColor()));
		addScreenComponent(getPoweredIndicator(6, 159, new int[] { 0, 1 }));

	}

	private void toggleBarOpen() {
		EXTENDED = !EXTENDED;
	}

}
