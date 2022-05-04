package matteroverdrive.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.client.screen.ScreenMatterRecycler;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.client.screen.ScreenTritaniumCrate;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;

public class ClientRegister {

	private static final ResourceLocation CHARGE = new ResourceLocation("charge");

	public static void init() {

		MenuScreens.register(DeferredRegisters.MENU_TRITANIUM_CRATE.get(), ScreenTritaniumCrate::new);
		MenuScreens.register(DeferredRegisters.MENU_SOLAR_PANEL.get(), ScreenSolarPanel::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_DECOMPOSER.get(), ScreenMatterDecomposer::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_RECYCLER.get(), ScreenMatterRecycler::new);

		ItemProperties.register(DeferredRegisters.ITEM_BATTERIES.get(BatteryType.REGULAR).get(), CHARGE,
				(stack, world, entity, call) -> {
					return stack.getCapability(CapabilityEnergy.ENERGY).map(m -> {
						double chargeRatio = m.getMaxEnergyStored() > 0
								? (double) m.getEnergyStored() / (double) m.getMaxEnergyStored()
								: 0.0;
						if (chargeRatio >= 0.8) {
							return 5;
						} else if (chargeRatio >= 0.6) {
							return 4;
						} else if (chargeRatio >= 0.4) {
							return 3;
						} else if (chargeRatio >= 0.2) {
							return 2;
						} else if (chargeRatio > 0) {
							return 1;
						}
						return 0;
					}).orElse(0);
				});
		ItemProperties.register(DeferredRegisters.ITEM_BATTERIES.get(BatteryType.HIGHCAPACITY).get(), CHARGE,
				(stack, world, entity, call) -> {
					return stack.getCapability(CapabilityEnergy.ENERGY).map(m -> {
						double chargeRatio = m.getMaxEnergyStored() > 0
								? (double) m.getEnergyStored() / (double) m.getMaxEnergyStored()
								: 0.0;
						if (chargeRatio >= 0.8) {
							return 5;
						} else if (chargeRatio >= 0.6) {
							return 4;
						} else if (chargeRatio >= 0.4) {
							return 3;
						} else if (chargeRatio >= 0.2) {
							return 2;
						} else if (chargeRatio > 0) {
							return 1;
						}
						return 0;
					}).orElse(0);
				});
		ItemProperties.register(DeferredRegisters.ITEM_MATTER_CONTAINER.get(), CHARGE, (stack, world, entity, call) -> {
			return stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).map(m -> {
				double chargeRatio = m.getMaxMatterStored() > 0 ? m.getMatterStored() / m.getMaxMatterStored() : 0.0;
				if (chargeRatio >= 0.875) {
					return 8;
				} else if (chargeRatio >= 0.75) {
					return 7;
				} else if (chargeRatio >= 0.625) {
					return 6;
				} else if (chargeRatio >= 0.5) {
					return 5;
				} else if (chargeRatio > 0.375) {
					return 4;
				} else if (chargeRatio >= 0.25) {
					return 3;
				} else if (chargeRatio >= 0.125) {
					return 2;
				} else if (chargeRatio > 0) {
					return 1;
				}
				return 0;
			}).orElse(0);
		});

	}

}
