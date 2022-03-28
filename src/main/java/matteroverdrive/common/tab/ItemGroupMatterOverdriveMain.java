package matteroverdrive.common.tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ItemGroupMatterOverdriveMain extends CreativeModeTab {

	public ItemGroupMatterOverdriveMain(String langKey) {
		super(langKey);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(Blocks.ACACIA_BUTTON);
	}

}
