package matteroverdrive.core.screen.component.button;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.component.utils.AbstractOverdriveButton;
import matteroverdrive.core.screen.component.wrappers.WrapperIOConfig;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;

public class ButtonIO extends AbstractOverdriveButton {

	private static final int X_START = 0;
	private static final int Y_START = 9;

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;

	private Supplier<IOMode> startingMode;
	public IOMode mode;
	public final BlockSide side;
	private Supplier<Boolean> supplierInput;
	private Supplier<Boolean> supplierOutput;
	private Boolean hasInput;
	private Boolean hasOutput;

	private boolean isActivated = false;
	private WrapperIOConfig owner;

	private int mouseButton = 0;

	public ButtonIO(int x, int Y, Supplier<IOMode> startingMode, final BlockSide side, WrapperIOConfig owner,
			Supplier<Boolean> canInput, Supplier<Boolean> canOutput) {
		super(owner.gui, x, Y, WIDTH, HEIGHT, NO_TEXT, button -> {
		}, (button, stack, mouseX, mouseY) -> {
			ButtonIO io = (ButtonIO) button;
			owner.displayTooltip(stack, UtilsText.tooltip("io", io.mode.name, io.side.name), mouseX, mouseY);
		});
		this.startingMode = startingMode;
		this.side = side;
		this.owner = owner;
		supplierInput = canInput;
		supplierOutput = canOutput;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (mode == null) {
			mode = startingMode.get();
		}
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		UtilsRendering.bindTexture(ButtonTextures.GENERIC_BUTTONS.getTexture());
		int x;
		int y;
		switch (mode) {
		case INPUT:
			if (isActivated) {
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
			if (isActivated) {
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
		blit(stack, this.x, this.y, x, y, WIDTH, HEIGHT);
	}

	@Override
	public void onPress() {
		isActivated = true;
		validateNull();
		cycleMode();
		if (!hasInput && mode == IOMode.INPUT) {
			cycleMode();
		}
		if (!hasOutput && mode == IOMode.OUTPUT) {
			cycleMode();
		}
	}

	@Override
	protected boolean isValidClickButton(int button) {
		mouseButton = button;
		return button > -1 && button < 2;
	}

	private void validateNull() {
		if (hasInput == null) {
			if (supplierInput != null) {
				hasInput = supplierInput.get();
			} else {
				hasInput = false;
			}
		}
		if (hasOutput == null) {
			if (supplierOutput != null) {
				hasOutput = supplierOutput.get();
			} else {
				hasOutput = false;
			}
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		super.onRelease(mouseX, mouseY);
		isActivated = false;
	}

	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_SOFT1.get(), 1.0F));

	}

	private void cycleMode() {
		int modeVal = mode.ordinal();
		IOMode[] vals = IOMode.values();
		if (mouseButton == 0) {
			if (modeVal >= vals.length - 1) {
				mode = vals[0];
			} else {
				mode = vals[modeVal + 1];
			}
		} else {
			if (modeVal == 0) {
				mode = vals[vals.length - 1];
			} else {
				mode = vals[modeVal - 1];
			}
		}
		owner.childPressed();
	}

	public enum IOMode {
		INPUT, OUTPUT, NONE;

		public final MutableComponent name;

		private IOMode() {
			name = UtilsText.tooltip("io" + this.toString().toLowerCase());
		}
	}

	public enum BlockSide {
		TOP(Direction.UP), BOTTOM(Direction.DOWN), LEFT(Direction.EAST), RIGHT(Direction.WEST), FRONT(Direction.SOUTH),
		BACK(Direction.NORTH);

		public final MutableComponent name;
		public final Direction mappedDir;

		private BlockSide(Direction dir) {
			name = UtilsText.tooltip("io" + this.toString().toLowerCase());
			mappedDir = dir;
		}
	}

}
