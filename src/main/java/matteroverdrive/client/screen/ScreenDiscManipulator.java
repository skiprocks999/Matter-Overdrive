package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryDiscManipulator;
import matteroverdrive.common.tile.matter_network.TileDiscManipulator;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.screen.component.wrappers.WrapperFusePatternDrive;
import matteroverdrive.core.screen.component.wrappers.WrapperWipePatterns;
import matteroverdrive.core.screen.types.GenericOverdriveScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenDiscManipulator extends GenericOverdriveScreen<InventoryDiscManipulator> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption erase;
	private ButtonMenuOption fuse;
	
	private WrapperWipePatterns eraseWrapper;
	private WrapperFusePatternDrive fuseWrapper;
	
	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;
	
	public ScreenDiscManipulator(InventoryDiscManipulator menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title, 224, 176);
	}
	
	@Override
	protected void init() {
		super.init();
		
		close = getCloseButton(207, 6);
		menu = new ButtonMenuBar(this, 212, 33, 143, EXTENDED, button -> {
			toggleBarOpen();
			erase.visible = !erase.visible;
			fuse.visible = !fuse.visible;
		});
		erase = new ButtonMenuOption(this, 217, FIRST_HEIGHT, button -> {
			updateScreen(0);
			fuse.isActivated = false;
			fuseWrapper.updateButtonVisibility(false);
		}, MenuButtonType.ERASE_DRIVE, menu, true);
		fuse = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			erase.isActivated = false;
			eraseWrapper.updateButtonVisibility(false);
		}, MenuButtonType.FUSE_DRIVE, menu, false);
		
		addScreenComponent(new ScreenComponentHotbarBar(this, 40, 143, 169, new int[] { 0, 1 }));
		
		addButton(close);
		addButton(menu);
		addButton(erase);
		addButton(fuse);
		
		eraseWrapper = new WrapperWipePatterns(this, 42, 26);
		fuseWrapper = new WrapperFusePatternDrive(this, 42, 26);

		eraseWrapper.init(itemRenderer);
		fuseWrapper.init(itemRenderer);
		
		eraseWrapper.initButtons();
		fuseWrapper.initButtons();
		
	}
	
	@Override
	protected void containerTick() {
		super.containerTick();
		TileDiscManipulator tile = getMenu().getTile();
		if(tile == null) {
			return;
		}
		boolean empty = tile.getInventoryCap().getStackInSlot(0).isEmpty();
		if(screenNumber == 0) {
			eraseWrapper.updateButtonVisibility(!empty);
			fuseWrapper.updateButtonVisibility(false);
		} else if (screenNumber == 1) {
			fuseWrapper.updateButtonVisibility(!empty);
			eraseWrapper.updateButtonVisibility(false);
		}
	}
	
	private void toggleBarOpen() {
		EXTENDED = !EXTENDED;
	}

}
