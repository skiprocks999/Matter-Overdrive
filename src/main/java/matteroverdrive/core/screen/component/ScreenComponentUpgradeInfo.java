package matteroverdrive.core.screen.component;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentUpgradeInfo extends OverdriveScreenComponent {

	private final Supplier<IUpgradableTile> tile;
	private boolean matterPerTick = false;
	private boolean powerNonTick = false;
	private boolean customTime = false;
	private String customTimeKey = null;

	public ScreenComponentUpgradeInfo(GenericScreen<?> gui, int x, int y, int[] screenNumbers,
			Supplier<IUpgradableTile> tile) {
		super(new ResourceLocation(""), gui, x, y, 0, 0, screenNumbers);
		this.tile = tile;
	}
	
	public ScreenComponentUpgradeInfo setMatterPerTick() {
		matterPerTick = true;
		return this;
	}
	
	public ScreenComponentUpgradeInfo setPowerNonTick() {
		powerNonTick = true;
		return this;
	}
	
	public ScreenComponentUpgradeInfo setCustomTimeKey(String key) {
		customTime = true;
		customTimeKey = key;
		return this;
	}
	
	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		IUpgradableTile owner = tile.get();
		TranslatableComponent component;
		int color;
		if (owner != null) {
			Font font = gui.getFontRenderer();
			int offset = 0;

			double currSpeed = owner.getCurrentSpeed(true);
			double operatingTime = owner.getProcessingTime();
			if (currSpeed > 0 && owner.getDefaultSpeed() > 0) {
				if(customTime) {
					component = UtilsText.gui(customTimeKey, currSpeed);
				} else {
					component = UtilsText.gui("time", UtilsText.formatTimeValue(operatingTime / currSpeed / 20.0));
				}
				color = currSpeed >= owner.getDefaultSpeed() ? UtilsRendering.GREEN : UtilsRendering.RED;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currPowerUsage = owner.getCurrentPowerUsage(true);
			if (currPowerUsage > 0 && owner.getDefaultPowerUsage() > 0) {
				String formatted = UtilsText.formatPowerValue(currPowerUsage);
				component = powerNonTick ? UtilsText.gui("usage", formatted) : UtilsText.gui("usagetick", formatted);
				color = currPowerUsage > owner.getDefaultPowerUsage() ? UtilsRendering.RED : UtilsRendering.GREEN;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currMatterUsage = owner.getCurrentMatterUsage(true);
			if (currMatterUsage > 0 && owner.getDefaultMatterUsage() > 0) {
				String formatted = UtilsText.formatMatterValue(currMatterUsage);
				component = matterPerTick ? UtilsText.gui("usagetick", formatted) : UtilsText.gui("usage", formatted);
				color = currMatterUsage > owner.getDefaultMatterUsage() ? UtilsRendering.RED : UtilsRendering.GREEN;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			float failureChance = owner.getCurrentFailure(true);
			if (owner.getDefaultFailure() > 0) {
				component = UtilsText.gui("failure", UtilsText.formatPercentage(failureChance * 100));
				color = failureChance > owner.getDefaultFailure() ? UtilsRendering.RED : UtilsRendering.GREEN;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			int range = (int) owner.getCurrentRange(true);
			if (owner.getDefaultRange() > 0) {
				component = UtilsText.gui("range", range);
				color = range >= owner.getDefaultRange() ? UtilsRendering.GREEN : UtilsRendering.RED;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currPowerStorage = owner.getCurrentPowerStorage(true);
			if (currPowerStorage > 0 && owner.getDefaultPowerStorage() > 0) {
				component = UtilsText.gui("storage", UtilsText.formatPowerValue(currPowerStorage));
				color = currPowerStorage >= owner.getDefaultPowerStorage() ? UtilsRendering.GREEN : UtilsRendering.RED;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currMatterStorage = owner.getCurrentMatterStorage(true);
			if (currMatterStorage > 0 && owner.getDefaultMatterStorage() > 0) {
				component = UtilsText.gui("storage", UtilsText.formatMatterValue(currMatterStorage));
				color = currMatterStorage >= owner.getDefaultMatterStorage() ? UtilsRendering.GREEN
						: UtilsRendering.RED;
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}
			if (owner.isMuffled(true)) {
				font.draw(stack, UtilsText.gui("soundmuted"), this.x, this.y + offset, UtilsRendering.GREEN);
			}

		}
	}

}
