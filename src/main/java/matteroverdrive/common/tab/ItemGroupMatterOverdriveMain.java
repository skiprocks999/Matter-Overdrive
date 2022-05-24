package matteroverdrive.common.tab;

import matteroverdrive.DeferredRegisters;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupMatterOverdriveMain extends CreativeModeTab {

	public ItemGroupMatterOverdriveMain(String langKey) {
		super(langKey);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(DeferredRegisters.BLOCK_TRANSPORTER.get());
	}

}
