package matteroverdrive.client.screen;

import java.util.HashSet;

import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
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

public class ScreenMatterReplicator extends GenericScreen<InventoryMatterReplicator> {

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
	private ButtonIOConfig matter;

	private WrapperIOConfig itemWrapper;
	private WrapperIOConfig energyWrapper;
	private WrapperIOConfig matterWrapper;

	private int screenNumber = 0;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;

	
	public ScreenMatterReplicator(InventoryMatterReplicator menu, Inventory playerinventory, Component title) {
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
			matter.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			matter.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
			matterWrapper.hideButtons();
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			home.isActivated = false;
			upgrades.isActivated = false;
			ioconfig.isActivated = false;
			redstone.visible = true;
			items.visible = false;
			energy.visible = false;
			matter.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			matter.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
			matterWrapper.hideButtons();
		}, MenuButtonType.SETTINGS, menu, false);
		upgrades = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 2, button -> {
			updateScreen(2);
			home.isActivated = false;
			settings.isActivated = false;
			ioconfig.isActivated = false;
			redstone.visible = false;
			items.visible = false;
			energy.visible = false;
			matter.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			matter.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
			matterWrapper.hideButtons();
		}, MenuButtonType.UPGRADES, menu, false);
		ioconfig = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 3, button -> {
			updateScreen(3);
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.visible = true;
			energy.visible = true;
			matter.visible = true;
			items.isActivated = false;
			energy.isActivated = false;
			matter.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
			matterWrapper.hideButtons();
		}, MenuButtonType.IO, menu, false);
		redstone = new ButtonRedstoneMode(this, 48, 32, button -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(matter.getBlockPos()));
			}
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientRedstoneMode;
			}
			return 0;
		});
		items = new ButtonIOConfig(this, 48, 32, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			energy.isActivated = false;
			matter.isActivated = false;
			itemWrapper.showButtons();
			energyWrapper.hideButtons();
			matterWrapper.hideButtons();
		}, IOConfigButtonType.ITEM);
		energy = new ButtonIOConfig(this, 48, 72, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.isActivated = false;
			matter.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.showButtons();
			matterWrapper.hideButtons();
		}, IOConfigButtonType.ENERGY);
		matter = new ButtonIOConfig(this, 48, 112, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
			matterWrapper.showButtons();
		}, IOConfigButtonType.MATTER);

		itemWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.hasInput;
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.hasOutput;
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Item);
		energyWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.canReceive();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.canExtract();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Energy);
		matterWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.canReceive();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.canExtract();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Matter);

		itemWrapper.initButtons();
		energyWrapper.initButtons();
		matterWrapper.initButtons();

		addButton(close);
		addButton(menu);
		addButton(home);
		addButton(settings);
		addButton(upgrades);
		addButton(redstone);
		addButton(ioconfig);
		addButton(items);
		addButton(energy);
		addButton(matter);
		for (ButtonIO button : itemWrapper.getButtons()) {
			addButton(button);
		}
		for (ButtonIO button : energyWrapper.getButtons()) {
			addButton(button);
		}
		for (ButtonIO button : matterWrapper.getButtons()) {
			addButton(button);
		}

		redstone.visible = false;
		items.visible = false;
		energy.visible = false;
		matter.visible = false;
		itemWrapper.hideButtons();
		energyWrapper.hideButtons();
		matterWrapper.hideButtons();
		
		addScreenComponent(new ScreenComponentProgress(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return (double) matter.clientProgress / matter.getProcessingTime();
			}
			return 0;
		}, this, 33, 48, new int[] { 0 }));
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null && matter.clientRunning) {
				return matter.getCurrentPowerUsage(true);
			}
			return 0;
		}, this, 167, 35, new int[] { 0 }));
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.getMatterStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.getMaxMatterStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null && matter.clientRunning) {
				return matter.clientRecipeValue;
			}
			return 0;
		}, this, 133, 35, new int[] { 0 }).setMatter());
		addScreenComponent(new ScreenComponentIndicator(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientRunning;
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
		addScreenComponent(new ScreenComponentLabel(this, 80, 122, new int[] { 3 }, UtilsText.gui("iomatter"),
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
