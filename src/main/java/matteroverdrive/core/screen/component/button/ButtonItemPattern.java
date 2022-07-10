package matteroverdrive.core.screen.component.button;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.capability.types.item_pattern.ItemPatternWrapper;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ButtonItemPattern extends ButtonHoldPress {
	
	private static final SlotType REG = SlotType.BIG;
	private static final SlotType HOV = SlotType.BIG_DARK;
	
	private static final ResourceLocation REGULAR = new ResourceLocation(REG.getTextureLoc());
	private static final ResourceLocation HOVERED = new ResourceLocation(HOV.getTextureLoc());
	
	private final int col;
	private final int row;
	
	private ItemPatternWrapper wrapper;
	private final ItemRenderer renderer;
	
	private final String currentSearch = "";
	
	public ButtonItemPattern(GenericScreen<?> gui, int x, int y, OnPress onPress, ItemRenderer renderer, 
			int row, int col) {
		super(gui, x, y, 22, 22, TextComponent.EMPTY, onPress, (button, stack, mouseX, mouseY) -> {
			ButtonItemPattern pattern = (ButtonItemPattern) button;
			if(pattern.wrapper != null) {
				Item item = pattern.wrapper.getItem();
				int percentage = pattern.wrapper.getPercentage();
				TranslatableComponent name = new TranslatableComponent(item.getDescriptionId());
				ChatFormatting color = ChatFormatting.RED;
				if(percentage >= 100) {
					color = ChatFormatting.GREEN;
				} else if (percentage < 100 && percentage > 50) {
					color = ChatFormatting.YELLOW;
				} else {
					color = ChatFormatting.RED;
				}
				List<FormattedCharSequence> tooltips = new ArrayList<>();
				tooltips.add(UtilsText.tooltip("storedpattern", name.withStyle(color), UtilsText.SINGLE_DECIMAL.format(percentage) + "%").getVisualOrderText());
				Double val = MatterRegister.INSTANCE.getClientMatterValue(new ItemStack(item));
				tooltips.add(val == null ? UtilsText.tooltip("matterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
						.withStyle(ChatFormatting.BLUE).getVisualOrderText()
				: UtilsText
						.tooltip("matterval",
								new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.GOLD)).
						withStyle(ChatFormatting.GRAY).getVisualOrderText());
				pattern.gui.renderTooltip(stack, tooltips, mouseX, mouseY);
			}
		});
		this.renderer = renderer;
		this.col = col;
		this.row = row;
	}
	
	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		SlotType type;
		if(isActivated || isHovered) {
			UtilsRendering.bindTexture(HOVERED);
			type = HOV;
		} else {
			UtilsRendering.bindTexture(REGULAR);
			type = REG;
		}
		blit(stack, this.x, this.y,
				type.getTextureX(), type.getTextureY(), type.getWidth(), type.getHeight(), type.getWidth(),
				type.getHeight());
	}
	
	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if(wrapper != null) {
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

}
