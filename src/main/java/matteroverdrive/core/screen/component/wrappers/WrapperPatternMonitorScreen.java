package matteroverdrive.core.screen.component.wrappers;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.client.screen.ScreenPatternMonitor;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.screen.component.button.ButtonItemPattern;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.screen.component.edit_box.EditBoxOverdrive;
import matteroverdrive.core.screen.component.edit_box.EditBoxOverdrive.EditBoxTextures;
import matteroverdrive.core.screen.component.edit_box.EditBoxSearchbar;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WrapperPatternMonitorScreen {

	private final ScreenPatternMonitor screen;
	private final int x;
	private final int y;
	private final ButtonItemPattern[][] patterns = new ButtonItemPattern[4][6];
	private EditBoxSearchbar searchbar;
	public ButtonItemPattern selectedItem;

	private ButtonOverdrive incVal;
	private ButtonOverdrive decVal;
	private EditBoxOverdrive orderQuantityBox;
	private ButtonGeneric sendOrder;

	private int topRowIndex = 0;
	private int lastRowCount = 0;

	private String searchContents = "";

	private static final Component PLUS = Component.literal("+");
	private static final Component MINUS = Component.literal("-");

	private static final MutableComponent ORDER = UtilsText.tooltip("order");

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
		searchbar.setTextColor(Colors.WHITE.getColor());
		searchbar.setTextColorUneditable(Colors.WHITE.getColor());

		orderQuantityBox = new EditBoxOverdrive(EditBoxTextures.OVERDRIVE_EDIT_BOX, screen, guiWidth + x + 44, guiHeight + y + 123, 54, 15);
		orderQuantityBox.setTextColor(Colors.WHITE.getColor());
		orderQuantityBox.setTextColorUneditable(Colors.WHITE.getColor());
		orderQuantityBox.setMaxLength(4);
		orderQuantityBox.setResponder(this::handleQuantityBar);
		orderQuantityBox.setFilter(EditBoxOverdrive.POSITIVE_INTEGER_BOX);
		orderQuantityBox.setValue(1 + "");

		int butOffX = -2;
		int butOffY = 17;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 6; j++) {
				patterns[i][j] = new ButtonItemPattern(screen, x + butOffX + 25 * j, y + butOffY + 24 * i, (button) -> {
					ButtonItemPattern pattern = (ButtonItemPattern) button;
					if (selectedItem.getPattern() != null && selectedItem.getPattern().isSame(pattern.getPattern())) {
						selectedItem.setPattern(null);
						selectedItem.isActivated = false;
					} else {
						selectedItem.setPattern(pattern.getPattern());
						selectedItem.isActivated = true;
					}
					orderQuantityBox.setValue("1");
				}, renderer, i, j, this);
			}
		}
		selectedItem = new ButtonItemPattern(screen, x - 9, y + 119, (button) -> {
			ButtonItemPattern pattern = (ButtonItemPattern) button;
			pattern.isActivated = true;
			pattern.setPattern(null);
		}, renderer, -1, -1, this).setNoHover();
		incVal = new ButtonOverdrive(screen, x + 98, y + 123, 15, 15, () -> PLUS, (button) -> {
			String order = orderQuantityBox.getValue();
			int orderVal = 1;
			if (!order.isEmpty()) {
				orderVal = Integer.parseInt(order);
			}
			int inc = Screen.hasShiftDown() ? 16 : 1;
			orderVal = Mth.clamp(orderVal + inc, 1, 9999999);
			orderQuantityBox.setValue(orderVal + "");
		}).setRight().setColor(Colors.WHITE.getColor()).setSound(getIncDecSound());
		decVal = new ButtonOverdrive(screen, x + 29, y + 123, 15, 15, () -> MINUS, (button) -> {
			String order = orderQuantityBox.getValue();
			int orderVal = 1;
			if (!order.isEmpty()) {
				orderVal = Integer.parseInt(order);
			}
			int inc = Screen.hasShiftDown() ? 16 : 1;
			orderVal = Mth.clamp(orderVal - inc, 1, 9999999);
			orderQuantityBox.setValue(orderVal + "");
		}).setLeft().setColor(Colors.WHITE.getColor()).setSound(getIncDecSound());
		sendOrder = new ButtonGeneric(screen, x + 129, y + 125, ButtonType.ORDER_ITEMS, OverdriveScreenComponent.NO_TEXT, (button) -> {
			Minecraft minecraft = Minecraft.getInstance();
			ItemPatternWrapper wrapper = selectedItem.getPattern();
			if (wrapper == null || wrapper.isAir()) {
				minecraft.getSoundManager()
						.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F));
				return;
			}
			double value = MatterRegister.INSTANCE.getClientMatterValue(new ItemStack(wrapper.getItem()));
			// safety check for data pack fuckery
			if (value <= 0.0) {
				minecraft.getSoundManager()
						.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F));
				return;
			}
			TilePatternMonitor monitor = screen.getMenu().getTile();
			if (monitor == null) {
				minecraft.getSoundManager()
						.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F));
				return;
			}
			String order = orderQuantityBox.getValue();
			int orderVal = 1;
			if (!order.isEmpty()) {
				orderVal = Integer.parseInt(order);
			}
			if (monitor.postOrderToNetwork(wrapper, orderVal, true, true)) {
				minecraft.getSoundManager()
						.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_SOFT1.get(), 1.0F));
			} else {
				minecraft.getSoundManager()
						.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F));
			}

			// Reset order window.
			orderQuantityBox.setValue("1");

			selectedItem.setPattern(null);
			selectedItem.isActivated = false;

			if (Minecraft.getInstance().player != null) {
				Minecraft.getInstance().player.sendSystemMessage(Component.literal("Sent request to queue.")
					.withStyle(ChatFormatting.AQUA));
			}
		}, (button, stack, x, y) -> screen.renderTooltip(stack, ORDER, x, y));
		screen.addEditBox(searchbar);
		screen.addEditBox(orderQuantityBox);
		screen.addButton(incVal);
		screen.addButton(decVal);
		screen.addButton(sendOrder);
		for (int i = 3; i >= 0; i--) {
			for (int j = 5; j >= 0; j--) {
				screen.addButton(patterns[i][j]);
			}
		}
		screen.addButton(selectedItem);
		for (int i = 3; i >= 0; i--) {
			for (int j = 5; j >= 0; j--) {
				patterns[i][j].visible = false;
			}
		}
	}

	public void tick() {
		searchbar.tick();
		orderQuantityBox.tick();
		TilePatternMonitor monitor = screen.getMenu().getTile();
		List<ItemPatternWrapper> patterns = new ArrayList<>();
		if (monitor != null) {
			patterns = monitor.getStoredPatterns(true);
			patterns.sort((pattern1, pattern2) -> pattern1.getItem().getDescription().getString()
				.compareToIgnoreCase(pattern2.getItem().getDescription().getString()));
		}
		List<ItemPatternWrapper> searchedFor = new ArrayList<>();
		if (!searchContents.isEmpty()) {
			for (ItemPatternWrapper wrapper : patterns) {
				if (wrapper.getItem().getDescription().getString().toLowerCase()
						.contains(searchContents.toLowerCase())) {
					searchedFor.add(wrapper);
				}
			}
		} else {
			searchedFor = patterns;
		}
		lastRowCount = (int) Math.ceil((double) searchedFor.size() / 6.0D);
		int index;
		for (ButtonItemPattern[] arr : this.patterns) {
			for (ButtonItemPattern button : arr) {
				index = (topRowIndex + button.getRow()) * 6 + button.getCol();
				if (index < searchedFor.size()) {
					button.setPattern(searchedFor.get(index));
				} else {
					button.setPattern(null);
				}
				button.visible = button.isFilled();
			}
		}
		ScreenComponentVerticalSlider slider = screen.slider;
		if (lastRowCount > 4) {
			slider.updateActive(true);
			if (!slider.isSliderHeld()) {
				int moveRoom = screen.slider.getHeight() - 15 - 4;

				// int moveRoom = 102 - 2;
				double moved = (double) topRowIndex / (lastRowCount - 4.0D);
				slider.setSliderYOffset((int) ((double) moveRoom * moved));
			}
		} else {
			slider.updateActive(false);
			slider.setSliderYOffset(0);
			topRowIndex = 0;
		}

	}

	// pos for down, neg for up
	public void handleMouseScroll(int dir) {
		if (Screen.hasControlDown()) {
			dir *= 4;
		}
		int lastRowIndex = lastRowCount - 1;
		if (lastRowCount > 4) {
			// check in case something borked
			if (topRowIndex >= lastRowCount) {
				topRowIndex = lastRowIndex - 3;
			}
			topRowIndex = Mth.clamp(topRowIndex + dir, 0, lastRowIndex - 3);
		} else {
			topRowIndex = 0;
		}
	}

	public void updateButtons(boolean visible) {
		searchbar.visible = visible;
		orderQuantityBox.visible = visible;
		ButtonItemPattern button;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 6; j++) {
				button = patterns[i][j];
				button.visible = visible && button.isFilled();
			}
		}
		selectedItem.visible = visible;
		incVal.visible = visible;
		decVal.visible = visible;
		sendOrder.visible = visible;
	}

	private void handleSearchBar(String string) {
		searchContents = string;
	}

	private void handleQuantityBar(String string) {
		if (string.isEmpty()) {
			orderQuantityBox.setValue("1");
		}
	}

	public Consumer<Integer> getSliderClickedConsumer() {
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = screen.slider;
			if (slider.isSliderActive()) {
				int sliderY = slider.y;
				int sliderHeight = slider.getHeight();
				int mouseHeight = mouseY - sliderY;
				if (mouseHeight >= sliderHeight - 4 - 15) {
					topRowIndex = lastRowCount - 4;
					slider.setSliderYOffset(sliderHeight - 4 - 15);
				} else if (mouseHeight <= 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else {
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - 4) * heightRatio);
					int moveRoom = screen.slider.getHeight() - 15 - 4;
					double moved = (double) topRowIndex / (lastRowCount - 4.0D);
					screen.slider.setSliderYOffset((int) ((double) moveRoom * moved));
				}
			}
		};
	}

	public Consumer<Integer> getSliderDraggedConsumer() {
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = screen.slider;
			if (slider.isSliderActive()) {
				int sliderY = slider.y;
				int sliderHeight = slider.getHeight();
				if (mouseY <= sliderY + 2) {
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

	public boolean isSearchBarSelected() {
		return searchbar.isFocused();
	}

	private Consumer<SoundManager> getIncDecSound() {
		float pitch = MatterOverdrive.RANDOM.nextFloat(0.9F, 1.1F);
		return manager -> manager.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_SOFT0.get(), 1.0F, pitch));
	}

}
