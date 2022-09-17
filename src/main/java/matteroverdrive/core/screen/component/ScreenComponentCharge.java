package matteroverdrive.core.screen.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;

public class ScreenComponentCharge extends OverdriveScreenComponent {

	private boolean isMatter = false;
	private boolean isGenerator = false;

	private boolean powerNonTick = false;
	private boolean matterPerTick = false;

	private static final int HEIGHT = 42;
	private static final int WIDTH = 14;

	private final DoubleSupplier maxStorage;
	private final DoubleSupplier currStorage;
	private final DoubleSupplier usage;

	public ScreenComponentCharge(final DoubleSupplier currStorage, final DoubleSupplier maxStorage,
			final DoubleSupplier generation, final GenericScreen<?> gui, final int x, final int y,
			final int[] screenNumbers) {
		super(OverdriveTextures.PROGRESS_BARS, gui, x, y, WIDTH, HEIGHT, screenNumbers);
		this.maxStorage = maxStorage;
		this.currStorage = currStorage;
		this.usage = generation;
	}

	public ScreenComponentCharge setMatter() {
		isMatter = true;
		return this;
	}

	public ScreenComponentCharge setGenerator() {
		isGenerator = true;
		return this;
	}

	public ScreenComponentCharge setPowerNonTick() {
		powerNonTick = true;
		return this;
	}

	public ScreenComponentCharge setMatterPerTick() {
		matterPerTick = true;
		return this;
	}

	@Override
	public void renderTooltip(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		List<FormattedCharSequence> components = new ArrayList<>();
		String storeLoc = isMatter ? "matterstored" : "energystored";
		String formattedNum = isMatter ? UtilsText.MATTER_FORMAT.format(currStorage.getAsDouble()) : UtilsText.POWER_FORMAT.format(currStorage.getAsDouble());
		String formattedDenom = isMatter ? UtilsText.MATTER_FORMAT.format(maxStorage.getAsDouble()) : UtilsText.POWER_FORMAT.format(maxStorage.getAsDouble());
		components.add(UtilsText.tooltip(storeLoc, formattedNum, formattedDenom).getVisualOrderText());

		double use = usage.getAsDouble();
		if (use > 0) {
			if (isMatter) {
				String usageLoc = matterPerTick ? "usagetick" : "usage";
				String formatted = UtilsText.formatMatterValue(use);
				if (isGenerator) {
					components.add(UtilsText.tooltip(usageLoc, "+" + formatted).withStyle(ChatFormatting.GREEN)
							.getVisualOrderText());
				} else {
					components.add(UtilsText.tooltip(usageLoc, "-" + formatted).withStyle(ChatFormatting.RED)
							.getVisualOrderText());
				}
			} else {
				String usageLoc = powerNonTick ? "usage" : "usagetick";
				String formatted = UtilsText.formatPowerValue(use);
				if (isGenerator) {
					components.add(UtilsText.tooltip(usageLoc, "+" + formatted).withStyle(ChatFormatting.GREEN)
							.getVisualOrderText());
				} else {
					components.add(UtilsText.tooltip(usageLoc, "-" + formatted).withStyle(ChatFormatting.RED)
							.getVisualOrderText());
				}
			}
		}
		gui.renderTooltip(stack, components, mouseX, mouseY);

	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(resource.getTexture());
		double progress = maxStorage.getAsDouble() > 0
				? Math.min(1.0, (double) currStorage.getAsDouble() / (double) maxStorage.getAsDouble())
				: 0;

		int height = (int) (progress * this.height);
		int offset = this.height - height;

		if (isMatter) {

			blit(stack, this.x, this.y, this.width * 2, 0, this.width, this.height);
			blit(stack, this.x, this.y + offset, this.width * 3, offset, this.width, height);

		} else {

			blit(stack, this.x, this.y, 0, 0, this.width, this.height);
			blit(stack, this.x, this.y + offset, this.width, offset, this.width, height);

		}
	}

}
