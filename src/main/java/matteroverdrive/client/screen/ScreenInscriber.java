package matteroverdrive.client.screen;

import java.util.HashSet;

import matteroverdrive.common.inventory.InventoryInscriber;
import matteroverdrive.common.tile.TileInscriber;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateRedstoneMode;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentProgress;
import matteroverdrive.core.screen.component.ScreenComponentUpgradeInfo;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonIO;
import matteroverdrive.core.screen.component.button.ButtonIOConfig;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.component.button.ButtonRedstoneMode;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonIOConfig.IOConfigButtonType;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.screen.component.wrappers.WrapperIOConfig;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenInscriber extends GenericScreen<InventoryInscriber> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption settings;
	private ButtonMenuOption upgrades;
	private ButtonMenuOption ioconfig;

	private ButtonRedstoneMode redstone;

	private ButtonIOConfig items;
	private ButtonIOConfig energy;

	private WrapperIOConfig itemWrapper;
	private WrapperIOConfig energyWrapper;

	private int screenNumber = 0;

	public static final int BETWEEN_MENUS = 26;
	public static final int FIRST_HEIGHT = 40;

	public ScreenInscriber(InventoryInscriber menu, Inventory playerinventory, Component title) {
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
			ioconfig.visible = !ioconfig.visible;
		});
		home = new ButtonMenuOption(this, 217, FIRST_HEIGHT, button -> {
			updateScreen(0);
			settings.isActivated = false;
			upgrades.isActivated = false;
			ioconfig.isActivated = false;
			redstone.visible = false;
			items.visible = false;
			energy.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			home.isActivated = false;
			upgrades.isActivated = false;
			ioconfig.isActivated = false;
			redstone.visible = true;
			items.visible = false;
			energy.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
		}, MenuButtonType.SETTINGS, menu, false);
		upgrades = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 2, button -> {
			updateScreen(2);
			home.isActivated = false;
			settings.isActivated = false;
			ioconfig.isActivated = false;
			redstone.visible = false;
			items.visible = false;
			energy.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
		}, MenuButtonType.UPGRADES, menu, false);
		ioconfig = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 3, button -> {
			updateScreen(3);
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.visible = true;
			energy.visible = true;
			items.isActivated = false;
			energy.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
		}, MenuButtonType.IO, menu, false);
		redstone = new ButtonRedstoneMode(this, 48, 32, button -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(inscriber.getBlockPos()));
			}
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientRedstoneMode;
			}
			return 0;
		});
		items = new ButtonIOConfig(this, 48, 32, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			energy.isActivated = false;
			itemWrapper.showButtons();
			energyWrapper.hideButtons();
		}, IOConfigButtonType.ITEM);
		energy = new ButtonIOConfig(this, 48, 72, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.showButtons();
		}, IOConfigButtonType.ENERGY);

		itemWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientInventory.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientInventory.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientInventory.hasInput;
			}
			return false;
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientInventory.hasOutput;
			}
			return false;
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Item);
		energyWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientEnergy.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientEnergy.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientEnergy.canReceive();
			}
			return false;
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientEnergy.canExtract();
			}
			return false;
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Energy);

		itemWrapper.initButtons();
		energyWrapper.initButtons();

		addButton(close);
		addButton(menu);
		addButton(home);
		addButton(settings);
		addButton(upgrades);
		addButton(redstone);
		addButton(ioconfig);
		addButton(items);
		addButton(energy);
		for (ButtonIO button : itemWrapper.getButtons()) {
			addButton(button);
		}
		for (ButtonIO button : energyWrapper.getButtons()) {
			addButton(button);
		}

		redstone.visible = false;
		items.visible = false;
		energy.visible = false;
		itemWrapper.hideButtons();
		energyWrapper.hideButtons();

		addScreenComponent(new ScreenComponentProgress(() -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return (double) inscriber.clientProgress / (double) TileInscriber.OPERATING_TIME;
			}
			return 0;
		}, this, 33, 48, new int[] { 0 }));
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null && inscriber.clientRunning) {
				return inscriber.getCurrentPowerUsage(true);
			}
			return 0;
		}, this, 118, 35, new int[] { 0 }));
		addScreenComponent(new ScreenComponentIndicator(() -> {
			TileInscriber inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientRunning;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1, 2, 3 }));
		addScreenComponent(new ScreenComponentHotbarBar(this, 40, 143, new int[] { 0, 1, 2, 3 }));
		addScreenComponent(new ScreenComponentLabel(this, 110, 37, new int[] { 1 }, UtilsText.gui("redstone"),
				UtilsRendering.TEXT_BLUE));
		addScreenComponent(new ScreenComponentUpgradeInfo(this, 79, 76, new int[] { 2 }, () -> getMenu().getTile()));
		addScreenComponent(new ScreenComponentLabel(this, 80, 42, new int[] { 3 }, UtilsText.gui("ioitems"),
				UtilsRendering.TEXT_BLUE));
		addScreenComponent(new ScreenComponentLabel(this, 80, 80, new int[] { 3 }, UtilsText.gui("ioenergy"),
				UtilsRendering.TEXT_BLUE));
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

}
