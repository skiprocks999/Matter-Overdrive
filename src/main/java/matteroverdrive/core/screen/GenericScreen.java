package matteroverdrive.core.screen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.inventory.GenericInventory;
import matteroverdrive.core.screen.component.ScreenComponentSlot;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.IGuiComponent;
import matteroverdrive.core.screen.component.utils.ISlotType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class GenericScreen<T extends GenericInventory> extends AbstractContainerScreen<T> implements IScreenWrapper {

	protected ResourceLocation defaultBackground = new ResourceLocation(
			References.ID + ":textures/gui/base/base_gui.png");
	protected Set<IGuiComponent> components = new HashSet<>();
	protected int playerInvOffset = 0;

	public GenericScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		initializeComponents();
	}

	protected void initializeComponents() {
		for (Slot slot : menu.slots) {
			components.add(createScreenSlot(slot));
		}
	}

	protected ScreenComponentSlot createScreenSlot(Slot slot) {
		if (slot instanceof ISlotType type) {
			return new ScreenComponentSlot(type.getSlotType(), this, slot.x - 1, slot.y - 1);
		}
		return new ScreenComponentSlot(SlotType.SMALL, this, slot.x - 1, slot.y - 1);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack stack, int x, int y) {
		float length = font.width(this.title);
		float offset = (144.0F - length) / 2.0F;
		this.font.draw(stack, this.title, (float) this.titleLabelX + 3 + offset, (float) this.titleLabelY + 1,
				UtilsRendering.getRGBA(1, 191, 228, 230));
		int xAxis = x - (width - imageWidth) / 2;
		int yAxis = y - (height - imageHeight) / 2;
		for (IGuiComponent component : components) {
			component.renderForeground(stack, xAxis, yAxis);
		}
	}

	@Override
	protected void renderBg(PoseStack stack, float partialTick, int x, int y) {
		UtilsRendering.bindTexture(defaultBackground);
		int guiWidth = (width - imageWidth) / 2;
		int guiHeight = (height - imageHeight) / 2;
		blit(stack, guiWidth - 37, guiHeight, 0, 0, 224, 176);
		int xAxis = x - guiWidth;
		int yAxis = y - guiHeight;
		for (IGuiComponent component : components) {
			component.renderBackground(stack, xAxis, yAxis, guiWidth, guiHeight);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		double xAxis = x - (width - imageWidth) / 2.0;
		double yAxis = y - (height - imageHeight) / 2.0;

		for (IGuiComponent component : components) {
			component.preMouseClicked(xAxis, yAxis, button);
		}

		boolean ret = super.mouseClicked(x, y, button);

		for (IGuiComponent component : components) {
			component.mouseClicked(xAxis, yAxis, button);
		}
		return ret;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		double xAxis = mouseX - (width - imageWidth) / 2.0;
		double yAxis = mouseY - (height - imageHeight) / 2.0;

		for (IGuiComponent component : components) {
			component.mouseClicked(xAxis, yAxis, button);
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean ret = super.mouseReleased(mouseX, mouseY, button);

		double xAxis = mouseX - (width - imageWidth) / 2.0;
		double yAxis = mouseY - (height - imageHeight) / 2.0;

		for (IGuiComponent component : components) {
			component.mouseReleased(xAxis, yAxis, button);
		}
		return ret;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		for (IGuiComponent component : components) {
			component.mouseWheel(mouseX, mouseY, delta);
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	public int getXPos() {
		return (width - imageWidth) / 2;
	}

	public int getYPos() {
		return (height - imageHeight) / 2;
	}

	@Override
	public void drawTexturedRect(PoseStack stack, int x, int y, int u, int v, int w, int h) {
		blit(stack, x, y, u, v, w, h);
	}

	@Override
	public void drawTexturedRect(PoseStack stack, int x, int y, int u, int v, int w, int h, int tW, int tH) {
		blit(stack, x, y, u, v, w, h, tH, tW);
	}

	@Override
	public void drawTexturedRectFromIcon(PoseStack stack, int x, int y, TextureAtlasSprite icon, int w, int h, int tW,
			int tH) {
		blit(stack, x, y, (int) (icon.getU0() * icon.getWidth()), (int) (icon.getV0() * icon.getHeight()), w, h, tH,
				tW);
	}

	@Override
	public void displayTooltip(PoseStack stack, Component text, int xAxis, int yAxis) {
		this.renderTooltip(stack, text, xAxis, yAxis);
	}

	@Override
	public void displayTooltips(PoseStack stack, List<? extends FormattedCharSequence> tooltips, int xAxis, int yAxis) {
		super.renderTooltip(stack, tooltips, xAxis, yAxis, font);
	}

	@Override
	public Font getFontRenderer() {
		return font;
	}

}
