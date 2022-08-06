package matteroverdrive.core.capability;

import matteroverdrive.core.capability.types.entity_data.ICapabilityEntityData;
import matteroverdrive.core.capability.types.item_pattern.ICapabilityItemPatternStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.capability.types.overworld_data.ICapabilityOverworldData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class MatterOverdriveCapabilities {

	public static final Capability<ICapabilityMatterStorage> MATTER_STORAGE = CapabilityManager
			.get(new CapabilityToken<>() {
			});
	public static final Capability<ICapabilityEntityData> ENTITY_DATA = CapabilityManager.get(new CapabilityToken<>() {
	});
	public static final Capability<ICapabilityOverworldData> OVERWORLD_DATA = CapabilityManager
			.get(new CapabilityToken<>() {
			});
	public static final Capability<ICapabilityItemPatternStorage> STORED_PATTERNS = CapabilityManager
			.get(new CapabilityToken<>() {
			});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(ICapabilityMatterStorage.class);
		event.register(ICapabilityEntityData.class);
		event.register(ICapabilityOverworldData.class);
		event.register(ICapabilityItemPatternStorage.class);
	}

}
