package matteroverdrive.core.sound.tile;

import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class TickableSoundTile extends AbstractTickableSoundInstance {

	// Yes it's weird, but I couldn't think of a better way
	private static final double MAXIMUM_DISTANCE = 10;

	private GenericSoundTile tile;
	private final float initialVolume;
	private final float initialPitch;

	public TickableSoundTile(SoundEvent event, GenericSoundTile tile, boolean repeat) {
		this(event, tile, 0.5F, 1.0F, repeat);
	}

	public TickableSoundTile(SoundEvent event, GenericSoundTile tile, float volume, float pitch, boolean repeat) {
		super(event, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
		this.tile = tile;
		this.volume = 0.5F;
		this.pitch = 1.0F;
		BlockPos pos = tile.getBlockPos();
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.looping = repeat;
		initialVolume = volume;
		initialPitch = pitch;
	}

	@Override
	public void tick() {
		if (!tile.shouldPlaySound() || tile.isRemoved()) {
			stop();
		}
		Player player = Minecraft.getInstance().player;
		double distance = UtilsWorld.distanceBetweenPositions(player.blockPosition(), tile.getBlockPos());
		if (distance > 0 && distance <= MAXIMUM_DISTANCE) {
			this.volume = (float) (initialVolume / distance);
		} else if (distance > MAXIMUM_DISTANCE) {
			this.volume = 0;
		} else {
			this.volume = initialVolume;
		}
	}

	@Override
	public void stop() {
		super.stop();
		tile.setNotPlaying();
	}

	public void stopAbstract() {
		super.stop();
	}

}
