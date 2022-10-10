package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.client.render.item.VariableAlphaItemRenderer;
import matteroverdrive.client.screen.ScreenDiscManipulator;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.tile.matter_network.TileDiscManipulator;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton.ButtonTextures;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;

public class ScreenComponentPatternInfo extends OverdriveScreenComponent {

	private final VariableAlphaItemRenderer renderer;
	private final int index;

	private ItemPatternWrapper wrapper = null;

	private static final int PIXEL_BLOCK_WIDTH = 16;
	private static final int DISTANCE_FROM_BUTTOM = 97;

	public ScreenComponentPatternInfo(ScreenDiscManipulator gui, int x, int y, int[] screenNumbers,
			ItemRenderer renderer, int index) {
		super(OverdriveTextures.NONE, gui, x, y, -1, -1, screenNumbers);
		this.renderer = new VariableAlphaItemRenderer(renderer);
		this.index = index;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.setShader(GameRenderer::getPositionShader);
		UtilsRendering.resetShaderColor();
		TileDiscManipulator tile = ((ScreenDiscManipulator) gui).getMenu().getTile();
		if (tile == null) {
			return;
		}
		ItemStack patternDrive = tile.getInventoryCap().getStackInSlot(0);
		if (patternDrive.isEmpty()) {
			return;
		}
		ItemPatternDrive drive = (ItemPatternDrive) patternDrive.getItem();
		LazyOptional<ICapabilityItemPatternStorage> lazy = patternDrive
				.getCapability(MatterOverdriveCapabilities.STORED_PATTERNS);
		if (!lazy.isPresent()) {
			return;
		}
		ICapabilityItemPatternStorage storage = lazy.resolve().get();
		boolean fused = drive.isFused(patternDrive);
		if (fused) {
			if (patternDrive.getOrCreateTag().getInt(UtilsNbt.INDEX) == index) {
				wrapper = storage.getStoredPatterns()[index];
				renderItemInfo(stack, wrapper, fused);
			} else {
				renderFusedInfo(stack);
			}
		} else {
			wrapper = storage.getStoredPatterns()[index];
			renderItemInfo(stack, wrapper, fused);
		}

	}

	private void renderItemInfo(PoseStack stack, ItemPatternWrapper wrapper, boolean fused) {
		ItemStack pattern = new ItemStack(wrapper.getItem());
		boolean empty = pattern.isEmpty();
		int percentage = wrapper.getPercentage();

		if (empty) {
			IconType type = IconType.BLOCK_DARK;
			UtilsRendering.bindTexture(type.getTexture());
			blit(stack, this.x, this.y, type.getTextureU(), type.getTextureV(), type.getUWidth(), type.getVHeight(),
					type.getTextureWidth(), type.getTextureHeight());
		} else {
			renderer.setAlpha((float) percentage / 100.0F);
			renderer.renderGuiItem(new ItemStack(wrapper.getItem()), this.x, this.y);
		}
		
		if(empty || fused) {
			UtilsRendering.bindTexture(ButtonTextures.OVERDRIVE_PRESS_REG.getTexture());
			ButtonOverdrive.drawButton(stack, this.x + DISTANCE_FROM_BUTTOM, this.y - 1, 50, 18);
		}

		int color;

		if(empty) {
			color = Colors.GREEN.getColor();
		} else if (percentage >= 100) {
			color = Colors.HOLO_GREEN.getColor();
		} else if (percentage >= 50) {
			color = Colors.HOLO_YELLOW.getColor();
		} else {
			color = Colors.HOLO_RED.getColor();
		}

		Component percentText;

		if (empty) {
			percentText = UtilsText.tooltip("empty");
		} else {
			percentText = Component.literal(percentage + "%");
		}

		renderText(stack, percentText, color);
	}
	
	private void renderFusedInfo(PoseStack stack) {
		renderer.setAlpha(1.0F);
		renderer.renderGuiItem(new ItemStack(Items.BARRIER), this.x, this.y);
		
		UtilsRendering.bindTexture(ButtonTextures.OVERDRIVE_PRESS_REG.getTexture());
		ButtonOverdrive.drawButton(stack, this.x + DISTANCE_FROM_BUTTOM, this.y - 1, 50, 18);
		
		renderText(stack, UtilsText.tooltip("fusedpattern"), Colors.HOLO_RED.getColor());
	}
	
	private void renderText(PoseStack stack, Component text, int color) {
		
		Font font = gui.getFontRenderer();
		
		int width = font.width(text);

		int offset = (DISTANCE_FROM_BUTTOM - PIXEL_BLOCK_WIDTH - width) / 2;

		font.draw(stack, text, this.x + PIXEL_BLOCK_WIDTH + offset, this.y + 4, color);
	}

	@Override
	protected boolean isPointInRegion(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + PIXEL_BLOCK_WIDTH
				&& mouseY < this.y + PIXEL_BLOCK_WIDTH;
	}

	@Override
	public void renderTooltip(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (wrapper != null && !new ItemStack(wrapper.getItem()).isEmpty()) {
			gui.renderTooltip(stack, wrapper.getItem().getDescription(), mouseX, mouseY);
		}

	}

}
