package matteroverdrive.core.cable;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

import matteroverdrive.References;
import matteroverdrive.core.cable.api.ITickableCableNetwork;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.FORGE)
public class CableNetworkRegistry {
	private static final HashSet<ITickableCableNetwork> networks = new HashSet<>();
	private static final HashSet<ITickableCableNetwork> remove = new HashSet<>();

	public static void register(ITickableCableNetwork network) {
		networks.add(network);
	}

	public static void deregister(ITickableCableNetwork network) {
		if (networks.contains(network)) {
			remove.add(network);
		}
	}

	@SubscribeEvent
	public static void update(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			try {
				networks.removeAll(remove);
				remove.clear();
				Iterator<ITickableCableNetwork> it = networks.iterator();
				while (it.hasNext()) {
					ITickableCableNetwork net = it.next();
					if (net.getSize() == 0) {
						deregister(net);
					} else {
						net.tick();
					}
				}
			} catch (ConcurrentModificationException exception) {
				exception.printStackTrace();
			}
		}
	}

}