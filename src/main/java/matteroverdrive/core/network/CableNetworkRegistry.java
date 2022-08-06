package matteroverdrive.core.network;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import matteroverdrive.References;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class CableNetworkRegistry {

	private static final Set<AbstractCableNetwork> SERVER_NETWORKS = ConcurrentHashMap.newKeySet();
	private static final Set<AbstractCableNetwork> SHOULD_REMOVE_SERVER = ConcurrentHashMap.newKeySet();

	private static final Set<AbstractCableNetwork> CLIENT_NETWORKS = ConcurrentHashMap.newKeySet();
	private static final Set<AbstractCableNetwork> SHOULD_REMOVE_CLIENT = ConcurrentHashMap.newKeySet();

	public static void register(AbstractCableNetwork network, boolean client) {
		if (client) {
			CLIENT_NETWORKS.add(network);
		} else {
			SERVER_NETWORKS.add(network);
		}
	}

	public static void deregister(AbstractCableNetwork network, boolean client) {
		if (client) {
			if (CLIENT_NETWORKS.contains(network)) {
				SHOULD_REMOVE_CLIENT.add(network);
			}
		} else {
			if (SERVER_NETWORKS.contains(network)) {
				SHOULD_REMOVE_SERVER.add(network);
			}
		}
	}

	@SubscribeEvent
	public static void tickServerNetworks(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			SERVER_NETWORKS.removeAll(SHOULD_REMOVE_SERVER);
			SHOULD_REMOVE_SERVER.clear();
			for (AbstractCableNetwork network : SERVER_NETWORKS) {
				// safety check
				if (network != null) {
					if (network.getSize() > 0) {
						network.tick();
					} else {
						network.deregister(false);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void tickClientNetworks(ClientTickEvent event) {
		if (event.phase == Phase.END) {
			CLIENT_NETWORKS.removeAll(SHOULD_REMOVE_CLIENT);
			SHOULD_REMOVE_CLIENT.clear();
			for (AbstractCableNetwork network : CLIENT_NETWORKS) {
				// safety check
				if (network != null) {
					if (network.getSize() > 0) {
						network.tick();
					} else {
						network.deregister(true);
					}
				}
			}
		}
	}

}