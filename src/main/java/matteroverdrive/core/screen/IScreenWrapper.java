// Credit to AurilisDev https://github.com/aurilisdev/Electrodynamics
package matteroverdrive.core.screen;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public interface IScreenWrapper {

	void drawTexturedRect(PoseStack stack, int x, int y, int u, int v, int w, int h, int tW, int tH);

	void drawTexturedRect(PoseStack stack, int x, int y, int u, int v, int w, int h);

	void drawTexturedRectFromIcon(PoseStack stack, int x, int y, TextureAtlasSprite icon, int w, int h, int tW, int tH);

	void displayTooltip(PoseStack stack, Component text, int xAxis, int yAxis);

	void displayTooltips(PoseStack stack, List<? extends FormattedCharSequence> tooltips, int xAxis, int yAxis);

	Font getFontRenderer();

	int[] getAxisAndGuiWidth(int mouseX, int mouseY);

}
