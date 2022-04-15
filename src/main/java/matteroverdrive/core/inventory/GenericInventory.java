package matteroverdrive.core.inventory;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.inventory.slot.SlotContainer;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.utils.UtilsInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public abstract class GenericInventory extends AbstractContainerMenu {

	private IItemHandler invcap;
	protected Player player;
	protected Level world;
	protected final int slotCount;
	protected int playerInvOffset = 0;
	private int nextIndex = 0;
	private boolean hasInventorySlots = true;
	private boolean hasHotbarSlots = true;

	public int nextIndex() {
		return nextIndex++;
	}

	protected GenericInventory(MenuType<?> menu, int id, Inventory playerinv, IItemHandler invcap) {
		super(menu, id);
		world = playerinv.player.level;
		slotCount = slots.size();
		this.invcap = invcap;
		addInvSlots(invcap, playerinv);
		addPlayerInventory(playerinv);
		player = playerinv.player;
	}

	public <T extends GenericInventory> T setNoInventory() {
		hasInventorySlots = false;
		return (T) this;
	}

	public <T extends GenericInventory> T setNoHotbar() {
		hasHotbarSlots = false;
		return (T) this;
	}

	protected void addPlayerInventory(Inventory playerinv) {
		if (hasInventorySlots) {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					addSlot(new SlotContainer(playerinv, j + i * 9 + 9, 8 + j * 18, 81 + i * 18 + playerInvOffset + 8,
							SlotType.SMALL));
				}
			}
		}
		if (hasHotbarSlots) {
			for (int k = 0; k < 9; ++k) {
				addSlot(new SlotContainer(playerinv, k, 8 + k * 18, 142 + playerInvOffset + 8, SlotType.SMALL));
			}
		}
	}

	public abstract void addInvSlots(IItemHandler invcap, Inventory playerinv);

	@Override
	public boolean stillValid(Player pPlayer) {
		if (invcap instanceof CapabilityInventory inv) {
			inv.isInRange(pPlayer);
		}
		return true;
	}

	public IItemHandler getHandler() {
		return invcap;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return UtilsInventory.handleShiftClick(slots, player, index);
	}

}
