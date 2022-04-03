package matteroverdrive.core.inventory;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.slot.vanilla.SlotVanillaContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public abstract class GenericInventory extends AbstractContainerMenu {

	private CapabilityInventory invcap;
	protected Player player;
	protected Level world;
	protected final int slotCount;
	protected int playerInvOffset = 0;
	private int nextIndex = 0;

	public int nextIndex() {
		return nextIndex++;
	}

	protected GenericInventory(MenuType<?> menu, int id, Inventory playerinv, CapabilityInventory invcap) {
		super(menu, id);
		world = playerinv.player.level;
		slotCount = slots.size();
		this.invcap = invcap;
		addPlayerInventory(playerinv);
		addInvSlots(invcap, playerinv);
		player = playerinv.player;
	}

	protected void addPlayerInventory(Inventory playerinv) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlot(new SlotVanillaContainer(playerinv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + playerInvOffset));
			}
		}

		for (int k = 0; k < 9; ++k) {
			addSlot(new SlotVanillaContainer(playerinv, k, 8 + k * 18, 142 + playerInvOffset));
		}
	}

	public abstract void addInvSlots(CapabilityInventory invcap, Inventory playerinv);

	@Override
	public boolean stillValid(Player pPlayer) {
		return invcap.isInRange(pPlayer);
	}

}
