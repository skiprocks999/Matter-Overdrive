package matteroverdrive.core.inventory.slot;

public interface IToggleableSlot extends ISlotType {

	public void setActive(boolean active);

	public boolean isScreenNumber(int number);

	public int[] getScreenNumbers();

}
