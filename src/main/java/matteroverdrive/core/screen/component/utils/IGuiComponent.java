// Credit to AurilisDev https://github.com/aurilisdev/Electrodynamics
package matteroverdrive.core.screen.component.utils;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IGuiComponent {

	Rectangle getBounds(int guiWidth, int guiHeight);

	boolean matchesScreenNumber(int num);

	default void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
	}

	default void renderForeground(PoseStack stack, int xAxis, int yAxis) {
	}

	default void preMouseClicked(double xAxis, double yAxis, int button) {
	}

	default void mouseClicked(double xAxis, double yAxis, int button) {
	}

	default void mouseClickMove(int mouseX, int mouseY, int button, long ticks) {
	}

	default void mouseReleased(double xAxis, double yAxis, int type) {
	}

	default void mouseWheel(double mouseX, double mouseY, double delta) {
	}

}
