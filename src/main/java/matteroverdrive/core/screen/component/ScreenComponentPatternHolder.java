package matteroverdrive.core.screen.component;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
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

public class ScreenComponentPatternHolder extends OverdriveScreenComponent {

	private static final ResourceLocation MAIN_EMPTY = SlotType.MAIN.getTexture();
	private static final ResourceLocation BIG_EMPTY = SlotType.BIG.getTexture();
	private static final ResourceLocation BIG_FULL = SlotType.BIG_DARK.getTexture();

	private final ItemRenderer itemRenderer;

	private final ScreenComponentProgress progress;

	public ScreenComponentPatternHolder(GenericScreen<?> gui, int x, int y, int[] screenNumbers,
			ItemRenderer itemRenderer) {
		super(OverdriveTextures.NONE, gui, x, y, 37, 22, screenNumbers);
		this.itemRenderer = itemRenderer;
		progress = new ScreenComponentProgress(() -> {
			TileMatterReplicator matter = (TileMatterReplicator) ((GenericInventoryTile<?>)gui.getMenu()).getTile();
			if (matter != null) {
				return (double) matter.getProgress() / matter.getProcessingTime();
			}
			return 0;
		}, gui, x + 28, y + 3, screenNumbers);
	}

	@Override
	public void initScreenSize() {
		super.initScreenSize();
		progress.initScreenSize();
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		UtilsRendering.bindTexture(MAIN_EMPTY);
		blit(stack, x, y, 37, 22, 0, 0, 37, 22, 37, 22);
		progress.renderBackground(stack, mouseX, mouseY, partialTicks);
		TileMatterReplicator replicator = (TileMatterReplicator) ((GenericInventoryTile<?>)gui.getMenu()).getTile();
		if (replicator != null) {
			if (replicator.getCurrentOrder().isEmpty()) {
				UtilsRendering.bindTexture(BIG_FULL);
			} else {
				UtilsRendering.bindTexture(BIG_EMPTY);
			}
		} else {
			UtilsRendering.bindTexture(BIG_EMPTY);
		}
		blit(stack, x, y, 22, 22, 0, 0, 22, 22, 22, 22);
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		TileMatterReplicator replicator = (TileMatterReplicator) ((GenericInventoryTile<?>)gui.getMenu()).getTile();
		QueuedReplication order = null;
		if (replicator != null) {
			order = replicator.getCurrentOrder();
			if (order != null) {
				itemRenderer.renderGuiItem(new ItemStack(order.getItem()), this.x + 3, this.y + 3);
			}
		}
		if (isHoveredOrFocused()) {
			List<FormattedCharSequence> tooltips = new ArrayList<>();
			if (order != null) {
				Item item = order.getItem();
				int percentage = order.getPercentage();
				MutableComponent name = Component.translatable(item.getDescriptionId());
				ChatFormatting color = ChatFormatting.RED;
				if (percentage >= 100) {
					color = ChatFormatting.GREEN;
				} else if (percentage < 100 && percentage > 50) {
					color = ChatFormatting.YELLOW;
				} else {
					color = ChatFormatting.RED;
				}

				tooltips.add(UtilsText.tooltip("storedpattern", name.withStyle(color),
						UtilsText.formatPercentage(percentage)).getVisualOrderText());
				tooltips.add(UtilsText
						.tooltip("reporder", UtilsText.tooltip("orderabv").withStyle(ChatFormatting.AQUA),
								Component.literal(order.getOrderedCount() + "").withStyle(ChatFormatting.BOLD),
								UtilsText.tooltip("remainabv").withStyle(ChatFormatting.GOLD),
								Component.literal(order.getRemaining() + "").withStyle(ChatFormatting.BOLD))
						.getVisualOrderText());
			} else {
				tooltips.add(UtilsText.tooltip("noorder").getVisualOrderText());
			}
			gui.renderTooltip(stack, tooltips, mouseX, mouseY);
		}
	}

	protected boolean isPointInRegion(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 22 && mouseY < this.y + 22;
	}

}
