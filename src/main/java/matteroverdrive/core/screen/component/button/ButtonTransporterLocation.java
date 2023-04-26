package matteroverdrive.core.screen.component.button;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.common.tile.transporter.utils.TransporterLocationWrapper;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class ButtonTransporterLocation extends ButtonHoldPress {

	private static final int WIDTH = 110;
	private static final int HEIGHT = 20;

	public int index;
	private Supplier<TileTransporter> transporter;

	public ButtonTransporterLocation(GenericScreen<?> gui, int x, int y, int index, OnPress onPress,
			Supplier<TileTransporter> transporter) {
		super(gui, x, y, WIDTH, HEIGHT, NO_TEXT, onPress, (button, stack, mouseX, mouseY) -> {
			ButtonTransporterLocation loc = (ButtonTransporterLocation) button;
			TileTransporter transport = loc.transporter.get();
			TransporterLocationWrapper wrapper = transport.locationManager.getLocation(loc.index);
			if (transport.validDestination(wrapper).getFirst()) {
				loc.gui.renderTooltip(stack, Component.literal(wrapper.getDestination().toShortString()), mouseX,
						mouseY);
			} else {
				loc.gui.renderTooltip(stack, UtilsText.tooltip("invaliddest"), mouseX, mouseY);
			}

		});
		this.index = index;
		this.transporter = transporter;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		TileTransporter transport = transporter.get();
		int tIndex = transport.destinationProp.get();
		if (tIndex == index) {
			isActivated = true;
		} else {
			isActivated = false;
		}
		super.renderBackground(stack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderForeground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		TileTransporter transport = transporter.get();
		TransporterLocationWrapper wrapper = transport.locationManager.getLocation(index);
		int color = transport.validDestination(wrapper).getFirst() ? Colors.HOLO.getColor() : Colors.RED.getColor();
		drawCenteredString(stack, gui.getFontRenderer(), wrapper.getName(), this.x + this.width / 2,
				this.y + (this.height - 8) / 2, color);
	}

	@Override
	public void playDownSound(SoundManager manager) {
		manager.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_SOFT1.get(), 1.0F, 1.0F));
	}

}
