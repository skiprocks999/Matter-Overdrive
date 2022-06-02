package matteroverdrive.common.block.states;

import matteroverdrive.common.block.charger.BlockAndroidChargerParent;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class OverdriveBlockStates {
	
	public static void init() {
		
	}
	
	public static final EnumProperty<BlockAndroidChargerParent.Position> CHARGER_POS = 
			EnumProperty.create("charger_pos", BlockAndroidChargerParent.Position.class, BlockAndroidChargerParent.Position.BOTTOM,
					BlockAndroidChargerParent.Position.MIDDLE, BlockAndroidChargerParent.Position.TOP);

}
