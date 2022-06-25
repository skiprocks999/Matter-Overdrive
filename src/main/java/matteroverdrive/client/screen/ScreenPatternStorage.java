package matteroverdrive.client.screen;

import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.tile.matter_network.TilePatternStorage;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateRedstoneMode;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentFillArea;
import matteroverdrive.core.screen.component.ScreenComponentHotbarBar;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentProgress;
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
import net.minecraft.world.item.ItemStack;

public class ScreenPatternStorage extends GenericScreen<InventoryPatternStorage> {

	private static boolean EXTENDED = false;

	private ButtonGeneric close;

	public ButtonMenuBar menu;

	private ButtonMenuOption home;
	private ButtonMenuOption settings;

	private ButtonRedstoneMode redstone;

	private int screenNumber = 0;

	private static final int BETWEEN_MENUS = 26;
	private static final int FIRST_HEIGHT = 40;
	
	public ScreenPatternStorage(InventoryPatternStorage menu, Inventory playerinventory, Component title) {
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
		
		redstone = new ButtonRedstoneMode(this, 48, 32, button -> {
			TilePatternStorage matter = getMenu().getTile();
			if (matter != null) {
				NetworkHandler.CHANNEL.sendToServer(new PacketUpdateRedstoneMode(matter.getBlockPos()));
			}
		}, () -> {
			TilePatternStorage matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientRedstoneMode;
			}
			return 0;
		});
		
		addButton(close);
		addButton(menu);
		addButton(home);
		addButton(settings);
		addButton(redstone);
		
		redstone.visible = false;
		
		addScreenComponent(new ScreenComponentFillArea(this, 15, 59, 2, 26, new int[] { 0 }, UtilsRendering.GUI_STANDARD));
		addScreenComponent(new ScreenComponentProgress(() -> 0, this, 8, 61, new int[] { 0 }).vertical());
		
		addScreenComponent(new ScreenComponentCharge(() -> {
			TilePatternStorage matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getEnergyStored();
			}
			return 0;
		}, () -> {
			TilePatternStorage matter = getMenu().getTile();
			if (matter != null) {
				return matter.clientEnergy.getMaxEnergyStored();
			}
			return 0;
		}, () -> {
			TilePatternStorage matter = getMenu().getTile();
			if (matter != null && matter.clientTilePowered) {
				int drives = 0;
				for(ItemStack stack : matter.getDrives(true, false)) {
					if(stack.getItem() instanceof ItemPatternDrive) {
						drives++;
					}
				}
				return TilePatternStorage.BASE_USAGE + drives * TilePatternStorage.USAGE_PER_DRIVE;
			}
			return 0;
		}, this, 167, 35, new int[] { 0 }));
		addScreenComponent(new ScreenComponentHotbarBar(this, 40, 143, new int[] { 0, 1 }));
		addScreenComponent(new ScreenComponentLabel(this, 110, 37, new int[] { 1 }, UtilsText.gui("redstone"),
				UtilsRendering.TEXT_BLUE));
		addScreenComponent(new ScreenComponentIndicator(() -> {
			TilePatternStorage inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.clientTilePowered;
			}
			return false;
		}, this, 6, 159, new int[] { 0, 1}));
		
		
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
