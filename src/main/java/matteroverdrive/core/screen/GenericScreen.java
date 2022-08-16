package matteroverdrive.core.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

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

	private List<OverdriveScreenComponent> components = new ArrayList<>();
	protected int playerInvOffset = 0;
	protected final ResourceLocation background;

	public GenericScreen(T menu, Inventory playerinventory, Component title, ResourceLocation background) {
		super(menu, playerinventory, title);
		this.background = background;
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
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(background);
		blit(stack, getXPos(), getYPos(), 0, 0, imageWidth, imageHeight);
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
		int xPos = getXPos();
		int yPos = getYPos();
		return new int[] { xPos, yPos, mouseX - xPos, mouseY - yPos };
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

	public int getGuiRight() {
		return getGuiLeft() + getXSize();
	}

	public int getGuiBottom() {
		return getGuiTop() + getYSize();
	}

	public abstract int getScreenNumber();
	
	public abstract void setScreenParams();

}
