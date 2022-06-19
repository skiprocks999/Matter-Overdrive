package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.tools.electric.ItemElectric;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class TilePatternStorage extends GenericTile implements IMatterNetworkMember {

	public boolean isPowered = false;
	
	public static final int SLOT_COUNT = 8;
	private static final int ENERGY_STORAGE = 64000;
	private static final int USAGE_PER_TICK = 100;
	
	private CapabilityInventory clientNetworkInv;
	private CapabilityInventory clientTileInv;
	
	public TilePatternStorage(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_PATTERN_STORAGE.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(7).setEnergySlots(1).setOwner(this)
				.setValidator(getValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}
	
	@Override
	public void tickServer() {
		
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		Direction facing = getFacing();
		Direction back = Direction.NORTH;
		Direction relative = UtilsDirection.getRelativeSide(back, facing);
		return relative == face;
	}
	
	@Override
	@Nullable
	public NetworkMatter getConnectedNetwork() {
		Direction back = UtilsDirection.getRelativeSide(Direction.NORTH, getFacing());
		BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(back));
		if(entity != null && entity instanceof TileMatterNetworkCable cable) {
			return (NetworkMatter) cable.getNetwork(false);
		}
		return null;
	}
	
	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();
		
		return data;
	}
	
	public void handleNetworkData(CompoundTag tag) {
		
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index < 7 && stack.getItem() instanceof ItemPatternDrive
				|| index == 7 && stack.getItem() instanceof ItemMatterScanner 
				|| index == 8 && stack.getItem() instanceof ItemElectric;
	}

}
