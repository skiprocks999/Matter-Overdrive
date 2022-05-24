package matteroverdrive.core.screen.component.button;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.SoundRegister;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.common.tile.transporter.TransporterLocationWrapper;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.utils.ButtonHoldPress;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;

public class ButtonTransporterLocation extends ButtonHoldPress {

	private static final int WIDTH = 110;
	private static final int HEIGHT = 20;

	public int index;
	private IScreenWrapper gui;
	private Supplier<TileTransporter> transporter;

	public ButtonTransporterLocation(int pX, int pY, int index, OnPress pOnPress, IScreenWrapper owner,
			Supplier<TileTransporter> transporter) {
		super(pX, pY, WIDTH, HEIGHT, TextComponent.EMPTY, pOnPress, (button, stack, mouseX, mouseY) -> {
			ButtonTransporterLocation loc = (ButtonTransporterLocation) button;
			TileTransporter transport = loc.transporter.get();
			TransporterLocationWrapper wrapper = transport.CLIENT_LOCATIONS[loc.index];
			if (transport.validDestination(wrapper).getFirst()) {
				loc.gui.displayTooltip(stack, new TextComponent(wrapper.getDestination().toShortString()), pX, pY);
			} else {
				loc.gui.displayTooltip(stack, UtilsText.tooltip("invaliddest"), pX, pY);
			}

		});
		this.index = index;
		gui = owner;
		this.transporter = transporter;
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		TileTransporter transport = transporter.get();
		int tIndex = transport.clientDestination;
		if (tIndex == index) {
			isActivated = true;
		} else {
			isActivated = false;
		}
		super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
		TransporterLocationWrapper wrapper = transport.CLIENT_LOCATIONS[index];
		int color = transport.validDestination(wrapper).getFirst() ? UtilsRendering.TEXT_BLUE : UtilsRendering.RED;
		drawCenteredString(pPoseStack, gui.getFontRenderer(), wrapper.getName(), this.x + this.width / 2,
				this.y + (this.height - 8) / 2, color);
	}
	
	@Override
	public void playDownSound(SoundManager manager) {
		manager.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_SOFT1.get(), 1.0F, 1.0F));
	}


}
