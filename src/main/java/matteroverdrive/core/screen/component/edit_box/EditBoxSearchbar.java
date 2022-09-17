package matteroverdrive.core.screen.component.edit_box;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;

public class EditBoxSearchbar extends EditBoxOverdrive {

	private static final double RS_WIDTH_COEFF = 0.13855421686746987951807228915663; // 23/166
	private static final double LS_WIDTH_COEFF = 0.07228915662650602409638554216867; // 12/166

	private final int totalWidth;

	public EditBoxSearchbar(GenericScreen<?> gui, int x, int y, int width, int height, int totalWidth) {
		super(EditBoxTextures.SEARCH_BAR, gui, x, y, width, height);
		this.totalWidth = totalWidth;
	}

	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		if (this.isVisible()) {
			Font font = gui.getFontRenderer();

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			UtilsRendering.bindTexture(texture.getTexture());
			int leftSide = (int) ((double) this.totalWidth * LS_WIDTH_COEFF);
			int rightSide = (int) ((double) this.totalWidth * RS_WIDTH_COEFF);

			blit(stack, this.x - leftSide, this.y, width + rightSide, 0, 0, 166, 14, 166, 14);

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
