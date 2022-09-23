package matteroverdrive.core.screen.component.button;

import java.util.function.Supplier;

import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class ButtonRedstoneMode extends ButtonOverdrive {

	private Supplier<Integer> mode;

	public ButtonRedstoneMode(GenericScreen<?> gui, int x, int y, OnPress pOnPress, Supplier<Integer> mode) {
		super(gui, x, y, 58, 20, Component.empty(), pOnPress);
		this.mode = mode;
	}

	@Override
	public void playDownSound(SoundManager pHandler) {
		pHandler.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F));
	}

	@Override
	public Component getMessage() {
		switch (mode.get()) {
		case 0:
			return UtilsText.gui("redstonelow");
		case 1:
			return UtilsText.gui("redstonehigh");
		case 2:
			return UtilsText.gui("redstonenone");
		default:
			return super.getMessage();
		}
	}

}
