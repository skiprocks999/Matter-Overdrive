package matteroverdrive.core.tile;

import matteroverdrive.References;
import matteroverdrive.common.item.ItemUpgrade;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.tile.utils.ITickableTile;
import matteroverdrive.core.tile.utils.IUpdatableTile;
import matteroverdrive.core.utils.UtilsCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;

public abstract class GenericTile extends BlockEntity implements Nameable, ITickableTile, IUpdatableTile {

	private IOverdriveCapability[] capabilities = new IOverdriveCapability[CapabilityType.values().length];

	public boolean hasMenu = false;
	private MenuProvider menu;

	public boolean isTickable = false;
	
	public boolean hasMenuData = false;
	public boolean hasRenderData = false;
	
	protected long ticks = 0;

	protected GenericTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void setMenuProvider(MenuProvider menu) {
		hasMenu = true;
		this.menu = menu;
	}

	public MenuProvider getMenuProvider() {
		return menu;
	}

	public void setTickable() {
		isTickable = true;
	}
	
	public void setHasMenuData() {
		hasMenuData = true;
	}
	
	public void setHasRenderData() {
		hasRenderData = true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		for (IOverdriveCapability i : capabilities) {
			if (i.matchesCapability(cap)) {
				return i.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}

	public void addCapability(IOverdriveCapability cap) {
		CapabilityType type = cap.getCapabilityType();
		IOverdriveCapability capability = capabilities[type.ordinal()];
		if (capability != null) {
			throw new RuntimeException("error: capability type " + type + " already added");
		}
		capability = cap;
	}

	public boolean hasCapability(CapabilityType type) {
		return capabilities[type.ordinal()] != null;
	}

	public <T extends IOverdriveCapability> T exposeCapability(CapabilityType type) {
		return (T) capabilities[type.ordinal()];
	}

	@Override
	public void onLoad() {
		super.onLoad();
		for (IOverdriveCapability cap : capabilities) {
			cap.onLoad(this);
		}
	}

	public void refreshCapabilities() {
		for (IOverdriveCapability cap : capabilities) {
			cap.refreshCapability();
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (IOverdriveCapability cap : capabilities) {
			cap.invalidateCapability();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		for (IOverdriveCapability cap : capabilities) {
			tag.put(cap.getSaveKey(), cap.serializeNBT());
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		for (IOverdriveCapability cap : capabilities) {
			cap.deserializeNBT(tag.getCompound(cap.getSaveKey()));
		}
	}

	public MutableComponent getContainerName(String name) {
		return Component.translatable("container." + name);
	}

	public Direction getFacing() {
		Level world = getLevel();
		BlockState state = world.getBlockState(getBlockPos());
		if (state.hasProperty(GenericEntityBlock.FACING)) {
			return state.getValue(GenericEntityBlock.FACING);
		}

		return Direction.UP;
	}

	public SimpleContainerData getCoordsData() {
		SimpleContainerData array = new SimpleContainerData(3);
		array.set(0, worldPosition.getX());
		array.set(1, worldPosition.getY());
		array.set(2, worldPosition.getZ());
		return array;
	}

	@Override
	// TODO allow translations
	public Component getName() {
		return Component.literal(References.ID + ".default.tile.name");
	}

	protected static TriPredicate<Integer, ItemStack, CapabilityInventory> machineValidator() {
		return (x, y, i) -> x < i.outputIndex()
				|| x >= i.energySlotsIndex() && x < i.matterSlotsIndex() && UtilsCapability.hasEnergyCap(y)
				|| x >= i.matterSlotsIndex() && x < i.upgradeIndex() && UtilsCapability.hasMatterCap(y)
				|| x >= i.upgradeIndex() && y.getItem() instanceof ItemUpgrade upgrade
						&& i.isUpgradeValid(upgrade.type);
	}
	
	@Override
	public long getTicks() {
		return ticks;
	}
	
	public void incrementTicks() {
		ticks++;
	};

}
