package matteroverdrive.common.item.tools.electric;

import matteroverdrive.References;
import net.minecraft.world.item.Item;

public class ItemMatterScanner extends ItemElectric {

	public static final int MAX_STORAGE = 20000;
	
	public ItemMatterScanner() {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN), MAX_STORAGE, true, false);
	}

}
