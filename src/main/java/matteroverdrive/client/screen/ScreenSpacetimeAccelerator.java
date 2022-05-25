package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.common.tile.TileSpacetimeAccelerator;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.PacketUpdateRedstoneMode;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentUpgradeInfo;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.component.button.ButtonRedstoneMode;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenSpacetimeAccelerator extends GenericScreen<InventorySpacetimeAccelerator> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption settings;
	private ButtonMenuOption upgrades;

	private ButtonRedstoneMode redstone;
	
	private int screenNumber = 0;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;

	
	public ScreenSpacetimeAccelerator(InventorySpacetimeAccelerator menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
		components.add(new ScreenComponentCharge(() -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null) {
				return spacetime.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null) {
				return spacetime.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null && spacetime.clientRunning) {
				return spacetime.getCurrentPowerUsage(true);
			}
			return 0;
		}, this, 141, 35, new int[] { 0 }));
		components.add(new ScreenComponentCharge(() -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null) {
				return spacetime.clientMatter.getMatterStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null) {
				return spacetime.clientMatter.getMaxMatterStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null && spacetime.clientRunning) {
				return spacetime.getCurrentMatterUsage(true);
			}
			return 0;
		}, this, 95, 35, new int[] { 0 }).setMatter().setMatterPerTick());
		components.add(new ScreenComponentIndicator(() -> {
			TileSpacetimeAccelerator spacetime = menu.getTile();
			if (spacetime != null) {
				return spacetime.clientRunning;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1, 2 }));
		components.add(new ScreenComponentHotbarBar(this, 40, 143, new int[] { 0, 1, 2 }));
		components.add(new ScreenComponentLabel(this, 110, 37, new int[] { 1 }, UtilsText.gui("redstone"),
				UtilsRendering.TEXT_BLUE));
		components.add(new ScreenComponentUpgradeInfo(this, 79, 76, new int[] { 2 }, () -> menu.getTile())
				.setCustomTimeKey("multiplier").setMatterPerTick());
	}
	
	@Override
	protected void init() {
		super.init();
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		close = new ButtonGeneric(guiWidth + 207, guiHeight + 6, ButtonType.CLOSE_SCREEN, button -> onClose());
		menu = new ButtonMenuBar(guiWidth + 212, guiHeight + 33, EXTENDED, button -> {
			toggleBarOpen();
			home.visible = !home.visible;
			settings.visible = !settings.visible;
			upgrades.visible = !upgrades.visible;
		}, this);
		home = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT, this, button -> {
			updateScreen(0);
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS, this, button -> {
			updateScreen(1);
			home.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = true;
		}, MenuButtonType.SETTINGS, menu, false);
		upgrades = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS * 2, this, button -> {
			updateScreen(2);
			home.isActivated = false;
			settings.isActivated = false;
			redstone.visible = false;
		}, MenuButtonType.UPGRADES, menu, false);
		redstone = new ButtonRedstoneMode(guiWidth + 48, guiHeight + 32, button -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(spacetime.getBlockPos()));
			}
		}, () -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				return spacetime.clientRedstoneMode;
			}
			return 0;
		});
		
		addRenderableWidget(close);
		addRenderableWidget(menu);
		addRenderableWidget(home);
		addRenderableWidget(settings);
		addRenderableWidget(upgrades);
		addRenderableWidget(redstone);
		
		redstone.visible = false;
	}

	@Override
	public int getScreenNumber() {
		return screenNumber;
	}
	
	private void toggleBarOpen() {
		EXTENDED = !EXTENDED;
	}

	private void updateScreen(int screenNumber) {
		this.screenNumber = screenNumber;
		updateSlotActivity(this.screenNumber);
	}

}
