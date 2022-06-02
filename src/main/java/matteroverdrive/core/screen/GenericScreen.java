package matteroverdrive.core.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.inventory.slot.IToggleableSlot;
import matteroverdrive.core.inventory.slot.SlotUpgrade;
import matteroverdrive.core.screen.component.ScreenComponentSlot;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.edit_box.EditBoxOverdrive;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class GenericScreen<T extends GenericInventory> extends AbstractContainerScreen<T> {

	protected static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(
			References.ID + ":textures/gui/base/base_gui.png");
	private List<OverdriveScreenComponent> components = new ArrayList<>();
	protected int playerInvOffset = 0;

	private static final int OFFSET = 37;

	public static final int GUI_WIDTH = 224;
	public static final int GUI_HEIGHT = 176;

	public GenericScreen(T menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
		setScreenParams();
	}
	
	@Override
	protected void init() {
		super.init();
		for (Slot slot : menu.slots) {
			addScreenComponent(createScreenSlot(slot));
		}
		updateComponentActivity(getScreenNumber());
	}

	protected ScreenComponentSlot createScreenSlot(Slot slot) {
		if (slot instanceof SlotUpgrade upgrade) {
			return new ScreenComponentSlot(upgrade.getSlotType(), upgrade.getIconType(), this, slot.x - 1, slot.y - 1,
					upgrade.getScreenNumbers()).setUpgrades(upgrade.getUpgrades());
		} else if (slot instanceof IToggleableSlot type) {
			return new ScreenComponentSlot(type.getSlotType(), type.getIconType(), this, slot.x - 1, slot.y - 1,
					type.getScreenNumbers());
		}
		return new ScreenComponentSlot(SlotType.SMALL, this, slot.x - 1, slot.y - 1, new int[] { 0 });
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		updateComponentActivity(getScreenNumber());
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack stack, int x, int y) {
		float length = font.width(this.title);
		float offset = (144.0F - length) / 2.0F;
		this.font.draw(stack, this.title, (float) this.titleLabelX + 3 + offset, (float) this.titleLabelY + 1,
				UtilsRendering.TITLE_BLUE);
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(DEFAULT_BACKGROUND);
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		blit(stack, guiWidth, guiHeight, 0, 0, imageWidth, imageHeight);
	}

	public int getXPos() {
		return (width - imageWidth) / 2;
	}

	public int getYPos() {
		return (height - imageHeight) / 2;
	}

	public Font getFontRenderer() {
		return font;
	}

	public int[] getAxisAndGuiWidth(int mouseX, int mouseY) {
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		return new int[] { guiWidth, guiHeight, mouseX - guiWidth, mouseY - guiHeight };
	}

	public void updateComponentActivity(int screenNum) {
		for (OverdriveScreenComponent component : components) {
			component.updateVisiblity(screenNum);
		}
		for (Slot slot : menu.slots) {
			if (slot instanceof IToggleableSlot toggle) {
				toggle.setActive(toggle.isScreenNumber(screenNum));
			}
		}
	}
	
	public void addScreenComponent(OverdriveScreenComponent component) {
		component.initScreenSize();
		components.add(component);
		addRenderableOnly(component);
	}
	
	public void addButton(AbstractOverdriveButton button) {
		button.initScreenSize();
		addRenderableWidget(button);
	}
	
	public void addEditBox(EditBoxOverdrive box) {
		addRenderableWidget(box);
	}

	public void setScreenParams() {
		leftPos -= OFFSET;
		imageWidth = GUI_WIDTH;
		imageHeight = GUI_HEIGHT;
		titleLabelX += OFFSET;
	}

	public int getGuiRight() {
		return getGuiLeft() + getXSize();
	}

	public int getGuiBottom() {
		return getGuiTop() + getYSize();
	}

	public abstract int getScreenNumber();

}
