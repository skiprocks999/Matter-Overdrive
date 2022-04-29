package matteroverdrive.core.utils;

import java.util.HashSet;

import matteroverdrive.common.block.BlockLightableMachine;
import matteroverdrive.core.capability.types.CapabilityType;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class UtilsTile {

	public static void outputEnergy(GenericTile tile) {
		if (tile.hasCapability(CapabilityType.Energy)) {
			CapabilityEnergyStorage energy = tile.exposeCapability(CapabilityType.Energy);
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
								LazyOptional<IEnergyStorage> lazy = entity.getCapability(CapabilityEnergy.ENERGY,
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
							LazyOptional<IEnergyStorage> lazy = entity.getCapability(CapabilityEnergy.ENERGY,
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

	public static boolean adjacentRedstoneSignal(GenericTile tile) {
		return tile.getLevel().hasNeighborSignal(tile.getBlockPos());
	}

	public static void updateLit(GenericTile tile, Boolean value) {
		Level world = tile.getLevel();
		BlockPos pos = tile.getBlockPos();
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(BlockLightableMachine.LIT, value));
	}

}
