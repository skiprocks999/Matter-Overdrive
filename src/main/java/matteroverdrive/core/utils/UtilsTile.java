package matteroverdrive.core.utils;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashSet;

public class UtilsTile {

	public static void outputEnergy(GenericTile tile) {
		if (tile.hasCapability(ForgeCapabilities.ENERGY)) {
			CapabilityEnergyStorage energy = tile.exposeCapability(ForgeCapabilities.ENERGY);
			Level world = tile.getLevel();
			BlockPos pos = tile.getBlockPos();
			if (energy.canExtract()) {
				if (energy.isSided()) {
					Direction facing = tile.getFacing();
					HashSet<Direction> directions = energy.getOutputDirections();
					if (directions != null) {
						for (Direction direction : directions) {
							Direction relative = UtilsDirection.getRelativeSide(facing, direction);
							BlockEntity entity = world.getBlockEntity(pos.relative(relative));
							if (entity != null && energy.getEnergyStored() > 0) {
								LazyOptional<IEnergyStorage> lazy = entity.getCapability(ForgeCapabilities.ENERGY,
										relative.getOpposite());
								if (lazy.isPresent()) {
									IEnergyStorage storage = lazy.resolve().get();
									if (storage.canReceive()) {
										int accepted = storage.receiveEnergy(energy.getEnergyStored(), true);
										if (accepted > 0) {
											storage.receiveEnergy(accepted, false);
											energy.extractEnergy(accepted, false);
										}
									}
								}
							}
						}
					}
				} else {
					for (Direction dir : Direction.values()) {
						BlockEntity entity = world.getBlockEntity(pos.relative(dir));
						if (entity != null && energy.getEnergyStored() > 0) {
							LazyOptional<IEnergyStorage> lazy = entity.getCapability(ForgeCapabilities.ENERGY,
									dir.getOpposite());
							if (lazy.isPresent()) {
								IEnergyStorage storage = lazy.resolve().get();
								if (storage.canReceive()) {
									int accepted = storage.receiveEnergy(energy.getEnergyStored(), true);
									if (accepted > 0) {
										storage.receiveEnergy(accepted, false);
										energy.extractEnergy(accepted, false);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void drainElectricSlot(GenericTile tile) {
		CapabilityInventory inv = tile.exposeCapability(ForgeCapabilities.ITEM_HANDLER);
		CapabilityEnergyStorage energy = tile.exposeCapability(ForgeCapabilities.ENERGY);
		for (ItemStack stack : inv.getEnergyInputItems()) {
			if (stack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
				IEnergyStorage storage = (IEnergyStorage) stack.getCapability(ForgeCapabilities.ENERGY).cast().resolve()
						.get();
				if (storage.canExtract()) {
					int accepted = energy.receiveEnergy(storage.getEnergyStored(), true);
					energy.receiveEnergy(accepted, false);
					storage.extractEnergy(accepted, false);
				}
			}
		}
	}

	public static void outputMatter(GenericTile tile) {
		if (tile.hasCapability(MatterOverdriveCapabilities.MATTER_STORAGE)) {
			CapabilityMatterStorage matter = tile.exposeCapability(MatterOverdriveCapabilities.MATTER_STORAGE);
			Level world = tile.getLevel();
			BlockPos pos = tile.getBlockPos();
			if (matter.canExtract() && matter.getMatterStored() > 0) {
				if (matter.isSided()) {
					Direction facing = tile.getFacing();
					HashSet<Direction> directions = matter.getOutputDirections();
					if (directions != null) {
						for (Direction direction : directions) {
							Direction relative = UtilsDirection.getRelativeSide(facing, direction);
							BlockEntity entity = world.getBlockEntity(pos.relative(relative));
							if (entity != null && matter.getMatterStored() > 0) {
								LazyOptional<ICapabilityMatterStorage> lazy = entity.getCapability(
										MatterOverdriveCapabilities.MATTER_STORAGE, relative.getOpposite());
								if (lazy.isPresent()) {
									ICapabilityMatterStorage storage = lazy.resolve().get();
									if (storage.canReceive()) {
										matter.extractMatter(storage.receiveMatter(matter.getMatterStored(), false),
												false);
									}
								}
							}
						}
					}
				} else {
					for (Direction dir : Direction.values()) {
						BlockEntity entity = world.getBlockEntity(pos.relative(dir));
						if (entity != null && matter.getMatterStored() > 0) {
							LazyOptional<ICapabilityMatterStorage> lazy = entity
									.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, dir.getOpposite());
							if (lazy.isPresent()) {
								ICapabilityMatterStorage storage = lazy.resolve().get();
								if (storage.canReceive()) {
									matter.extractMatter(storage.receiveMatter(matter.getMatterStored(), false), false);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void drainMatterSlot(GenericTile tile) {
		CapabilityInventory inv = tile.exposeCapability(ForgeCapabilities.ITEM_HANDLER);
		CapabilityMatterStorage energy = tile.exposeCapability(MatterOverdriveCapabilities.MATTER_STORAGE);
		for (ItemStack stack : inv.getMatterInputItems()) {
			if (stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
				ICapabilityMatterStorage storage = (ICapabilityMatterStorage) stack
						.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).cast().resolve().get();
				if (storage.canExtract()) {
					double accepted = energy.receiveMatter(storage.getMatterStored(), true);
					energy.receiveMatter(accepted, false);
					storage.extractMatter(accepted, false);
				}
			}
		}
	}

	public static void fillMatterSlot(GenericTile tile) {
		CapabilityInventory inv = tile.exposeCapability(ForgeCapabilities.ITEM_HANDLER);
		CapabilityMatterStorage matter = tile.exposeCapability(MatterOverdriveCapabilities.MATTER_STORAGE);
		for (ItemStack stack : inv.getMatterOutputItems()) {
			if (stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
				ICapabilityMatterStorage storage = (ICapabilityMatterStorage) stack
						.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).cast().resolve().get();
				if (storage.canExtract()) {
					double accepted = storage.receiveMatter(matter.getMatterStored(), true);
					storage.receiveMatter(accepted, false);
					matter.extractMatter(accepted, false);
				}
			}
		}
	}

	public static boolean isFEReciever(BlockEntity acceptor, Direction dir) {
		if (acceptor != null) {
			if (acceptor.getCapability(ForgeCapabilities.ENERGY, dir).isPresent()) {
				return true;
			}
		}
		return false;
	}

	public static boolean adjacentRedstoneSignal(GenericTile tile) {
		return tile.getLevel().hasNeighborSignal(tile.getBlockPos());
	}

	public static void updateLit(GenericTile tile, Boolean value) {
		Level world = tile.getLevel();
		BlockPos pos = tile.getBlockPos();
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(BlockStateProperties.LIT, value));
	}

}
