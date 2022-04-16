package matteroverdrive.core.inventory.slot;

public interface IToggleableSlot extends ISlotType {

	public void setActive(boolean active);
	
	public void setScreenNumber(int[] numbers);
	
	public boolean isScreenNumber(int number);
	
	public int[] getScreenNumbers();
	
}
