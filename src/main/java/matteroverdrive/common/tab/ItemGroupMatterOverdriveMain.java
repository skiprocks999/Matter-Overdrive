package matteroverdrive.common.tab;

import matteroverdrive.registry.BlockRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupMatterOverdriveMain extends CreativeModeTab {

	public ItemGroupMatterOverdriveMain(String langKey) {
		super(langKey);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(BlockRegistry.BLOCK_CHUNKLOADER.get());
	}

}
