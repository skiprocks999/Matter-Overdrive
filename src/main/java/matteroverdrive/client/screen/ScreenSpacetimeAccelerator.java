package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventorySpacetimeAccelerator;
import matteroverdrive.common.tile.TileSpacetimeAccelerator;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateRedstoneMode;
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
	}
	
	@Override
	protected void init() {
		super.init();
		close = new ButtonGeneric(this, 207, 6, ButtonType.CLOSE_SCREEN, button -> onClose());
		menu = new ButtonMenuBar(this, 212, 33, EXTENDED, button -> {
			toggleBarOpen();
			home.visible = !home.visible;
			settings.visible = !settings.visible;
			upgrades.visible = !upgrades.visible;
		});
		home = new ButtonMenuOption(this, 217, FIRST_HEIGHT, button -> {
			updateScreen(0);
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			home.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = true;
		}, MenuButtonType.SETTINGS, menu, false);
		upgrades = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 2, button -> {
			updateScreen(2);
			home.isActivated = false;
			settings.isActivated = false;
			redstone.visible = false;
		}, MenuButtonType.UPGRADES, menu, false);
		redstone = new ButtonRedstoneMode(this, 48, 32, button -> {
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
		
		addButton(close);
		addButton(menu);
		addButton(home);
		addButton(settings);
		addButton(upgrades);
		addButton(redstone);
		
		redstone.visible = false;
		
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				return spacetime.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				return spacetime.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null && spacetime.clientRunning) {
				return spacetime.getCurrentPowerUsage(true);
			}
			return 0;
		}, this, 141, 35, new int[] { 0 }));
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				return spacetime.clientMatter.getMatterStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				return spacetime.clientMatter.getMaxMatterStored();
			}
			return 0;
		}, () -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null && spacetime.clientRunning) {
				return spacetime.getCurrentMatterUsage(true);
			}
			return 0;
		}, this, 95, 35, new int[] { 0 }).setMatter().setMatterPerTick());
		addScreenComponent(new ScreenComponentIndicator(() -> {
			TileSpacetimeAccelerator spacetime = getMenu().getTile();
			if (spacetime != null) {
				return spacetime.clientRunning;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1, 2 }));
		addScreenComponent(new ScreenComponentHotbarBar(this, 40, 143, new int[] { 0, 1, 2 }));
		addScreenComponent(new ScreenComponentLabel(this, 110, 37, new int[] { 1 }, UtilsText.gui("redstone"),
				UtilsRendering.TEXT_BLUE));
		addScreenComponent(new ScreenComponentUpgradeInfo(this, 79, 76, new int[] { 2 }, () -> getMenu().getTile())
				.setCustomTimeKey("multiplier").setMatterPerTick());
		
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
		updateComponentActivity(screenNumber);
	}

}
