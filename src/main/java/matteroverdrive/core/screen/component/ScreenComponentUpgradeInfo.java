package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.tile.utils.IUpgradableTile;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;

public class ScreenComponentUpgradeInfo extends OverdriveScreenComponent {

	private boolean matterPerTick = false;
	private boolean powerNonTick = false;
	private boolean customTime = false;
	private String customTimeKey = null;
	private boolean zeroSpeed = false;

	public ScreenComponentUpgradeInfo(GenericScreen<?> gui, int x, int y, int[] screenNumbers) {
		super(OverdriveTextures.NONE, gui, x, y, 0, 0, screenNumbers);
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
		IUpgradableTile owner = (IUpgradableTile) ((GenericInventoryTile<?>) gui.getMenu()).getTile();
		MutableComponent component;
		int color;
		if (owner != null) {
			Font font = gui.getFontRenderer();
			int offset = 0;

			double currSpeed = owner.getCurrentSpeed();
			double operatingTime = owner.getProcessingTime();
			if (currSpeed > 0 && owner.getDefaultSpeed() > 0) {
				if (customTime) {
					component = UtilsText.gui(customTimeKey, currSpeed);
				} else {
					component = UtilsText.gui("time", UtilsText.formatTimeValue(operatingTime / currSpeed / 20.0));
				}
				color = currSpeed >= owner.getDefaultSpeed() ? Colors.GREEN.getColor() : Colors.RED.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currPowerUsage = owner.getCurrentPowerUsage();
			if (currPowerUsage > 0 && owner.getDefaultPowerUsage() > 0) {
				String formatted = UtilsText.formatPowerValue(currPowerUsage);
				component = powerNonTick ? UtilsText.gui("usage", formatted) : UtilsText.gui("usagetick", formatted);
				color = currPowerUsage > owner.getDefaultPowerUsage() ? Colors.RED.getColor() : Colors.GREEN.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currMatterUsage = owner.getCurrentMatterUsage();
			if (currMatterUsage > 0 && owner.getDefaultMatterUsage() > 0) {
				String formatted = UtilsText.formatMatterValue(currMatterUsage);
				component = matterPerTick ? UtilsText.gui("usagetick", formatted) : UtilsText.gui("usage", formatted);
				color = currMatterUsage > owner.getDefaultMatterUsage() ? Colors.RED.getColor() : Colors.GREEN.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			float failureChance = owner.getCurrentFailure();
			if (owner.getDefaultFailure() > 0) {
				component = UtilsText.gui("failure", UtilsText.formatPercentage(failureChance * 100));
				color = failureChance > owner.getDefaultFailure() ? Colors.RED.getColor() : Colors.GREEN.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			int range = (int) owner.getCurrentRange();
			if (owner.getDefaultRange() > 0) {
				component = UtilsText.gui("range", range);
				color = range >= owner.getDefaultRange() ? Colors.GREEN.getColor() : Colors.RED.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currPowerStorage = owner.getCurrentPowerStorage();
			if (currPowerStorage > 0 && owner.getDefaultPowerStorage() > 0) {
				component = UtilsText.gui("storage", UtilsText.formatPowerValue(currPowerStorage));
				color = currPowerStorage >= owner.getDefaultPowerStorage() ? Colors.GREEN.getColor() : Colors.RED.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}

			double currMatterStorage = owner.getCurrentMatterStorage();
			if (currMatterStorage > 0 && owner.getDefaultMatterStorage() > 0) {
				component = UtilsText.gui("storage", UtilsText.formatMatterValue(currMatterStorage));
				color = currMatterStorage >= owner.getDefaultMatterStorage() ? Colors.GREEN.getColor() : Colors.RED.getColor();
				font.draw(stack, component, this.x, this.y + offset, color);
				offset += 10;
			}
			if (owner.isMuffled()) {
				font.draw(stack, UtilsText.gui("soundmuted"), this.x, this.y + offset, Colors.GREEN.getColor());
			}

		}
	}

}
