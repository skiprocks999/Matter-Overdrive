package matteroverdrive.core.screen.types;

import java.util.HashSet;
import java.util.function.DoubleSupplier;

import matteroverdrive.core.inventory.GenericInventoryTile;
import matteroverdrive.core.packet.type.serverbound.misc.PacketUpdateCapabilitySides.CapabilityType;
import matteroverdrive.core.screen.component.ScreenComponentCharge;
import matteroverdrive.core.screen.component.ScreenComponentIndicator;
import matteroverdrive.core.screen.component.ScreenComponentProgress;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.screen.component.wrappers.WrapperIOConfig;
import matteroverdrive.core.tile.types.GenericMachineTile;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GenericMachineScreen<T extends GenericInventoryTile<? extends GenericMachineTile>>
		extends GenericOverdriveScreen<T> {

	public GenericMachineScreen(T menu, Inventory playerinventory, Component title, int guiWidth, int guiHeight) {
		super(menu, playerinventory, title, guiWidth, guiHeight);
	}

	public ButtonOverdrive redstoneButton(int x, int y) {
		return new ButtonOverdrive(this, x, y, 58, 20, () -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				switch (machine.getCurrMode()) {
				case 0:
					return UtilsText.gui("redstonelow");
				case 1:
					return UtilsText.gui("redstonehigh");
				case 2:
					return UtilsText.gui("redstonenone");
				default:
					return Component.empty();
				}
			}
			return Component.empty();
		}, button -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				machine.getPropertyManager().updateServerBlockEntity(machine.currRedstoneModeProp,
						machine.getCurrMode() + 1);
			}
		}).setSound(handler -> handler.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F)));
	}

	public ScreenComponentCharge defaultEnergyBar(int x, int y, int[] screenNumbers) {
		return getEnergyBar(x, y, screenNumbers, () -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null && machine.isRunning()) {
				return machine.getCurrentPowerUsage();
			}
			return 0;
		});
	}

	public ScreenComponentCharge getEnergyBar(int x, int y, int[] screenNumbers, DoubleSupplier getUsage) {
		return new ScreenComponentCharge(() -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				return machine.getEnergyStorageCap().getEnergyStored();
			}
			return 0;
		}, () -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				return machine.getEnergyStorageCap().getMaxEnergyStored();
			}
			return 0;
		}, getUsage, this, x, y, screenNumbers);
	}

	public ScreenComponentCharge defaultRecipeMatterBar(int x, int y, int[] screenNumbers) {
		return getMatterBar(x, y, screenNumbers, () -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null && machine.isRunning()) {
				return machine.getRecipeValue();
			}
			return 0;
		});
	}

	public ScreenComponentCharge defaultUsageMatterBar(int x, int y, int[] screenNumbers) {
		return getMatterBar(x, y, screenNumbers, () -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null && machine.isRunning()) {
				return machine.getCurrentMatterUsage();
			}
			return 0;
		});
	}

	public ScreenComponentCharge getMatterBar(int x, int y, int[] screenNumbers, DoubleSupplier getUsage) {
		return new ScreenComponentCharge(() -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				return machine.getMatterStorageCap().getMatterStored();
			}
			return 0;
		}, () -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				return machine.getMatterStorageCap().getMaxMatterStored();
			}
			return 0;
		}, getUsage, this, x, y, screenNumbers).setMatter();
	}

	public ScreenComponentIndicator getRunningIndicator(int x, int y, int[] screenNumbers) {
		return new ScreenComponentIndicator(() -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				return machine.isRunning();
			}
			return false;
		}, this, x, y, screenNumbers);
	}

	public ScreenComponentIndicator getPoweredIndicator(int x, int y, int[] screenNumbers) {
		return new ScreenComponentIndicator(() -> {
			GenericMachineTile machine = getMenu().getTile();
			if (machine != null) {
				return machine.isPowered();
			}
			return false;
		}, this, x, y, screenNumbers);
	}

	public ScreenComponentProgress getProgressArrow(int x, int y, int[] screenNumbers) {
		return new ScreenComponentProgress(() -> {
			GenericMachineTile inscriber = getMenu().getTile();
			if (inscriber != null && inscriber.getProcessingTime() > 0) {
				return inscriber.getProgress() / inscriber.getProcessingTime();
			}
			return 0;
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
