package matteroverdrive.core.screen.component.edit_box;

import java.util.function.Predicate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EditBoxOverdrive extends EditBox {

	protected GenericScreen<?> gui;

	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID,
			"textures/gui/button/edit_box.png");

	private static final char[] VALID_NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' };
	private static final char[] VALID_POSITIVE_NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static final Predicate<String> INTEGER_BOX = (string) -> {
		if (string == null) {
			return false;
		}
		if (string.length() == 0) {
			string = "0";
			return true;
		}
		boolean validChar = false;
		for (char character : string.toCharArray()) {
			validChar = false;
			for (char valid : VALID_NUMBERS) {
				if (valid == character) {
					validChar = true;
					break;
				}
			}
			if (!validChar) {
				return false;
			}
		}
		int firstOccurance = string.indexOf('-');
		if (firstOccurance < 0) {
			return true;
		} else if (firstOccurance > 0) {
			return false;
		} else {
			int nextIndex = string.indexOf('-', 1);
			if (nextIndex < 0) {
				return true;
			}
		}

		return false;
	};

	public static final Predicate<String> POSITIVE_INTEGER_BOX = (string) -> {
		if (string == null) {
			return false;
		}
		if (string.length() == 0) {
			return true;
		}
		boolean validChar = false;
		for (char character : string.toCharArray()) {
			validChar = false;
			for (char valid : VALID_POSITIVE_NUMBERS) {
				if (valid == character) {
					validChar = true;
					break;
				}
			}
			if (!validChar) {
				return false;
			}
		}

		return true;
	};

	public EditBoxOverdrive(GenericScreen<?> gui, int x, int y, int width, int height) {
		super(gui.getFontRenderer(), x, y, width, height, Component.empty());
		this.gui = gui;

	}

	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		if (this.isVisible()) {
			Font font = gui.getFontRenderer();

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			UtilsRendering.bindTexture(TEXTURE);
			ButtonOverdrive.drawButton(stack, this.x, this.y, this.width, this.height);

			int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
			int j = this.cursorPos - this.displayPos;
			int k = this.highlightPos - this.displayPos;
			String s = font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
			int l = this.x + 4;
			int i1 = this.y + (this.height - 8) / 2;
			int j1 = l;
			if (k > s.length()) {
				k = s.length();
			}

			if (!s.isEmpty()) {
				String s1 = flag ? s.substring(0, j) : s;
				j1 = font.drawShadow(stack, this.formatter.apply(s1, this.displayPos), (float) l, (float) i1, i2);
			}

			boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
			int k1 = j1;
			if (!flag) {
				k1 = j > 0 ? l + this.width : l;
			} else if (flag2) {
				k1 = j1 - 1;
				--j1;
			}

			if (!s.isEmpty() && flag && j < s.length()) {
				font.drawShadow(stack, this.formatter.apply(s.substring(j), this.cursorPos), (float) j1, (float) i1,
						i2);
			}

			if (!flag2 && this.suggestion != null) {
				font.drawShadow(stack, this.suggestion, (float) (k1 - 1), (float) i1, -8355712);
			}

			if (flag1) {
				if (flag2) {
					GuiComponent.fill(stack, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
				} else {
					font.drawShadow(stack, "_", (float) k1, (float) i1, i2);
				}
			}

			if (k != j) {
				int l1 = l + font.width(s.substring(0, k));
				this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
			}

		}
	}

}
