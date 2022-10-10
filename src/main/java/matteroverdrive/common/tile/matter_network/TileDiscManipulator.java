package matteroverdrive.common.tile.matter_network;

import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryDiscManipulator;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.property.Property;
import matteroverdrive.core.property.PropertyTypes;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.registry.TileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileDiscManipulator extends GenericTile {

	public static final int SIZE = 1;

	public final Property<CompoundTag> capInventoryProp;

	public TileDiscManipulator(BlockPos pos, BlockState state) {
		super(TileRegistry.TILE_DISC_MANIPULATOR.get(), pos, state);

		capInventoryProp = this.getPropertyManager().addTrackedProperty(PropertyTypes.NBT
				.create(() -> getInventoryCap().serializeNBT(), tag -> getInventoryCap().deserializeNBT(tag)));

		addCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new CapabilityInventory(SIZE, false, false)
				.setOwner(this).setInputs(1).setValidator((getValidator())).setPropertyManager(capInventoryProp));
		setMenuProvider(new SimpleMenuProvider(
				(id, inv, play) -> new InventoryDiscManipulator(id, play.getInventory(),
						exposeCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY), getCoordsData()),
				getContainerName(TypeMachine.DISC_MANIPULATOR.id())));

	}

	@Override
	public void onInventoryChange(int slot, CapabilityInventory inv) {
		setChanged();
	}

	public CapabilityInventory getInventoryCap() {
		return exposeCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
	}

	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index == 0 && stack.getItem() instanceof ItemPatternDrive drive
				&& !drive.isFused(stack);
	}

}
