package matteroverdrive.core.inventory;

import javax.annotation.Nullable;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.item.PlayerSlotDataWrapper;
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

	@Nullable
	private final PlayerSlotDataWrapper wrapper;

	protected GenericInventory(MenuType<?> menu, int id, Inventory playerinv, CapabilityInventory invcap) {
		super(menu, id);
		world = playerinv.player.level;
		slotCount = slots.size();
		this.invcap = invcap;
		player = playerinv.player;
		
		wrapper = getDataWrapper(player);
		
		init();
		
	}
	
	public void init() {
		addInvSlots(invcap, player.getInventory());
		addPlayerInventory(player.getInventory());
	}

	public int nextIndex() {
		return nextIndex++;
	}

	protected void addPlayerInventory(Inventory playerinv) {
		if (hasInventorySlots) {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					addSlot(new SlotContainer(playerinv, j + i * 9 + 9, wrapper.pInvStartX() + j * wrapper.pInvSlotW(),
							wrapper.pInvStartY() + i * wrapper.pInvSlotH(), wrapper.pInvNumbers(),
							wrapper.pInvSlotType()));
				}
			}
		}
		if (hasHotbarSlots) {
			for (int k = 0; k < 9; ++k) {
				addSlot(new SlotContainer(playerinv, k, wrapper.hotbarStartX() + k * wrapper.hotbarSlotW(),
						wrapper.hotbarStartY(), wrapper.hotbarNumbers(), wrapper.hotbarSlotType()));
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

	public abstract PlayerSlotDataWrapper getDataWrapper(Player player);
	
	public static PlayerSlotDataWrapper defaultOverdriveScreen(int[] hotbarNumbers, int[] inventoryNumbers) {
		return new PlayerSlotDataWrapper(45, 89, 18, 18, 45, 150, 18, 18, SlotType.SMALL, SlotType.SMALL, hotbarNumbers, inventoryNumbers);
	}
	
	public static PlayerSlotDataWrapper defaultVanillaScreen(int[] hotbarNumbers, int[] inventoryNumbers) {
		return new PlayerSlotDataWrapper(8, 84, 18, 18, 8, 142, 18, 18, SlotType.VANILLA,
				SlotType.VANILLA, hotbarNumbers, inventoryNumbers);
	}

}
