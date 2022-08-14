package matteroverdrive.core.screen.component.wrappers.utils;

import java.util.List;
import java.util.function.Consumer;

import matteroverdrive.common.tile.matter_network.matter_replicator.utils.QueuedReplication;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketCancelReplication;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentLabel;
import matteroverdrive.core.screen.component.ScreenComponentQueuedOrder;
import matteroverdrive.core.screen.component.ScreenComponentVerticalSlider;
import matteroverdrive.core.screen.component.button.ButtonGeneric;
import matteroverdrive.core.screen.component.button.ButtonGeneric.ButtonType;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public abstract class AbstractWrapperReplicationQueue {

	private final int x;
	private final int y;
	private final int[] screenNumbers;
	private ScreenComponentQueuedOrder[] orders = new ScreenComponentQueuedOrder[6];
	private ButtonGeneric[] cancelButtons = new ButtonGeneric[6];
	private int topRowIndex = 0;
	private int lastRowCount = 0;
	protected final GenericScreen<?> screen;
	private ScreenComponentLabel label;
	
	public AbstractWrapperReplicationQueue(GenericScreen<?> screen, int x, int y, int[] screenNumbers) {
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.screenNumbers = screenNumbers;
	}
	
	public void initButtons(ItemRenderer renderer) {
		
		label = new ScreenComponentLabel(screen, x, y, screenNumbers, getCatagoryName(), UtilsRendering.TEXT_BLUE);
		
		int xOff = -2;
		int yOff = 10;
		
		for(int i = 0; i < 6; i++) {
			final int index = i;
			orders[i] = new ScreenComponentQueuedOrder(screen, x + xOff, y + yOff + i * 21, screenNumbers, renderer);
			cancelButtons[i] = new ButtonGeneric(screen, x + xOff + 143, y +  + yOff + 5 + i * 21, ButtonType.CLOSE_RED, button -> {
				handleOrderCancel(index, orders[index].getOrder());
			});
		}
		
		screen.addScreenComponent(label);
		
		for(int i = 0; i < 6; i++) {
			screen.addScreenComponent(orders[i]);
			screen.addButton(cancelButtons[i]);
		}
		
		
	}
	
	public void tick() {
		List<QueuedReplication> orders = getOrders();
		lastRowCount = orders.size();
		ScreenComponentQueuedOrder component;
		int index;
		for(int i = 0; i < 6; i++) {
			component = this.orders[i];
			index = topRowIndex + i;
			if(index < orders.size()) {
				component.setOrder(orders.get(index));
			} else {
				component.setOrder(null);
			}
			component.visible = component.isFilled();
			cancelButtons[i].visible = component.visible;
		}
		
		ScreenComponentVerticalSlider slider = getSlider();
		if(lastRowCount > 6) {
			slider.updateActive(true);
			if(!slider.isSliderHeld()) {
				int moveRoom = slider.getHeight() - 15 - 4;
				double moved = (double) topRowIndex / (double) (lastRowCount - 6.0D);
				slider.setSliderYOffset((int) ((double) moveRoom * moved));
			}
		} else {
			slider.updateActive(false);
			slider.setSliderYOffset(0);
			topRowIndex = 0;
		}
		
	}
	
	public void updateButtons(boolean visible) {
		ScreenComponentQueuedOrder order;
		boolean vis;
		for(int i = 0; i < 6; i++) {
			order = orders[i];
			vis = order.isFilled() && visible;;
			order.visible = vis;
			cancelButtons[i].visible = vis;
		}
		label.visible = visible;
	}
	
	//pos for down, neg for up
	public void handleMouseScroll(int dir) {
		if(Screen.hasControlDown()) {
			dir*= 4;
		}
		int lastRowIndex = lastRowCount - 1;
		if(lastRowCount > 6) {
			//check in case something borked
			if(topRowIndex >= lastRowCount) {
				topRowIndex = lastRowIndex - 5;
			}
			topRowIndex = Mth.clamp(topRowIndex += dir, 0, lastRowIndex - 5);
		} else {
			topRowIndex = 0;
		}
	}
	
	public Consumer<Integer> getSliderClickedConsumer() {
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = getSlider();
			if(slider.isSliderActive()) {
				int sliderY = slider.y;
				int sliderHeight = slider.getHeight();
				int mouseHeight = mouseY - sliderY;
				if(mouseHeight >= sliderHeight - 4 - 15) {
					topRowIndex = lastRowCount - 6;
					slider.setSliderYOffset(sliderHeight - 4 - 15);
				} else if (mouseHeight <= 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else {
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - 6) * heightRatio);
					int moveRoom = slider.getHeight() - 15 - 4;
					double moved = (double) topRowIndex / (double) (lastRowCount - 6.0D);
					slider.setSliderYOffset((int) ((double) moveRoom * moved));
				}
			}
		};
	}
	
	public Consumer<Integer> getSliderDraggedConsumer(){
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = getSlider();
			if(slider.isSliderActive()) {
				int sliderY = slider.y;
				int sliderHeight = slider.getHeight();
				if(mouseY <= sliderY + 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else if (mouseY >= sliderY + sliderHeight - 4 - 15) {
					topRowIndex = lastRowCount - 6;
					slider.setSliderYOffset(sliderHeight - 4 - 15);
				} else {
					int mouseHeight = mouseY - sliderY;
					slider.setSliderYOffset(mouseHeight);
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - 6) * heightRatio);
				}
			}
		};
	}
	
	public abstract ScreenComponentVerticalSlider getSlider();
	
	public abstract List<QueuedReplication> getOrders();
	
	public abstract MutableComponent getCatagoryName();
	
	public void handleOrderCancel(int buttonIndex, QueuedReplication order) {
		if(order != null) {
			NetworkHandler.CHANNEL.sendToServer(new PacketCancelReplication(order.getOwnerPos(), order.getQueuePos()));
		}	
	}
	
}
