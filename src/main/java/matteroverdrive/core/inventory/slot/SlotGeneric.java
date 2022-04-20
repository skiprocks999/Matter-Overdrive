package matteroverdrive.core.inventory.slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotGeneric extends Slot implements IToggleableSlot {

	private SlotType type;
	@Nullable
	private IconType icon;
	private boolean isActive;
	private int[] screenNumbers;
	
	private static Container emptyInventory = new SimpleContainer(0);
    private final CapabilityInventory inventory;
    private final int index;

	public SlotGeneric(CapabilityInventory inventory, int index, int xPosition, int yPosition, int[] screenNumbers, SlotType type, IconType icon) {
		super(emptyInventory, index, xPosition, yPosition);
        this.inventory = inventory;
        this.index = index;
		this.type = type;
		this.screenNumbers = screenNumbers;
		this.icon = icon;
	}

	public SlotGeneric(CapabilityInventory inventory, int index, int xPosition, int yPosition, int[] screenNumbers) {
		this(inventory, index, xPosition, yPosition, screenNumbers, SlotType.SMALL, null);
	}

	@Override
	public SlotType getSlotType() {
		return type;
	}

	@Override
	public void setActive(boolean active) {
		isActive = active;
	}

	@Override
	public boolean isScreenNumber(int number) {
		for(int num : screenNumbers) {
			if (num == number) return true;
		}
		return false;
	}

	@Override
	public int[] getScreenNumbers() {
		return screenNumbers;
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public IconType getIconType() {
		return icon;
	}
	   
    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
        	return false;
        }
        return inventory.isItemValid(index, stack);
    }

    @Override
    @Nonnull
    public ItemStack getItem() {
        return this.getInventory().getStackInSlotNoCheck(index);
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        this.getInventory().setStackInSlotNoCheck(index, stack);
        this.setChanged();
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) { }

    @Override
    public int getMaxStackSize() {
        return this.getInventory().getSlotLimit(this.index);
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        CapabilityInventory inventory = this.getInventory();
        ItemStack currentStack = inventory.getStackInSlotNoCheck(index);
        inventory.setStackInSlotNoCheck(index, ItemStack.EMPTY);
        ItemStack remainder = inventory.insertItemNoCheck(index, maxAdd, true);
        inventory.setStackInSlotNoCheck(index, currentStack);
        
        return maxInput - remainder.getCount();
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !this.getInventory().extractItemNoCheck(index, 1, true).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        return this.getInventory().extractItemNoCheck(index, amount, false);
    }

    public CapabilityInventory getInventory() {
        return inventory;
    }
	
}
