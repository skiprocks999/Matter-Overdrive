package matteroverdrive.core.property;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Interface for Objects that can manage properties. Default Implementations of
 * this is: - Containers {@link MenuType} - Entities {@link Entity} - Block
 * Entities {@link BlockEntity}
 */
public interface IPropertyManaged {
	/**
	 * Gets the {@link PropertyManager} for the managing object.
	 *
	 * @return Returns the objects {@link PropertyManager}
	 */
	PropertyManager getPropertyManager();
}
