package matteroverdrive.core.screen.component;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentUpgradeInfo extends ScreenComponent {

	private UpgradeType[] upgrades;
	
	public ScreenComponentUpgradeInfo(IScreenWrapper gui, int x, int y, int[] screenNumbers, @Nonnull UpgradeType[] upgrades) {
		super(new ResourceLocation(""), gui, x, y, screenNumbers);
		this.upgrades = upgrades;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(0, 0, 0, 0);
	}
	
	@Override
	public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		Font font = gui.getFontRenderer();
		font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation, fontColor);
		
	}

}
