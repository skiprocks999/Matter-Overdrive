package matteroverdrive.core.tile;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.tile.utils.PacketHandler;
import matteroverdrive.core.tile.utils.Ticker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class GenericTile extends BlockEntity implements Nameable {

	private List<IOverdriveCapability> capabilities = new ArrayList<>();

	public boolean hasMenu = false;
	private MenuProvider menu;

	public boolean hasTicker = false;
	private Ticker ticker;

	public boolean hasPacketHandler = false;
	private PacketHandler handler;

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

	public void setTicker(Ticker ticker) {
		hasTicker = true;
		this.ticker = ticker;
	}

	public Ticker getTicker() {
		return ticker;
	}

	public void setPacketHandler(PacketHandler handler) {
		hasPacketHandler = true;
		this.handler = handler;
	}

	public PacketHandler getPacketHandler() {
		return handler;
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

	public boolean hasCapability(CapabilityType type) {
		for(IOverdriveCapability cap : capabilities) {
			if(cap.getCapabilityType() == type) {
				return true;
			}
		}
		return false;
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

	public SimpleContainerData getCoordsData() {
		SimpleContainerData array = new SimpleContainerData(3);
		array.set(0, worldPosition.getX());
		array.set(1, worldPosition.getY());
		array.set(2, worldPosition.getZ());
		return array;
	}


	@Override
	//TODO allow translations
	public Component getName() {
		return new TextComponent(References.ID + ".default.tile.name");
	}

}
