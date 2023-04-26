package matteroverdrive.core.screen.component.button;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.wrappers.WrapperPatternMonitorScreen;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ButtonItemPattern extends ButtonHoldPress {

	private static final SlotType REG = SlotType.BIG;
	private static final SlotType HOV = SlotType.BIG_DARK;

	private static final ResourceLocation REGULAR = REG.getTexture();
	private static final ResourceLocation HOVERED = HOV.getTexture();

	private boolean shouldHover = true;

	private int col;
	private int row;

	private ItemPatternWrapper wrapper;
	private final ItemRenderer renderer;

	private final WrapperPatternMonitorScreen parent;

	public ButtonItemPattern(GenericScreen<?> gui, int x, int y, OnPress onPress, ItemRenderer renderer, int row,
			int col, WrapperPatternMonitorScreen parent) {
		super(gui, x, y, 22, 22, NO_TEXT, onPress, (button, stack, mouseX, mouseY) -> {
			ButtonItemPattern pattern = (ButtonItemPattern) button;
			if (pattern.wrapper != null) {
				Item item = pattern.wrapper.getItem();
				int percentage = pattern.wrapper.getPercentage();
				MutableComponent name = Component.translatable(item.getDescriptionId());
				ChatFormatting color = ChatFormatting.RED;
				if (percentage >= 100) {
					color = ChatFormatting.GREEN;
				} else if (percentage < 100 && percentage > 50) {
					color = ChatFormatting.YELLOW;
				} else {
					color = ChatFormatting.RED;
				}
				List<FormattedCharSequence> tooltips = new ArrayList<>();
				tooltips.add(UtilsText.tooltip("storedpattern", name.withStyle(color),
						UtilsText.formatPercentage(percentage)).getVisualOrderText());
				double val = MatterRegister.INSTANCE.getClientMatterValue(new ItemStack(item));
				tooltips.add(
						val <= 0.0
								? UtilsText
										.tooltip("matterval",
												UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
										.withStyle(ChatFormatting.BLUE).getVisualOrderText()
								: UtilsText
										.tooltip("matterval",
												Component.literal(UtilsText.formatMatterValue(val))
														.withStyle(ChatFormatting.GOLD))
										.withStyle(ChatFormatting.GRAY).getVisualOrderText());
				pattern.gui.renderTooltip(stack, tooltips, mouseX, mouseY);
			}
		});
		this.renderer = renderer;
		this.col = col;
		this.row = row;
		this.parent = parent;
	}

	public ButtonItemPattern setNoHover() {
		this.shouldHover = false;
		return this;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		SlotType type;
		ItemPatternWrapper orderedPattern = parent.selectedItem.getPattern();
		boolean shouldActivate = false;
		if (orderedPattern != null && wrapper != null) {
			shouldActivate = orderedPattern.isItem(wrapper.getItem());
		}
		if (shouldActivate || isHovered && shouldHover) {
			UtilsRendering.bindTexture(HOVERED);
			type = HOV;
		} else {
			UtilsRendering.bindTexture(REGULAR);
			type = REG;
		}
		blit(stack, this.x, this.y, type.getTextureU(), type.getTextureV(), type.getUWidth(), type.getVHeight(),
				type.getTextureWidth(), type.getTextureHeight());
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (wrapper != null) {
			renderer.renderGuiItem(new ItemStack(wrapper.getItem()), this.x + 3, this.y + 3);
		}
	}

	public void setPattern(ItemPatternWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public boolean isFilled() {
		return wrapper != null && wrapper.isNotAir();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isSame(ButtonItemPattern press) {
		return press.col == col && press.row == row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public ItemPatternWrapper getPattern() {
		return wrapper;
	}

}
