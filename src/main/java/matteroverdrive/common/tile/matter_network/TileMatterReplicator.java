package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryMatterReplicator;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.tools.electric.ItemElectric;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericSoundTile;
import matteroverdrive.core.utils.UtilsDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class TileMatterReplicator extends GenericSoundTile implements IMatterNetworkMember {

	public static final int SLOT_COUNT = 9;
	private static final int ENERGY_STORAGE = 512000;
	private static final int USAGE_PER_TICK = 110;
	private static final int MATTER_STORAGE = 1024;
	
	public CapabilityInventory clientNetworkInv;
	
	public TileMatterReplicator(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_MATTER_REPLICATOR.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(2).setOutputs(2).setEnergySlots(1)
				.setUpgrades(4).setOwner(this).setValidUpgrades(InventoryMatterReplicator.UPGRADES)
				.setValidator(getValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		addCapability(new CapabilityMatterStorage(MATTER_STORAGE, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryMatterReplicator(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.MATTER_REPLICATOR.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}
	
	@Override
	public void tickServer() {

	}

	@Override
	public boolean shouldPlaySound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNotPlaying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxMode() {
		// TODO Auto-generated method stub
		return 0;
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
	
	@Override
	public boolean isPowered(boolean client, boolean network) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public CompoundTag getNetworkData() {
		CompoundTag data = new CompoundTag();
		
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		data.put(inv.getSaveKey(), inv.serializeNBT());
		
		return data;
	}
	
	public void handleNetworkData(CompoundTag tag) {
		clientNetworkInv = new CapabilityInventory();
		clientNetworkInv.deserializeNBT(tag.getCompound(clientNetworkInv.getSaveKey()));
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index < 7 && stack.getItem() instanceof ItemPatternDrive
				|| index == 7 && stack.getItem() instanceof ItemMatterScanner 
				|| index == 8 && stack.getItem() instanceof ItemElectric;
	}
	
}
