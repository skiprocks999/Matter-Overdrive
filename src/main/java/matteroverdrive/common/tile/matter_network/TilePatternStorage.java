package matteroverdrive.common.tile.matter_network;

import javax.annotation.Nullable;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.common.block.type.TypeMachine;
import matteroverdrive.common.inventory.InventoryPatternStorage;
import matteroverdrive.common.item.ItemPatternDrive;
import matteroverdrive.common.item.tools.electric.ItemElectric;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.network.NetworkMatter;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.network.utils.IMatterNetworkMember;
import matteroverdrive.core.tile.types.GenericRedstoneTile;
import matteroverdrive.core.utils.UtilsDirection;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;

public class TilePatternStorage extends GenericRedstoneTile implements IMatterNetworkMember {

	private boolean isPowered = false;
	
	public static final int SLOT_COUNT = 9;
	private static final int ENERGY_STORAGE = 64000;
	public static final int BASE_USAGE = 50;
	public static final int USAGE_PER_DRIVE = 100;
	
	public CapabilityInventory clientNetworkInventory;
	public boolean clientNetworkPowered;
	
	
	public boolean clientTilePowered;
	
	public CapabilityInventory clientInventory;
	public CapabilityEnergyStorage clientEnergy;
	
	
	public TilePatternStorage(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_PATTERN_STORAGE.get(), pos, state);
		addCapability(new CapabilityInventory(SLOT_COUNT, true, true).setInputs(7).setOutputs(1).setEnergySlots(1).setOwner(this)
				.setValidator(getValidator()));
		addCapability(new CapabilityEnergyStorage(ENERGY_STORAGE, true, false).setOwner(this));
		setMenuProvider(
				new SimpleMenuProvider(
						(id, inv, play) -> new InventoryPatternStorage(id, play.getInventory(),
								exposeCapability(CapabilityType.Item), getCoordsData()),
						getContainerName(TypeMachine.PATTERN_STORAGE.id())));
		setTickable();
		setHasMenuData();
		setHasRenderData();
	}
	
	@Override
	public void tickServer() {
		if(canRun()) {
			UtilsTile.drainElectricSlot(this);
			CapabilityInventory inv = exposeCapability(CapabilityType.Item);
			CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
			int drives = 0;
			for(ItemStack stack : inv.getInputs()) {
				if (stack.getItem() instanceof ItemPatternDrive) {
					drives++;
				}
			}
			int usage = BASE_USAGE + drives * USAGE_PER_DRIVE;
			if(energy.getEnergyStored() >= usage) {
				isPowered = true;
				energy.removeEnergy(usage);
			} else {
				isPowered = false;
			}
		} else {
			isPowered = false;
		}
	}
	
	@Override
	public void getMenuData(CompoundTag tag) {
		CompoundTag data = new CompoundTag();
		CapabilityEnergyStorage energy = exposeCapability(CapabilityType.Energy);
		data.put(energy.getSaveKey(), energy.serializeNBT());
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		data.put(inv.getSaveKey(), inv.serializeNBT());
		
		tag.put("data", data);
	}
	
	@Override
	public void readMenuData(CompoundTag tag) {
		CompoundTag data = tag.getCompound("data");
		clientEnergy = new CapabilityEnergyStorage(0, false, false);
		clientEnergy.deserializeNBT(data.getCompound(clientEnergy.getSaveKey()));
		clientInventory = new CapabilityInventory();
		clientInventory.deserializeNBT(data.getCompound(clientInventory.getSaveKey()));
	}
	
	@Override
	public void getRenderData(CompoundTag tag) {
		CompoundTag data = new CompoundTag();
		
		data.putBoolean("isPowered", isPowered);
		
		tag.put("data", data);
	}
	
	@Override
	public void readRenderData(CompoundTag tag) {
		CompoundTag data = tag.getCompound("data");
		clientTilePowered = data.getBoolean("isPowered");
	}

	@Override
	public boolean canConnectToFace(Direction face) {
		Direction relative = UtilsDirection.getRelativeSide(Direction.NORTH, getFacing());
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
		
		CapabilityInventory inv = exposeCapability(CapabilityType.Item);
		data.put(inv.getSaveKey(), inv.serializeNBT());
		data.putBoolean("ispowered", isPowered);
		
		return data;
	}
	
	public void handleNetworkData(CompoundTag tag) {
		CompoundTag data = tag.getCompound("data");
		
		clientNetworkInventory = new CapabilityInventory();
		clientNetworkInventory.deserializeNBT(data.getCompound(clientNetworkInventory.getSaveKey()));
		clientNetworkPowered = data.getBoolean("ispowered");
	}

	@Override
	public int getMaxMode() {
		return 2;
	}
	
	private static TriPredicate<Integer, ItemStack, CapabilityInventory> getValidator() {
		return (index, stack, cap) -> index < 6 && stack.getItem() instanceof ItemPatternDrive
				|| index == 6 && stack.getItem() instanceof ItemMatterScanner 
				|| index == 8 && stack.getItem() instanceof ItemElectric;
	}

}
