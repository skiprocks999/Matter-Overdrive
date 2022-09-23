package matteroverdrive.common.tile.matter_network.matter_replicator.utils;

import matteroverdrive.common.tile.matter_network.matter_replicator.TileMatterReplicator;
import matteroverdrive.core.sound.SoundBarrierMethods;
import matteroverdrive.core.sound.tile.TickableSoundTile;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.Minecraft;

public class SoundHandlerReplicator {

	private TickableSoundTile continuousReplication;
	private TickableSoundTile singleReplication;
	private TileMatterReplicator replicator;

	private boolean playingReplicationSound = false;

	private boolean previousContinuous = true;

	public SoundHandlerReplicator(TileMatterReplicator replicator) {
		this.replicator = replicator;
	}

	public void tick(int adjustedTicks, boolean playing) {

		boolean playReplicateSound = ((replicator.getProcessingTime() - replicator.getProgress())
				/ (replicator.getCurrentSpeed() == 0 ? 1.0D
						: replicator.getCurrentSpeed())) <= TileMatterReplicator.SOUND_TICKS;
		boolean continuous = (adjustedTicks <= TileMatterReplicator.SOUND_TICKS) || (replicator.getRecipeValue() <= 1);

		if (!continuous && previousContinuous && playing) {
			playingReplicationSound = false;
			if (continuousReplication != null) {
				continuousReplication.stopAbstract();
			}
		}

		if (continuous && !previousContinuous && playing) {
			continuousReplication = new TickableSoundTile(SoundRegistry.SOUND_MATTER_REPLICATOR.get(), replicator,
					true);
			Minecraft.getInstance().getSoundManager().play(continuousReplication);
			if (singleReplication != null) {
				singleReplication.stopAbstract();
			}
		}

		previousContinuous = continuous;

		if (replicator.shouldPlaySound() && !playing) {
			continuousReplication = new TickableSoundTile(SoundRegistry.SOUND_MATTER_REPLICATOR.get(), replicator,
					true);
			if (continuous) {
				Minecraft.getInstance().getSoundManager().play(continuousReplication);
			}
			SoundBarrierMethods.playTileSound(SoundRegistry.SOUND_MACHINE.get(), replicator, true);
			replicator.setSoundPlaying();
		}

		if (replicator.getProgress() <= 12 && playing) {
			playingReplicationSound = false;
		}

		if (playing && playReplicateSound && !continuous && !playingReplicationSound) {
			singleReplication = new TickableSoundTile(SoundRegistry.SOUND_MATTER_REPLICATOR.get(), replicator, false);
			Minecraft.getInstance().getSoundManager().play(singleReplication);
			playingReplicationSound = true;
		}
	}

}
