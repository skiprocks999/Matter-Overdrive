package matteroverdrive.core.screen.types;

import java.util.HashSet;
import java.util.function.DoubleSupplier;

import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.packet.type.serverbound.PacketUpdateCapabilitySides.CapabilityType;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.button.ButtonRedstoneMode;
import matteroverdrive.core.screen.component.wrappers.WrapperIOConfig;
import matteroverdrive.core.tile.types.GenericMachineTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericMachineScreen<T extends GenericInventoryTile<? extends GenericMachineTile>> extends GenericOverdriveScreen<T> {

	public GenericMachineScreen(T menu, Inventory playerinventory, Component title) {
		super(menu, playerinventory, title);
	}
	
	public ButtonRedstoneMode redstoneButton(int x, int y) {
		return new ButtonRedstoneMode(this, x, y, button -> {
			GenericMachineTile charger = getMenu().getTile();
			if (charger != null) {
				charger.getPropertyManager().updateServerBlockEntity(charger.currRedstoneModeProp, charger.getCurrMode() + 1);
			}
		}, () -> {
			GenericMachineTile charger = getMenu().getTile();
			if (charger != null) {
				return charger.getCurrMode();
			}
			return 0;
		});
	}
	
	public ScreenComponentCharge defaultEnergyBar(int x, int y, int[] screenNumbers) {
		return getEnergyBar(x, y, screenNumbers, () -> {
			GenericMachineTile supply = getMenu().getTile();
			if (supply != null && supply.isRunning()) {
				return supply.getCurrentPowerUsage();
			}
			return 0;
		});
	}
	
	public ScreenComponentCharge getEnergyBar(int x, int y, int[] screenNumbers, DoubleSupplier getUsage) {
		return new ScreenComponentCharge(() -> {
			GenericMachineTile charger = getMenu().getTile();
			if (charger != null) {
				return charger.getEnergyStorageCap().getEnergyStored();
			}
			return 0;
		}, () -> {
			GenericMachineTile charger = getMenu().getTile();
			if (charger != null) {
				return charger.getEnergyStorageCap().getMaxEnergyStored();
			}
			return 0;
		}, getUsage, this, x, y, screenNumbers);
	}
	
	public ScreenComponentCharge defaultRecipeMatterBar(int x, int y, int[] screenNumbers) {
		return getMatterBar(x, y, screenNumbers, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null && matter.isRunning()) {
				return matter.getRecipeValue();
			}
			return 0;
		});
	}
	
	public ScreenComponentCharge defaultUsageMatterBar(int x, int y, int[] screenNumbers) {
		return getMatterBar(x, y, screenNumbers, () -> {
			GenericMachineTile spacetime = getMenu().getTile();
			if (spacetime != null && spacetime.isRunning()) {
				return spacetime.getCurrentMatterUsage();
			}
			return 0;
		});
	}
	
	public ScreenComponentCharge getMatterBar(int x, int y, int[] screenNumbers, DoubleSupplier getUsage) {
		return new ScreenComponentCharge(() -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getMatterStored();
			}
			return 0;
		}, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getMaxMatterStored();
			}
			return 0;
		}, getUsage, this, x, y, screenNumbers).setMatter();
	}
	
	public ScreenComponentIndicator getRunningIndicator(int x, int y, int[] screenNumbers) {
		return new ScreenComponentIndicator(() -> {
			GenericMachineTile charger = getMenu().getTile();
			if (charger != null) {
				return charger.isRunning();
			}
			return false;
		}, this, x, y, screenNumbers);
	}
	
	public ScreenComponentIndicator getPoweredIndicator(int x, int y, int[] screenNumbers) {
		return new ScreenComponentIndicator(() -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.isPowered();
			}
			return false;
		}, this, x, y, screenNumbers);
	}
	
	public WrapperIOConfig getItemIOWrapper(int x, int y) {
		return new WrapperIOConfig(this, x, y, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getInventoryCap().getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getInventoryCap().getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getInventoryCap().hasInput;
			}
			return false;
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getInventoryCap().hasOutput;
			}
			return false;
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.ITEM);
	}
	
	public WrapperIOConfig getEnergyIOWrapper(int x, int y) {
		return new WrapperIOConfig(this, x, y, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getEnergyStorageCap().getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getEnergyStorageCap().getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getEnergyStorageCap().canReceive();
			}
			return false;
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getEnergyStorageCap().canExtract();
			}
			return false;
		}, () -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null) {
				return inscriber.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.ENERGY);
	}
	
	public WrapperIOConfig getMatterIOWrapper(int x, int y) {
		return new WrapperIOConfig(this, 137, 59, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getInputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().getOutputDirections();
			}
			return new HashSet<Direction>();
		}, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().canReceive();
			}
			return false;
		}, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getMatterStorageCap().canExtract();
			}
			return false;
		}, () -> {
			GenericMachineTile matter = getMenu().getTile();
			if (matter != null) {
				return matter.getBlockPos();
			}
			return new BlockPos(0, -100, 0);
		}, CapabilityType.MATTER);
	}

}
