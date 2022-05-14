package matteroverdrive.core.capability;

import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class MatterOverdriveCapabilities {

	public static final Capability<ICapabilityMatterStorage> MATTER_STORAGE = CapabilityManager
			.get(new CapabilityToken<>() {
			});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(ICapabilityMatterStorage.class);
	}

}
