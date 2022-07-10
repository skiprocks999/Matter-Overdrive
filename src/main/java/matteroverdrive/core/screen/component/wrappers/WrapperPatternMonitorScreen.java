package matteroverdrive.core.screen.component.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import matteroverdrive.client.screen.ScreenPatternMonitor;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
import matteroverdrive.core.screen.component.button.ButtonItemPattern;
import matteroverdrive.core.screen.component.edit_box.EditBoxSearchbar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;

public class WrapperPatternMonitorScreen {

	private final ScreenPatternMonitor screen;
	private final int x;
	private final int y;
	private ButtonItemPattern[][] patterns = new ButtonItemPattern[4][6];
	private EditBoxSearchbar searchbar;
	
	private int topRowIndex = 0;
	private int lastRowCount = 0;
	
	private String searchContents = "";
	
	public WrapperPatternMonitorScreen(ScreenPatternMonitor screen, int x, int y) {
		this.screen = screen;
		this.x = x;
		this.y = y;
	}
	
	public void initButtons(ItemRenderer renderer) {
		int guiWidth = screen.getXPos();
		int guiHeight = screen.getYPos();
		searchbar = new EditBoxSearchbar(screen, guiWidth + x, guiHeight + y, 134, 14, 167);
		searchbar.setResponder(this::handleSearchBar);
		int butOffX = -2;
		int butOffY = 17;
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 6; j++) {
				patterns[i][j] = new ButtonItemPattern(screen, x + butOffX + 25 * j, y + butOffY + 24 * i, (button) -> {
					ButtonItemPattern pattern = (ButtonItemPattern) button;
					for(ButtonItemPattern[] arr : this.patterns) {
						for(ButtonItemPattern but : arr) {
							if(!pattern.isSame(but)) {
								but.isActivated = false;
							} 
						}
					}
				}, renderer, i, j);
			}
		}
		screen.addEditBox(searchbar);
		for(int i = 3; i >= 0; i--) {
			for(int j = 5; j >= 0; j--) {
				screen.addButton(patterns[i][j]);
			}
		}
	}
	
	public void tick() {
		TilePatternMonitor monitor = screen.getMenu().getTile();
		List<ItemPatternWrapper> patterns = new ArrayList<>();
		if(monitor != null) {
			NetworkMatter matter = monitor.getConnectedNetwork();
			if(matter != null) {
				patterns = matter.getStoredPatterns(true, true);
				Collections.sort(patterns, new Comparator<ItemPatternWrapper>() {
					@Override
					public int compare(ItemPatternWrapper pattern1, ItemPatternWrapper pattern2) {
						return pattern1.getItem().getDescription().getString()
								.compareToIgnoreCase(pattern2.getItem().getDescription().getString());
					}
				});
			}
		}
		List<ItemPatternWrapper> searchedFor = new ArrayList<>();
		if(searchContents.length() > 0) {
			for(ItemPatternWrapper wrapper : patterns) {
				if(wrapper.getItem().getDescription().getString().toLowerCase().contains(searchContents.toLowerCase())) {
					searchedFor.add(wrapper);
				}
			}
		} else {
			searchedFor = patterns;
		}
		lastRowCount = (int) Math.ceil((double) searchedFor.size() / 6.0D);
		int index;
		for(ButtonItemPattern[] arr : this.patterns) {
			for(ButtonItemPattern button : arr) {
				index = (topRowIndex + button.getRow()) * 6 + button.getCol();
				if(index < searchedFor.size()) {
					button.setPattern(searchedFor.get(index));
				} else {
					button.setPattern(null);
				}
				button.visible = button.isFilled();
			}
		}
		ScreenComponentVerticalSlider slider = screen.slider;
		if(lastRowCount > 4) {
			slider.updateActive(true);
			if(!slider.isSliderHeld()) {
				int moveRoom = screen.slider.getHeight() - 15 - 4;
				
				//int moveRoom = 102 - 2;
				double moved = (double) topRowIndex / (double) (lastRowCount - 4.0D);
				slider.setSliderYOffset((int) ((double) moveRoom * moved));
			}
		} else {
			slider.updateActive(false);
		}
		
	}
	
	//pos for down, neg for up
	public void handleMouseScroll(int dir) {
		if(Screen.hasControlDown()) {
			dir*= 4;
		}
		int lastRowIndex = lastRowCount - 1;
		if(lastRowCount > 4) {
			//check in case something borked
			if(topRowIndex >= lastRowCount) {
				topRowIndex = lastRowIndex - 3;
			}
			topRowIndex = Mth.clamp(topRowIndex += dir, 0, lastRowIndex - 3);
		} else {
			topRowIndex = 0;
		}
	}
	
	public void updateButtons(boolean visible) {
		searchbar.visible = visible;
		ButtonItemPattern button;
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 6; j++) {
				button = patterns[i][j];
				button.visible = visible && button.isFilled();
			}
		}
	}
	
	private void handleSearchBar(String string) {
		searchContents = string;
	}
	
	public Consumer<Integer> getSliderClickedConsumer() {
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = screen.slider;
			if(slider.isSliderActive()) {
				int sliderY = slider.y;
				int sliderHeight = slider.getHeight();
				int mouseHeight = mouseY - sliderY;
				if(mouseHeight >= sliderHeight - 4 - 15) {
					topRowIndex = lastRowCount - 4;
					slider.setSliderYOffset(sliderHeight - 4 - 15);
				} else if (mouseHeight <= 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else {
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - 4) * heightRatio);
					int moveRoom = screen.slider.getHeight() - 15 - 4;
					double moved = (double) topRowIndex / (double) (lastRowCount - 4.0D);
					screen.slider.setSliderYOffset((int) ((double) moveRoom * moved));
				}
			}
		};
	}
	
	public Consumer<Integer> getSliderDraggedConsumer(){
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = screen.slider;
			if(slider.isSliderActive()) {
				int sliderY = slider.y;
				int sliderHeight = slider.getHeight();
				if(mouseY <= sliderY + 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else if (mouseY >= sliderY + sliderHeight - 4 - 15) {
					topRowIndex = lastRowCount - 4;
					slider.setSliderYOffset(sliderHeight - 4 - 15);
				} else {
					int mouseHeight = mouseY - sliderY;
					slider.setSliderYOffset(mouseHeight);
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - 4) * heightRatio);
				}
			}
		};
	}
	
}
