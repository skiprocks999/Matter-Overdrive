package matteroverdrive.common.event.handler.tick;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import matteroverdrive.core.eventhandler.server.AbstractServerTickHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent.Phase;

public class ScheduledTaskHandler extends AbstractServerTickHandler {

	private ConcurrentHashMap<Runnable, Integer> tasks = new ConcurrentHashMap<>();
	
	@Override
	public void handleTick(MinecraftServer server, Phase phase, boolean enoughTime) {
		Iterator<Entry<Runnable, Integer>> it = tasks.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Runnable, Integer> next = it.next();
			if (next.getValue() <= 0) {
				next.getKey().run();
				it.remove();
			} else {
				next.setValue(next.getValue() - 1);
			}
		}
	}
	
	public void queueTask(@Nonnull Runnable run, int delay) {
		tasks.put(run, delay);
	}
	
	public void queueTask(@Nonnull Runnable run) {
		queueTask(run, 1);
	}

}
