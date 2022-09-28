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

public abstract class GenericInventory extends AbstractContainerMenu {

	private CapabilityInventory invcap;
	protected Player player;
	protected Level world;
	protected final int slotCount;
	private int nextIndex = 0;
	protected boolean hasInventorySlots = true;
	protected boolean hasHotbarSlots = true;

	private final int hotbarX;
	private final int hotbarY;
	private final int playerInvX;
	private final int playerInvY;
	private final SlotType hotbarSlotType;
	private final SlotType playerInvSlotType;

	protected GenericInventory(MenuType<?> menu, int id, Inventory playerinv, CapabilityInventory invcap,
			int playerInvX, int playerInvY, int hotbarX, int hotbarY, SlotType hotbarSlotType,
			SlotType playerInvSlotType) {
		super(menu, id);
		world = playerinv.player.level;
		slotCount = slots.size();
		this.invcap = invcap;

		this.hotbarX = hotbarX;
		this.hotbarY = hotbarY;
		this.playerInvX = playerInvX;
		this.playerInvY = playerInvY;
		this.hotbarSlotType = hotbarSlotType;
		this.playerInvSlotType = playerInvSlotType;

		player = playerinv.player;

		addInvSlots(invcap, playerinv);
		addPlayerInventory(playerinv);

	}

	public int nextIndex() {
		return nextIndex++;
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
					addSlot(new SlotContainer(playerinv, j + i * 9 + 9, playerInvX + j * 18, 89 + i * 18,
							getPlayerInvNumbers(player), playerInvSlotType));
				}
			}
		}
		if (hasHotbarSlots) {
			for (int k = 0; k < 9; ++k) {
				addSlot(new SlotContainer(playerinv, k, 45 + k * 18, 150, getHotbarNumbers(player), hotbarSlotType));
			}
		}
	}

	public abstract void addInvSlots(CapabilityInventory invcap, Inventory playerinv);

	@Override
	public boolean stillValid(Player pPlayer) {
		return invcap.isInRange(pPlayer);
	}

	public CapabilityInventory getInventory() {
		return invcap;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return UtilsInventory.handleShiftClick(slots, player, index);
	}

	public abstract int[] getHotbarNumbers(Player player);

	public abstract int[] getPlayerInvNumbers(Player player);

}
