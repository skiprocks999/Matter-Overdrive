package matteroverdrive.core.tile.utils;

import java.util.function.BiFunction;

import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class TileMenuProvider implements MenuProvider {

	private GenericTile owner;
	private String name;
	private BiFunction<Integer, Inventory, AbstractContainerMenu> createMenu;
	
	public TileMenuProvider(String name, GenericTile tile) {
		owner = tile;
		this.name = name;
	}
	
	public TileMenuProvider createMenu(BiFunction<Integer, Inventory, AbstractContainerMenu> function) {
		this.createMenu = function;
		return this;
	}
	
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		if (createMenu != null) {
			if (owner.hasCapability(CapabilityType.Item)) {
				ComponentInventory componentinv = holder.getComponent(ComponentType.Inventory);
				if (componentinv.stillValid(pl)) {
					componentinv.startOpen(pl);
				} else {
					return null;
				}
			}
			return createMenu.apply(id, inv);
		}
		return null;
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent(name);
	}

}
