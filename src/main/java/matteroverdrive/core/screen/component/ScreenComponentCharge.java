package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ScreenComponentCharge extends ScreenComponent {

	private boolean isMatter = false;
	private boolean isGenerator = false;

	private final int matterHeight = 42;
	private final int matterWidth = 14;
	private final int energyHeight = 42;
	private final int energyWidth = 14;

	private final DoubleSupplier maxStorage;
	private final DoubleSupplier currStorage;
	private final DoubleSupplier usage;

	public ScreenComponentCharge(final DoubleSupplier currStorage, final DoubleSupplier maxStorage,
			final DoubleSupplier generation, final IScreenWrapper gui, final int x, final int y,
			final int[] screenNumbers) {
		super(new ResourceLocation(References.ID + ":textures/gui/progress/progress.png"), gui, x, y, screenNumbers);
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

	@Override
	public void renderForeground(PoseStack stack, int xAxis, int yAxis) {
		if (isPointInRegion(xLocation, yLocation, xAxis, yAxis, isMatter ? matterWidth : energyWidth,
				isMatter ? matterHeight : energyHeight)) {
			List<FormattedCharSequence> components = new ArrayList<>();
			String storeLoc = isMatter ? "matterstored" : "energystored";
			components.add(UtilsText.tooltip(storeLoc, currStorage.getAsDouble(), maxStorage.getAsDouble())
					.getVisualOrderText());

			double use = usage.getAsDouble();
			if (use > 0) {
				String usageLoc = isMatter ? "matterusage" : "energyusage";
				if (isGenerator) {
					components.add(UtilsText.tooltip(usageLoc, "+" + use).withStyle(ChatFormatting.GREEN)
							.getVisualOrderText());
				} else {
					components.add(
							UtilsText.tooltip(usageLoc, "+" + use).withStyle(ChatFormatting.RED).getVisualOrderText());
				}
			}
			gui.displayTooltips(stack, components, xAxis, yAxis);
		}
	}

	@Override
	public Rectangle getBounds(int guiWidth, int guiHeight) {
		return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, isMatter ? matterWidth : energyWidth,
				isMatter ? matterHeight : energyHeight);
	}

	@Override
	public void renderBackground(PoseStack stack, final int xAxis, final int yAxis, final int guiWidth,
			final int guiHeight) {
		UtilsRendering.bindTexture(resource);
		double progress = maxStorage.getAsDouble() > 0
				? Math.min(1.0, (double) currStorage.getAsDouble() / (double) maxStorage.getAsDouble())
				: 0;
		if (isMatter) {

			int height = (int) (progress * matterHeight);
			int offset = matterHeight - height;
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, energyWidth * 2, 0, energyWidth,
					energyHeight);
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation + offset, energyWidth * 3, offset,
					energyWidth, height);

		} else {

			int height = (int) (progress * energyHeight);
			int offset = energyHeight - height;
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, 0, energyWidth, energyHeight);
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation + offset, energyWidth, offset,
					energyWidth, height);

		}
	}

}
