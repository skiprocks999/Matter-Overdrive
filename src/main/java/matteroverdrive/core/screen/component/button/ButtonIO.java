package matteroverdrive.core.screen.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.core.screen.component.IOConfigWrapper;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonIO extends Button {

	private static final ResourceLocation TEXTURE = new ResourceLocation(References.ID + ":textures/gui/button/buttons.png");
	
	private static final int X_START = 58;
	private static final int Y_START = 0;
	
	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	private IOMode mode;
	public final BlockSide side;
	
	private boolean isActivated = false;
	private IOConfigWrapper owner;
	
	public ButtonIO(int x, int Y, IOMode startingMode, final BlockSide side, IOConfigWrapper owner) {
		super(x, Y, WIDTH, HEIGHT, TextComponent.EMPTY, button -> {}, (button, stack, mouseX, mouseY) -> {
			ButtonIO io = (ButtonIO) button;
			TranslatableComponent text = new TranslatableComponent("tooltip.matteroverdrive.io", io.mode.name, io.side.name);
			owner.displayTooltip(stack, text, mouseX, mouseY);
		});
		mode = startingMode;
		this.side = side;
		this.owner = owner;
	}
	
	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		UtilsRendering.bindTexture(TEXTURE);
		int x;
		int y;
		switch(mode) {
		case INPUT:
			if(isActivated) {
				x = X_START + HEIGHT * 2;
				y = Y_START;
			} else if (isHoveredOrFocused()) {
				x = X_START;
				y = Y_START + HEIGHT;
			} else {
				x = X_START;
				y = Y_START;
			}
			break;
		case OUTPUT:
			if(isActivated) {
				x = X_START + WIDTH;
				y = Y_START + HEIGHT * 2;
			} else if (isHoveredOrFocused()) {
				x = X_START + WIDTH;
				y = Y_START + HEIGHT;
			} else {
				x = X_START + WIDTH;
				y = Y_START;
			}
			break;
		case NONE:
			x = X_START + WIDTH * 2;
			y = Y_START;
			break;
		default:
			x = X_START;
			y = Y_START;
			break;
		}
		blit(pPoseStack, this.x, this.y, x, y, WIDTH, HEIGHT);
		if(isHoveredOrFocused()) {
			renderToolTip(pPoseStack, pMouseX, pMouseY);
		}
	}
	
	@Override
	public void onPress() {
		isActivated = true;
		int modeVal = mode.ordinal();
		IOMode[] vals = IOMode.values();
		if(modeVal >= vals.length - 1) {
			mode = vals[0];
		} else {
			mode = vals[modeVal + 1];
		}
		owner.childPressed();
	}
	
	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		isActivated = false;
	}
	
	public enum IOMode {
		INPUT, OUTPUT, NONE;
		
		public final TranslatableComponent name;
		
		private IOMode() {
			name = new TranslatableComponent("tooltip.matteroverdrive.io" + this.toString().toLowerCase());
		}
	}
	
	public enum BlockSide {
		TOP(Direction.UP), BOTTOM(Direction.DOWN), LEFT(Direction.EAST), RIGHT(Direction.WEST), FRONT(Direction.NORTH), BACK(Direction.SOUTH);
		
		public final TranslatableComponent name;
		public final Direction mappedDir;
		
		private BlockSide(Direction dir) {
			name = new TranslatableComponent("tooltip.matteroverdrive.io" + this.toString().toLowerCase());
			mappedDir = dir;
		}
	}

}
