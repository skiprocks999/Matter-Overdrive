// Credit to AurilisDev https://github.com/aurilisdev/Electrodynamics
package matteroverdrive.core.utils.misc;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import matteroverdrive.References;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class Scheduler {
	private static ConcurrentHashMap<Runnable, Integer> scheduledServer = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Runnable, Integer> scheduledClient = new ConcurrentHashMap<>();

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if (!scheduledServer.isEmpty()) {
			Iterator<Entry<Runnable, Integer>> it = scheduledServer.entrySet().iterator();
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
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if (!scheduledClient.isEmpty()) {
			Iterator<Entry<Runnable, Integer>> it = scheduledClient.entrySet().iterator();
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
	}

	public static void schedule(int timeUntil, Runnable run, boolean client) {
		if (client) {
			scheduledClient.put(run, timeUntil);
		} else {
			scheduledServer.put(run, timeUntil);
		}
	}

}