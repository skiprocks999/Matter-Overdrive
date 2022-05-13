package matteroverdrive.common.item.tools;

import matteroverdrive.References;
import matteroverdrive.common.item.utils.OverdriveItem;
import net.minecraft.world.item.Item;

public class ItemTransporterFlashdrive extends OverdriveItem {

	public ItemTransporterFlashdrive() {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN));
	}

}
