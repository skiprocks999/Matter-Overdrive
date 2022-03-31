package matteroverdrive.core.tile.utils;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import matteroverdrive.core.tile.GenericTile;
import net.minecraft.world.level.Level;

// Credit for class idea goes to AurilisDev
public class Ticker {

	private GenericTile owner;
	protected Consumer<Ticker> tickCommon;
	protected Consumer<Ticker> tickClient;
	protected Consumer<Ticker> tickServer;
	private long ticks = 0;

	public Ticker(GenericTile owner) {
		this.owner = owner;
	}

	public Ticker tickCommon(@Nonnull Consumer<Ticker> consumer) {
		Consumer<Ticker> safe = consumer;
		if (tickCommon != null) {
			safe = safe.andThen(tickCommon);
		}
		tickCommon = safe;
		return this;
	}

	public Ticker tickClient(@Nonnull Consumer<Ticker> consumer) {
		Consumer<Ticker> safe = consumer;
		if (tickClient != null) {
			safe = safe.andThen(tickClient);
		}
		tickClient = safe;
		return this;
	}

	public Ticker tickServer(@Nonnull Consumer<Ticker> consumer) {
		Consumer<Ticker> safe = consumer;
		if (tickServer != null) {
			safe = safe.andThen(tickServer);
		}
		tickServer = safe;
		return this;
	}

	public void tickCommon() {
		ticks++;
		if (tickCommon != null) {
			tickCommon.accept(this);
		}
	}

	public void tickServer() {
		if (tickServer != null) {
			tickServer.accept(this);
		}
	}

	public void tickClient() {
		if (tickClient != null) {
			tickClient.accept(this);
		}
	}

	public long getTicks() {
		return ticks;
	}

	public void performTick(Level level) {
		tickCommon();
		if (!level.isClientSide) {
			tickServer();
		} else {
			tickClient();
		}
	}


}
