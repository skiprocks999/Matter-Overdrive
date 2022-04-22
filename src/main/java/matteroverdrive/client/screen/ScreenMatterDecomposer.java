package matteroverdrive.client.screen;

import java.util.HashSet;

import matteroverdrive.common.inventory.InventoryMatterDecomposer;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.PacketUpdateRedstoneMode;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.IOConfigWrapper;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentProgress;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonIOConfig;
import matteroverdrive.core.screen.component.button.ButtonMenuBar;
import matteroverdrive.core.screen.component.button.ButtonMenuOption;
import matteroverdrive.core.screen.component.button.ButtonRedstoneMode;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonIO;
import matteroverdrive.core.screen.component.button.ButtonIOConfig.IOConfigButtonType;
import matteroverdrive.core.screen.component.button.ButtonMenuOption.MenuButtonType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMatterDecomposer extends GenericScreen<InventoryMatterDecomposer> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	private ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption settings;
	private ButtonMenuOption upgrades;
	private ButtonMenuOption ioconfig;

	private ButtonRedstoneMode redstone;

	private ButtonIOConfig items;
	private ButtonIOConfig energy;
	private ButtonIOConfig matter;

	private IOConfigWrapper itemWrapper;
	private IOConfigWrapper energyWrapper;
	private IOConfigWrapper matterWrapper;

	private int screenNumber = 0;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;

	public ScreenMatterDecomposer(InventoryMatterDecomposer menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
		components.add(new ScreenComponentProgress(() -> {
			return 0;
		}, this, -4, 48, new int[] { 0 }));
		components.add(new ScreenComponentCharge(() -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null) {
				return matter.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null) {
				return matter.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null && matter.clientRunning) {
				return matter.clientEnergyUsage;
			}
			return 0;
		}, this, 130, 35, new int[] { 0 }));
		components.add(new ScreenComponentCharge(() -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null) {
				return matter.clientMatter.getMatterStored();
			}
			return 0;
		}, () -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null) {
				return matter.clientMatter.getMaxMatterStored();
			}
			return 0;
		}, () -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null && matter.clientRunning) {
				return matter.clientRecipeValue;
			}
			return 0;
		}, this, 58, 35, new int[] { 0 }).setGenerator().setMatter());
		components.add(new ScreenComponentIndicator(() -> {
			TileMatterDecomposer matter = menu.getTile();
			if (matter != null) {
				return matter.clientRunning;
			}
			return false;
		}, this, -31, 159, new int[] { 0, 1, 2, 3 }));
		components.add(new ScreenComponentHotbarBar(this, 3, 143, new int[] { 0, 1, 2, 3 }));
		components.add(new ScreenComponentLabel(this, 73, 37, new int[] { 1 },
				new TranslatableComponent("gui.matteroverdrive.redstone"), UtilsRendering.getRGBA(1, 169, 226, 251)));
		components.add(new ScreenComponentLabel(this, 43, 42, new int[] { 3 },
				new TranslatableComponent("gui.matteroverdrive.ioitems"), UtilsRendering.getRGBA(1, 169, 226, 251)));
		components.add(new ScreenComponentLabel(this, 43, 80, new int[] { 3 },
				new TranslatableComponent("gui.matteroverdrive.ioenergy"), UtilsRendering.getRGBA(1, 169, 226, 251)));
		components.add(new ScreenComponentLabel(this, 43, 122, new int[] { 3 },
				new TranslatableComponent("gui.matteroverdrive.iomatter"), UtilsRendering.getRGBA(1, 169, 226, 251)));
	}

	@Override
	protected void init() {
		super.init();
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		close = new ButtonGeneric(guiWidth + 170, guiHeight + 6, ButtonType.CLOSE_SCREEN, button -> onClose());
		menu = new ButtonMenuBar(guiWidth + 175, guiHeight + 33, EXTENDED, button -> {
			toggleBarOpen();
			home.visible = !home.visible;
			settings.visible = !settings.visible;
			upgrades.visible = !upgrades.visible;
			ioconfig.visible = !ioconfig.visible;
		}, this);
		home = new ButtonMenuOption(guiWidth + 180, guiHeight + FIRST_HEIGHT, this, button -> {
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
		settings = new ButtonMenuOption(guiWidth + 180, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS, this, button -> {
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
		upgrades = new ButtonMenuOption(guiWidth + 180, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS * 2, this, button -> {
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
		ioconfig = new ButtonMenuOption(guiWidth + 180, guiHeight + FIRST_HEIGHT + BETWEEN_MENUS * 3, this, button -> {
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
		redstone = new ButtonRedstoneMode(guiWidth + 11, guiHeight + 32, button -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(matter.getBlockPos()));
			}
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientRedstoneMode;
			}
			return 0;
		});
		items = new ButtonIOConfig(guiWidth + 11, guiHeight + 32, button -> {
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
		energy = new ButtonIOConfig(guiWidth + 11, guiHeight + 72, button -> {
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
		matter = new ButtonIOConfig(guiWidth + 11, guiHeight + 112, button -> {
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

		itemWrapper = new IOConfigWrapper(this, guiWidth + 100, guiHeight + 59, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> { 
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.hasInput;
			}
			return false;
		}, () -> { 
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientInventory.hasOutput;
			}
			return false;
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Item);
		energyWrapper = new IOConfigWrapper(this, guiWidth + 100, guiHeight + 59, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> { 
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.canReceive();
			}
			return false;
		}, () -> { 
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.canExtract();
			}
			return false;
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Energy);
		matterWrapper = new IOConfigWrapper(this, guiWidth + 100, guiHeight + 59, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> { 
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.canReceive();
			}
			return false;
		}, () -> { 
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientMatter.canExtract();
			}
			return false;
		}, () -> {
			TileMatterDecomposer matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.Matter);

		itemWrapper.initButtons();
		energyWrapper.initButtons();
		matterWrapper.initButtons();

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

		redstone.visible = false;
		items.visible = false;
		energy.visible = false;
		matter.visible = false;
		itemWrapper.hideButtons();
		energyWrapper.hideButtons();
		matterWrapper.hideButtons();
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

}
