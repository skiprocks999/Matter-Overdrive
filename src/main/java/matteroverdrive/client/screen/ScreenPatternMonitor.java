package matteroverdrive.client.screen;

import com.mojang.blaze3d.platform.InputConstants;

import matteroverdrive.common.inventory.InventoryPatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.screen.component.wrappers.WrapperPatternMonitorScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenPatternMonitor extends GenericScreen<InventoryPatternMonitor> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption tasks;
	
	private WrapperPatternMonitorScreen wrapper;

	private int screenNumber = 0;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;
	
	public ScreenComponentVerticalSlider slider;
	
	public ScreenPatternMonitor(InventoryPatternMonitor menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}
	
	@Override
	protected void containerTick() {
		super.containerTick();
		if(screenNumber == 0) {
			wrapper.tick();
		}
	}
	
	@Override
	protected void init() {
		super.init();
		
		close = new ButtonGeneric(this, 207, 6, ButtonType.CLOSE_SCREEN, button -> onClose());
		menu = new ButtonMenuBar(this, 212, 33, EXTENDED, button -> {
			toggleBarOpen();
			home.visible = !home.visible;
			tasks.visible = !tasks.visible;
		});
		home = new ButtonMenuOption(this, 217, FIRST_HEIGHT, button -> {
			updateScreen(0);
			tasks.isActivated = false;
			wrapper.updateButtons(true);
		}, MenuButtonType.HOME, menu, true);
		tasks = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			home.isActivated = false;
			wrapper.updateButtons(false);
		}, MenuButtonType.TASKS, menu, false);
		
		addScreenComponent(new ScreenComponentHotbarBar(this, 40, 139, new int[] { 0, 1 }));
		
		wrapper = new WrapperPatternMonitorScreen(this, 53, 26);
		
		addButton(close);
		addButton(menu);
		addButton(home);
		addButton(tasks);
		
		wrapper.initButtons(itemRenderer);
		
		addScreenComponent(new ScreenComponentIndicator(() -> {
			TilePatternMonitor monitor = getMenu().getTile();
			if (monitor != null) {
				return monitor.getConnectedNetwork() != null;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1 }));
		
		
		slider = new ScreenComponentVerticalSlider(this, 9, 39, 102, new int[] { 0 });
		slider.setClickConsumer(wrapper.getSliderClickedConsumer());
		slider.setDragConsumer(wrapper.getSliderDraggedConsumer());
		addScreenComponent(slider);
		
	}

	private void toggleBarOpen() {
		EXTENDED = !EXTENDED;
	}

	private void updateScreen(int screenNumber) {
		this.screenNumber = screenNumber;
		updateComponentActivity(screenNumber);
	}

	@Override
	public int getScreenNumber() {
		return screenNumber;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if(wrapper != null && screenNumber == 0) {
			if(delta > 0) {
				//scroll up
				wrapper.handleMouseScroll(-1);
			} else if (delta < 0) {
				// scroll down
				wrapper.handleMouseScroll(1);
			}
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);
		if(slider != null && screenNumber == 0) {
			slider.mouseMoved(mouseX, mouseY);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(slider != null && screenNumber == 0) {
			slider.mouseClicked(mouseX, mouseY, button);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(slider != null && screenNumber == 0) {
			slider.mouseReleased(mouseX, mouseY, button);
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
		if(this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && screenNumber == 0 && wrapper.isSearchBarSelected()) {
			return false;
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

}
