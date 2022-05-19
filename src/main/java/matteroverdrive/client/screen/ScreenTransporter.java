package matteroverdrive.client.screen;

import java.util.HashSet;

import com.mojang.blaze3d.platform.InputConstants;

import matteroverdrive.common.inventory.InventoryTransporter;
import matteroverdrive.common.tile.TileTransporter;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.PacketUpdateTransporterLocationInfo;
import matteroverdrive.core.packet.type.PacketUpdateTransporterLocationInfo.PacketType;
import matteroverdrive.core.packet.type.PacketUpdateRedstoneMode;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentUpgradeInfo;
import matteroverdrive.core.screen.component.button.ButtonEditTransporterLocation;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonIO;
import matteroverdrive.core.screen.component.button.ButtonIOConfig;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.component.button.ButtonRedstoneMode;
import matteroverdrive.core.screen.component.button.ButtonTransporterLocation;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonIOConfig.IOConfigButtonType;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.screen.component.wrappers.WrapperIOConfig;
import matteroverdrive.core.screen.component.wrappers.WrapperTransporterLocationEditer;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenTransporter extends GenericScreen<InventoryTransporter> {

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

	private ButtonTransporterLocation[] locationButtons = new ButtonTransporterLocation[5];
	private ButtonEditTransporterLocation[] editButtons = new ButtonEditTransporterLocation[5];
	
	private WrapperTransporterLocationEditer editor;

	private int screenNumber = 0;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;

	public ScreenTransporter(InventoryTransporter menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
		components.add(new ScreenComponentCharge(() -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null) {
				return transporter.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null) {
				return transporter.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null && transporter.clientRunning) {
				return transporter.clientEnergyUsage;
			}
			return 0;
		}, this, 48, 35, new int[] { 0 }));
		components.add(new ScreenComponentCharge(() -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null) {
				return transporter.clientMatter.getMatterStored();
			}
			return 0;
		}, () -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null) {
				return transporter.clientMatter.getMaxMatterStored();
			}
			return 0;
		}, () -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null && transporter.clientRunning) {
				return transporter.clientMatterUsage;
			}
			return 0;
		}, this, 48, 94, new int[] { 0 }).setMatter());
		components.add(new ScreenComponentIndicator(() -> {
			TileTransporter transporter = menu.getTile();
			if (transporter != null) {
				return transporter.clientRunning;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1, 2, 3, 4 }));
		components.add(new ScreenComponentHotbarBar(this, 40, 143, new int[] { 0, 1, 2, 3 }));
		components.add(new ScreenComponentLabel(this, 110, 37, new int[] { 1 }, UtilsText.gui("redstone"),
				UtilsRendering.TEXT_BLUE));
		components.add(new ScreenComponentUpgradeInfo(this, 79, 76, new int[] { 2 }, () -> menu.getTile()));
		components.add(new ScreenComponentLabel(this, 80, 42, new int[] { 3 }, UtilsText.gui("ioitems"),
				UtilsRendering.TEXT_BLUE));
		components.add(new ScreenComponentLabel(this, 80, 80, new int[] { 3 }, UtilsText.gui("ioenergy"),
				UtilsRendering.TEXT_BLUE));
		components.add(new ScreenComponentLabel(this, 80, 122, new int[] { 3 }, UtilsText.gui("iomatter"),
				UtilsRendering.TEXT_BLUE));
		components.add(new ScreenComponentLabel(this, 70, 59, new int[] { 4 }, UtilsText.gui("xlabel"),
				UtilsRendering.WHITE));
		components.add(new ScreenComponentLabel(this, 70, 79, new int[] { 4 }, UtilsText.gui("ylabel"),
				UtilsRendering.WHITE));
		components.add(new ScreenComponentLabel(this, 70, 99, new int[] { 4 }, UtilsText.gui("zlabel"),
				UtilsRendering.WHITE));
	}

	@Override
	protected void init() {
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		super.init();
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		close = new ButtonGeneric(guiWidth + 207, guiHeight + 6, ButtonType.CLOSE_SCREEN, button -> onClose());
		menu = new ButtonMenuBar(guiWidth + 212, guiHeight + 33, EXTENDED, button -> {
			toggleBarOpen();
			home.visible = !home.visible;
			settings.visible = !settings.visible;
			upgrades.visible = !upgrades.visible;
			ioconfig.visible = !ioconfig.visible;
		}, this);
		home = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT, this, button -> {
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
			editor.updateButtons(false);
			for(int i = 0; i < locationButtons.length; i++) {
				locationButtons[i].visible = true;
				editButtons[i].visible = true;
			}
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS, this, button -> {
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
			editor.updateButtons(false);
			for(int i = 0; i < locationButtons.length; i++) {
				locationButtons[i].visible = false;
				editButtons[i].visible = false;
			}
		}, MenuButtonType.SETTINGS, menu, false);
		upgrades = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS * 2, this, button -> {
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
			editor.updateButtons(false);
			for(int i = 0; i < locationButtons.length; i++) {
				locationButtons[i].visible = false;
				editButtons[i].visible = false;
			}
		}, MenuButtonType.UPGRADES, menu, false);
		ioconfig = new ButtonMenuOption(guiWidth + 217, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS * 3, this, button -> {
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
			editor.updateButtons(false);
			for(int i = 0; i < locationButtons.length; i++) {
				locationButtons[i].visible = false;
				editButtons[i].visible = false;
			}
		}, MenuButtonType.IO, menu, false);
		redstone = new ButtonRedstoneMode(guiWidth + 48, guiHeight + 32, button -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(transporter.getBlockPos()));
			}
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientRedstoneMode;
			}
			return 0;
		});
		items = new ButtonIOConfig(guiWidth + 48, guiHeight + 32, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			energy.isActivated = false;
			matter.isActivated = false;
			itemWrapper.showButtons();
			energyWrapper.hideButtons();
			matterWrapper.hideButtons();
			editor.updateButtons(false);
		}, IOConfigButtonType.ITEM);
		energy = new ButtonIOConfig(guiWidth + 48, guiHeight + 72, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.isActivated = false;
			matter.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.showButtons();
			matterWrapper.hideButtons();
			editor.updateButtons(false);
		}, IOConfigButtonType.ENERGY);
		matter = new ButtonIOConfig(guiWidth + 48, guiHeight + 112, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			redstone.visible = false;
			items.isActivated = false;
			energy.isActivated = false;
			itemWrapper.hideButtons();
			energyWrapper.hideButtons();
			matterWrapper.showButtons();
			editor.updateButtons(false);
		}, IOConfigButtonType.MATTER);

		itemWrapper = new WrapperIOConfig(this, guiWidth + 137, guiHeight + 59, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientInventory.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientInventory.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientInventory.hasInput;
			}
			return false;
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientInventory.hasOutput;
			}
			return false;
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Item);
		energyWrapper = new WrapperIOConfig(this, guiWidth + 137, guiHeight + 59, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientEnergy.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientEnergy.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientEnergy.canReceive();
			}
			return false;
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientEnergy.canExtract();
			}
			return false;
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Energy);
		matterWrapper = new WrapperIOConfig(this, guiWidth + 137, guiHeight + 59, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientMatter.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientMatter.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientMatter.canReceive();
			}
			return false;
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.clientMatter.canExtract();
			}
			return false;
		}, () -> {
			TileTransporter transporter = getMenu().getTile();
			if (transporter != null) {
				return transporter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Matter);

		itemWrapper.initButtons();
		energyWrapper.initButtons();
		matterWrapper.initButtons();
		
		for(int i = 0; i < locationButtons.length; i++) {
			locationButtons[i] = (ButtonTransporterLocation) new ButtonTransporterLocation(guiWidth + 68, guiHeight + 32 + 22 * i, i,
					button -> {
						ButtonTransporterLocation locationButton = (ButtonTransporterLocation) button;
						TileTransporter transporter = getMenu().getTile();
						if (transporter != null) {
							if (locationButtons[locationButton.index].isActivated) {
								NetworkHandler.CHANNEL
										.sendToServer(new PacketUpdateTransporterLocationInfo(transporter.getBlockPos(), -1, PacketType.UPDATE_INDEX));
							} else {
								NetworkHandler.CHANNEL
										.sendToServer(new PacketUpdateTransporterLocationInfo(transporter.getBlockPos(), locationButton.index, PacketType.UPDATE_INDEX));
							}
							for(int j = 0; j < locationButtons.length; j++) {
								if(j != locationButton.index) {
									locationButtons[j].isActivated = false;
								}
							}
						}
					}, this, () -> getMenu().getTile()).setLeft();
		}
		
		for(int i = 0; i < editButtons.length; i++) {
			editButtons[i] = (ButtonEditTransporterLocation) new ButtonEditTransporterLocation(guiWidth + 180, guiHeight + 32 + 22 * i,
					button -> {
						ButtonEditTransporterLocation edit = (ButtonEditTransporterLocation) button;
						updateScreen(4);
						home.isActivated = false;
						settings.isActivated = false;
						upgrades.isActivated = false;
						redstone.visible = false;
						energy.isActivated = false;
						matter.isActivated = false;
						itemWrapper.hideButtons();
						energyWrapper.hideButtons();
						matterWrapper.hideButtons();

						for(int j = 0; j < locationButtons.length; j++) {
							locationButtons[j].visible = false;	
							editButtons[j].visible = false;
						}
						editButtons[edit.index].isPressed = false;

						editor.setCurrIndex(edit.index);
						editor.updateButtons(true);
					}, i).setRight();
		}
	
		editor = new WrapperTransporterLocationEditer(this, guiWidth, guiHeight, getMenu()::getTile);
		editor.initButtons();

		addRenderableWidget(close);
		addRenderableWidget(menu);
		addRenderableWidget(home);
		addRenderableWidget(settings);
		addRenderableWidget(upgrades);
		addRenderableWidget(redstone);
		addRenderableWidget(ioconfig);
		addRenderableWidget(items);
		addRenderableWidget(energy);
		addRenderableWidget(matter);
		for (ButtonIO button : itemWrapper.getButtons()) {
			addRenderableWidget(button);
		}
		for (ButtonIO button : energyWrapper.getButtons()) {
			addRenderableWidget(button);
		}
		for (ButtonIO button : matterWrapper.getButtons()) {
			addRenderableWidget(button);
		}
		for(int i = 0; i < editButtons.length; i++) {
			addRenderableWidget(editButtons[i]);
			addRenderableWidget(locationButtons[i]);
		}
		
		editor.addRenderingData(this);

		redstone.visible = false;
		items.visible = false;
		energy.visible = false;
		matter.visible = false;
		itemWrapper.hideButtons();
		energyWrapper.hideButtons();
		matterWrapper.hideButtons();
		editor.updateButtons(false);
		
	}
	
	@Override
	protected void containerTick() {
		super.containerTick();
		editor.tickEditBoxes();
	}

	private void toggleBarOpen() {
		EXTENDED = !EXTENDED;
	}

	private void updateScreen(int screenNumber) {
		this.screenNumber = screenNumber;
		updateSlotActivity(this.screenNumber);
	}

	@Override
	public int getScreenNumber() {
		return screenNumber;
	}
	
	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
		if(this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && editor.areEditBoxesActive()) {
			return false;
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

}
