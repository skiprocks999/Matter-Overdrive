package matteroverdrive.core.tile;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class GenericTile extends BlockEntity implements Nameable {
	
	private List<IOverdriveCapability> capabilities = new ArrayList<>();
	
	protected GenericTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		boolean valid = true;
		for(IOverdriveCapability i : capabilities) {
			if(i.getCapabilityType() == cap.getCapabilityType()) {
				valid = false;
				break;
			}
		}
		if(valid) {
			capabilities.add(cap);
		} else {
			throw new RuntimeException("error: capability type " + cap.getCapabilityType() + " already added");
		}
	}
	
	public <T extends IOverdriveCapability> T exposeCapability(CapabilityType type) {
		for (IOverdriveCapability cap : capabilities) {
			if (cap.getCapabilityType() == type) {
				return (T) cap;
			}
		}
		return null;
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
	
	public Direction getFacing() {
		Level world = getLevel();
		BlockState state = world.getBlockState(getBlockPos());
		if(state.hasProperty(GenericEntityBlock.FACING)) {
			return state.getValue(GenericEntityBlock.FACING);
		}
		return Direction.UP;
	}

	@Override
	public Component getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
