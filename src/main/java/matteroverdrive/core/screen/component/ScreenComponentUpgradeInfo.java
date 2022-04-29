package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.tile.IUpgradableTile;
import matteroverdrive.core.utils.UtilsFormatting;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentUpgradeInfo extends ScreenComponent {

	private static final int RED = UtilsRendering.getRGBA(1, 255, 0, 0);
	private static final int GREEN = UtilsRendering.getRGBA(1, 0, 255, 0);

	private final Supplier<IUpgradableTile> tile;

	public ScreenComponentUpgradeInfo(IScreenWrapper gui, int x, int y, int[] screenNumbers,
			Supplier<IUpgradableTile> tile) {
		super(new ResourceLocation(""), gui, x, y, screenNumbers);
		this.tile = tile;
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(0, 0, 0, 0);
	}

	@Override
	public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		IUpgradableTile owner = tile.get();
		TranslatableComponent component;
		int color;
		if (owner != null) {
			Font font = gui.getFontRenderer();
			int offset = 0;

			double currSpeed = owner.getCurrentSpeed(true);
			double operatingTime = owner.getProcessingTime();
			if (currSpeed > 0 && owner.getDefaultSpeed() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.time",
						UtilsFormatting.formatTimeValue(operatingTime / currSpeed / 20.0));
				color = currSpeed >= owner.getDefaultSpeed() ? GREEN : RED;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}

			double currPowerUsage = owner.getCurrentPowerUsage(true);
			if (currPowerUsage > 0 && owner.getDefaultPowerUsage() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.usage",
						UtilsFormatting.formatPowerValue(currPowerUsage));
				color = currPowerUsage > owner.getDefaultPowerUsage() ? RED : GREEN;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}

			double currMatterUsage = owner.getCurrentMatterUsage(true);
			if (currMatterUsage > 0 && owner.getDefaultMatterUsage() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.usage",
						UtilsFormatting.formatMatterValue(currMatterUsage));
				color = currMatterUsage > owner.getDefaultMatterUsage() ? RED : GREEN;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}

			float failureChance = owner.getCurrentFailure(true);
			if (owner.getDefaultFailure() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.failure",
						UtilsFormatting.formatPercentage(failureChance * 100));
				color = failureChance > owner.getDefaultFailure() ? RED : GREEN;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}

			int range = (int) owner.getCurrentRange(true);
			if (owner.getDefaultRange() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.range", range);
				color = range >= owner.getDefaultRange() ? GREEN : RED;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}

			double currPowerStorage = owner.getCurrentPowerStorage(true);
			if (currPowerStorage > 0 && owner.getDefaultPowerStorage() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.storage",
						UtilsFormatting.formatPowerValue(currPowerStorage));
				color = currPowerStorage >= owner.getDefaultPowerStorage() ? GREEN : RED;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}

			double currMatterStorage = owner.getCurrentMatterStorage(true);
			if (currMatterStorage > 0 && owner.getDefaultMatterStorage() > 0) {
				component = new TranslatableComponent("gui.matteroverdrive.storage",
						UtilsFormatting.formatMatterValue(currMatterStorage));
				color = currMatterStorage >= owner.getDefaultMatterStorage() ? GREEN : RED;
				font.draw(stack, component, guiWidth + this.xLocation, guiHeight + this.yLocation + offset, color);
				offset += 10;
			}
			if (owner.isMuffled(true)) {
				font.draw(stack, new TranslatableComponent("gui.matteroverdrive.soundmuted"), guiWidth + this.xLocation,
						guiHeight + this.yLocation + offset, GREEN);
			}

		}
	}

}
