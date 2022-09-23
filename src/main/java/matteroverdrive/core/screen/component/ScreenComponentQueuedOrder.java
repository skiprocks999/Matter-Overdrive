package matteroverdrive.core.screen.component;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ScreenComponentQueuedOrder extends OverdriveScreenComponent {

	private final ItemRenderer itemRenderer;
	private QueuedReplication order;

	public ScreenComponentQueuedOrder(GenericScreen<?> gui, int x, int y, int[] screenNumbers,
			ItemRenderer itemRenderer) {
		super(OverdriveTextures.ORDER_SEARCH_BAR, gui, x, y, 158, 20, screenNumbers);
		this.itemRenderer = itemRenderer;
	}

	public void setOrder(QueuedReplication replication) {
		order = replication;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (order != null) {
			UtilsRendering.bindTexture(resource.getTexture());
			blit(stack, x, y, width, height, 0, 0, width, height, width, height);
		}
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (order != null) {
			itemRenderer.renderGuiItem(new ItemStack(order.getItem()), x + 2, y + 2);
			MutableComponent order = UtilsText.gui("orderratio", this.order.getRemaining(),
					this.order.getOrderedCount());
			gui.getFontRenderer().draw(stack, order, x + 22, y + 6.5F, Colors.WHITE.getColor());
		}
	}

	public boolean isFilled() {
		return order != null;
	}

	public QueuedReplication getOrder() {
		return order;
	}

}
