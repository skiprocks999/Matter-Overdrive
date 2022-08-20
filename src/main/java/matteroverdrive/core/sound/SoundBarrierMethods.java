package matteroverdrive.core.sound;

import matteroverdrive.core.sound.tile.TickableSoundTile;
import matteroverdrive.core.tile.types.GenericSoundTile;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class SoundBarrierMethods {

	public static void playTileSound(SoundEvent event, GenericSoundTile tile, boolean repeat) {
		Minecraft.getInstance().getSoundManager().play(new TickableSoundTile(event, tile, repeat));
	}

	public static void playTileSound(SoundEvent event, GenericSoundTile tile, float volume, float pitch,
			boolean repeat) {
		Minecraft.getInstance().getSoundManager().play(new TickableSoundTile(event, tile, volume, pitch, repeat));
	}

}
