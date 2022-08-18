package matteroverdrive.client.screen;

import java.util.HashSet;

import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateCapabilitySides.CapabilityType;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateRedstoneMode;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentPatternHolder;
import matteroverdrive.core.screen.component.ScreenComponentUpgradeInfo;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
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
import matteroverdrive.core.screen.component.wrappers.WrapperMatterReplicatorOrders;
import matteroverdrive.core.screen.types.GenericOverdriveScreen;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMatterReplicator extends GenericOverdriveScreen<InventoryMatterReplicator> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption settings;
	private ButtonMenuOption upgrades;
	private ButtonMenuOption ioconfig;
	private ButtonMenuOption orders;

	private ButtonRedstoneMode redstone;

	private ButtonIOConfig items;
	private ButtonIOConfig energy;
	private ButtonIOConfig matter;

	private WrapperIOConfig itemWrapper;
	private WrapperIOConfig energyWrapper;
	private WrapperIOConfig matterWrapper;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;
	
	public ScreenComponentVerticalSlider slider;
	public WrapperMatterReplicatorOrders queued;

	
	public ScreenMatterReplicator(InventoryMatterReplicator menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}
	
	@Override
	protected void containerTick() {
		super.containerTick();
		if(screenNumber == 4) {
			queued.tick();
		}
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
			orders.visible = !orders.visible;
		});
		home = new ButtonMenuOption(this, 217, FIRST_HEIGHT, button -> {
			updateScreen(0);
			settings.isActivated = false;
			upgrades.isActivated = false;
			ioconfig.isActivated = false;
			orders.isActivated = false;
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
			queued.updateButtons(false);
		}, MenuButtonType.HOME, menu, true);
		settings = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS, button -> {
			updateScreen(1);
			home.isActivated = false;
			upgrades.isActivated = false;
			ioconfig.isActivated = false;
			orders.isActivated = false;
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
			queued.updateButtons(false);
		}, MenuButtonType.SETTINGS, menu, false);
		upgrades = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 2, button -> {
			updateScreen(2);
			home.isActivated = false;
			settings.isActivated = false;
			ioconfig.isActivated = false;
			orders.isActivated = false;
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
			queued.updateButtons(false);
		}, MenuButtonType.UPGRADES, menu, false);
		ioconfig = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 3, button -> {
			updateScreen(3);
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			orders.isActivated = false;
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
			queued.updateButtons(false);
		}, MenuButtonType.IO, menu, false);
		orders = new ButtonMenuOption(this, 217, FIRST_HEIGHT + BETWEEN_MENUS * 4, button -> {
			updateScreen(4);
			home.isActivated = false;
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
			queued.updateButtons(true);
		}, MenuButtonType.TASKS, menu, false);
		redstone = new ButtonRedstoneMode(this, 48, 32, button -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(matter.getBlockPos()));
			}
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.currRedstoneMode;
			}
			return 0;
		});
		items = new ButtonIOConfig(this, 48, 32, button -> {
			home.isActivated = false;
			settings.isActivated = false;
			upgrades.isActivated = false;
			orders.isActivated = false;
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
			orders.isActivated = false;
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
			orders.isActivated = false;
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
				return matter.getInventoryCap().getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getInventoryCap().getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getInventoryCap().hasInput;
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getInventoryCap().hasOutput;
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.ITEM);
		energyWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getEnergyStorageCap().getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getEnergyStorageCap().getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getEnergyStorageCap().canReceive();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getEnergyStorageCap().canExtract();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.ENERGY);
		matterWrapper = new WrapperIOConfig(this, 137, 59, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().canReceive();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().canExtract();
			}
			return false;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.MATTER);

		itemWrapper.initButtons();
		energyWrapper.initButtons();
		matterWrapper.initButtons();
		
		queued = new WrapperMatterReplicatorOrders(this, 48, 32, new int[] { 4 } );

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
		addButton(orders);
		for (ButtonIO button : itemWrapper.getButtons()) {
			addButton(button);
		}
		for (ButtonIO button : energyWrapper.getButtons()) {
			addButton(button);
		}
		for (ButtonIO button : matterWrapper.getButtons()) {
			addButton(button);
		}
		
		queued.initButtons(itemRenderer);

		redstone.visible = false;
		items.visible = false;
		energy.visible = false;
		matter.visible = false;
		itemWrapper.hideButtons();
		energyWrapper.hideButtons();
		matterWrapper.hideButtons();
		queued.updateButtons(false);
		
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getEnergyStorageCap().getEnergyStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getEnergyStorageCap().getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null && matter.isRunning) {
				return matter.getCurrentPowerUsage();
			}
			return 0;
		}, this, 167, 35, new int[] { 0 }));
		addScreenComponent(new ScreenComponentCharge(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getMatterStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getMaxMatterStored();
			}
			return 0;
		}, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null && matter.isRunning) {
				return matter.clientRecipeValue;
			}
			return 0;
		}, this, 133, 35, new int[] { 0 }).setMatter());
		addScreenComponent(new ScreenComponentPatternHolder(this, 5, 45, new int[] { 0 }, () -> {
			return getMenu().getTile();
		}, itemRenderer, () -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return (double) matter.clientProgress / matter.getProcessingTime();
			}
			return 0;
		}));
		addScreenComponent(new ScreenComponentIndicator(() -> {
			TileMatterReplicator matter = getMenu().getTile();
			if (matter != null) {
				return matter.isRunning;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1, 2, 3, 4 }));
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
		slider = new ScreenComponentVerticalSlider(this, 9, 39, 102, new int[] { 4 });
		slider.setClickConsumer(queued.getSliderClickedConsumer());
		slider.setDragConsumer(queued.getSliderDraggedConsumer());
		addScreenComponent(slider);
	}

	private void toggleBarOpen() {
		EXTENDED = !EXTENDED;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if(queued != null && screenNumber == 4) {
			if(delta > 0) {
				//scroll up
				queued.handleMouseScroll(-1);
			} else if (delta < 0) {
				// scroll down
				queued.handleMouseScroll(1);
			}
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);
		if(slider != null && screenNumber == 4) {
			slider.mouseMoved(mouseX, mouseY);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(slider != null && screenNumber == 4) {
			slider.mouseClicked(mouseX, mouseY, button);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(slider != null && screenNumber == 4) {
			slider.mouseReleased(mouseX, mouseY, button);
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

}
