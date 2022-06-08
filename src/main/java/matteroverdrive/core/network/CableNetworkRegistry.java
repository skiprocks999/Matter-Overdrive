package matteroverdrive.core.network;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import matteroverdrive.References;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class CableNetworkRegistry {
	private static final Set<BaseNetwork> NETWORKS = ConcurrentHashMap.newKeySet();
	private static final Set<BaseNetwork> SHOULD_REMOVE = ConcurrentHashMap.newKeySet();

	public static void register(BaseNetwork network) {
		NETWORKS.add(network);
	}

	public static void deregister(BaseNetwork network) {
		if (NETWORKS.contains(network)) {
			SHOULD_REMOVE.add(network);
		}
	}

	@SubscribeEvent
	public static void tickNetworks(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			NETWORKS.removeAll(SHOULD_REMOVE);
			SHOULD_REMOVE.clear();
			for(BaseNetwork network : NETWORKS) {
				// safety check
				if(network != null) {
					if(network.getSize() > 0) {
						network.tick();
					} else {
						network.tick();
					}
				}
			}
		}
	}

}