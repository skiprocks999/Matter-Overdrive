package matteroverdrive.core.tile.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

@FunctionalInterface
public interface MenuProviderFactory {
	AbstractContainerMenu build(MenuProvider provider, BlockPos pos, ContainerLevelAccess access,
			Inventory playerInventory, int menuId);
}
