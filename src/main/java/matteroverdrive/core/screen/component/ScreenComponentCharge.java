package matteroverdrive.core.screen.component;

import java.awt.Rectangle;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ScreenComponentCharge extends ScreenComponent {

	private boolean isMatter = false;
	private boolean isGenerator = false;

	private final int matterHeight = 42;
	private final int matterWidth = 14;
	private final int energyHeight = 42;
	private final int energyWidth = 14;

	private final Supplier<Integer> maxStorage;
	private final Supplier<Integer> currStorage;
	private final Supplier<Integer> usage;

	public ScreenComponentCharge(final Supplier<Integer> currStorage, final Supplier<Integer> maxStorage, final Supplier<Integer> generation, 
			final IScreenWrapper gui, final int x, final int y, final int[] screenNumbers) {
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
		if(isPointInRegion(xLocation, yLocation, xAxis, yAxis, isMatter ? matterWidth : energyWidth, isMatter ? matterHeight : energyHeight)) {
			String storeLoc = isMatter ? "tooltip.matteroverdrive.matterstored" : "tooltip.matteroverdrive.energystored";
			gui.displayTooltip(stack, new TranslatableComponent(storeLoc, currStorage.get(), maxStorage.get()), xAxis, yAxis);
			String usageLoc = isMatter ? "tooltip.matteroverdrive.matterusage" : "tooltip.matteroverdrive.energyusage";
			int use = usage.get();
			if(isGenerator) {
				if(use > 0) {
					gui.displayTooltip(stack, new TranslatableComponent(usageLoc, "+" + use).withStyle(ChatFormatting.GREEN), xAxis, yAxis + 10);
				}
			} else {
				if(use < 0) {
					gui.displayTooltip(stack, new TranslatableComponent(usageLoc, "-" + use).withStyle(ChatFormatting.RED), xAxis, yAxis + 10);
				}
			}
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
		double progress = maxStorage.get() > 0 ?  Math.min(1.0, (double) currStorage.get() / (double) maxStorage.get()) : 0;
		if (isMatter) {

			int height = (int) (progress * matterHeight);
			int offset = matterHeight - height;
			gui.drawTexturedRect(stack, guiWidth + xLocation, guiHeight + yLocation, 0, energyWidth * 2, energyWidth,
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
