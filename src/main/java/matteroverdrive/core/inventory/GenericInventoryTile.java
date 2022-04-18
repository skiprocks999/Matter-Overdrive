package matteroverdrive.core.inventory;

import javax.annotation.Nullable;

import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;

public abstract class GenericInventoryTile<T extends BlockEntity> extends GenericInventory {

	protected final ContainerData tilecoords;

	protected GenericInventoryTile(MenuType<?> menu, int id, Inventory playerinv, IItemHandler invcap,
			ContainerData tilecoords) {
		super(menu, id, playerinv, invcap);
		this.tilecoords = tilecoords;
		addDataSlots(tilecoords);
	}

	@Nullable
	public T getTile() {
		try {
			return (T) world.getBlockEntity(new BlockPos(tilecoords.get(0), tilecoords.get(1), tilecoords.get(2)));
		} catch (Exception e) {
			return null;
		}
	}

	@Nullable
	public BlockEntity getTileUnsafe() {
		return world.getBlockEntity(new BlockPos(tilecoords.get(0), tilecoords.get(1), tilecoords.get(2)));
	}
	
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if(!player.level.isClientSide && getTile() != null && getTile() instanceof GenericTile generic && generic.hasMenuPacketHandler) {
			generic.getMenuPacketHandler().sendCustomPacket(player);
		}
	}

}
