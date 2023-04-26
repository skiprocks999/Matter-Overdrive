package matteroverdrive.core.screen.component.wrappers;

import matteroverdrive.client.screen.ScreenDiscManipulator;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.tile.matter_network.TileDiscManipulator;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.screen.component.ScreenComponentPatternInfo;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public class WrapperWipePatterns {

	private final ScreenDiscManipulator screen;
	private final ButtonOverdrive[] eraseButtons = new ButtonOverdrive[3];
	private final ScreenComponentPatternInfo[] patternInfo = new ScreenComponentPatternInfo[3];
	private final int x;
	private final int y;
	
	public WrapperWipePatterns(ScreenDiscManipulator screen, int x, int y) {
		this.screen = screen;
		this.x = x;
		this.y = y;
	}
	
	public void init(ItemRenderer renderer) {
		for(int i = 0; i < 3; i++) {
			final int index = i;
			eraseButtons[i] = new ButtonOverdrive(screen, x + 102, y + 1 + 21 * i, 50, 18, () -> UtilsText.tooltip("erasepattern"), button -> {
				TileDiscManipulator manipulator = screen.getMenu().getTile();
				if(manipulator == null) {
					return;
				}
				CapabilityInventory inv = manipulator.getInventoryCap();
				ItemStack drive = inv.getStackInSlot(0);
				if(drive.isEmpty()) {
					return;
				}
				ItemPatternDrive pattern = (ItemPatternDrive) drive.getItem();
				if(pattern.isFused(drive)) {
					return;
				}
				drive.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS).ifPresent(h -> {
					h.getStoredPatterns()[index] = ItemPatternWrapper.EMPTY;
					manipulator.getPropertyManager().updateServerBlockEntity(manipulator.capInventoryProp, inv.serializeNBT());
				});
			}).setSound(manager -> manager.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_ELECTRIC_ARC_0.get(), 1.0F)));
			patternInfo[i] = new ScreenComponentPatternInfo(screen, x + 5, y + 2 + 21 * i, new int[] { 0 }, renderer, i);
		}

	}
	
	public void initButtons() {
		for(int i = 0; i < 3; i++) {
			screen.addButton(eraseButtons[i]);
			eraseButtons[i].visible = false;
		}
		for(int i = 0; i < 3; i++) {
			screen.addScreenComponent(patternInfo[i]);
		}
	}
	
	public void updateButtonVisibility(boolean show) {
		boolean factoredShow = show;
		for(int i = 0; i < 3; i++) {
			if(show) {
				TileDiscManipulator tile = screen.getMenu().getTile();
				if(tile != null) {
					ItemStack patternDrive = tile.getInventoryCap().getStackInSlot(0);
					if(!patternDrive.isEmpty() && patternDrive.getItem() instanceof ItemPatternDrive pattern) {
						if(pattern.isFused(patternDrive)) {
							factoredShow = false;
						} else {
							LazyOptional<ICapabilityItemPatternStorage> lazy = patternDrive.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
							if(lazy.isPresent()) {
								ICapabilityItemPatternStorage storage = lazy.resolve().get();
								if(storage.getStoredPatterns()[i].isAir()) {
									factoredShow = false;
								} else {
									factoredShow = show;
								}
							}	
						}
					}
				}
			}
			eraseButtons[i].visible = factoredShow;
		}
	}
	
}
