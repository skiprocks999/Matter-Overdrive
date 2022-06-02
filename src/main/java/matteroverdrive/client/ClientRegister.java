package matteroverdrive.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.client.particle.replicator.ParticleReplicator;
import matteroverdrive.client.particle.shockwave.ParticleShockwave;
import matteroverdrive.client.renderer.tile.RendererCharger;
import matteroverdrive.client.renderer.tile.RendererInscriber;
import matteroverdrive.client.screen.ScreenCharger;
import matteroverdrive.client.screen.ScreenInscriber;
import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.client.screen.ScreenMatterRecycler;
import matteroverdrive.client.screen.ScreenMicrowave;
import matteroverdrive.client.screen.ScreenNetworkPowerSupply;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.client.screen.ScreenSpacetimeAccelerator;
import matteroverdrive.client.screen.ScreenTransporter;
import matteroverdrive.client.screen.ScreenTritaniumCrate;
import matteroverdrive.common.item.tools.ItemMatterContainer.ContainerType;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientRegister {

	private static final ResourceLocation CHARGE = new ResourceLocation("charge");

	/* MODELS */

	public static final ResourceLocation MODEL_CHARGER = blockModel("charger_renderer");

	public static void init() {

		MenuScreens.register(DeferredRegisters.MENU_TRITANIUM_CRATE.get(), ScreenTritaniumCrate::new);
		MenuScreens.register(DeferredRegisters.MENU_SOLAR_PANEL.get(), ScreenSolarPanel::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_DECOMPOSER.get(), ScreenMatterDecomposer::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_RECYCLER.get(), ScreenMatterRecycler::new);
		MenuScreens.register(DeferredRegisters.MENU_CHARGER.get(), ScreenCharger::new);
		MenuScreens.register(DeferredRegisters.MENU_MICROWAVE.get(), ScreenMicrowave::new);
		MenuScreens.register(DeferredRegisters.MENU_INSCRIBER.get(), ScreenInscriber::new);
		MenuScreens.register(DeferredRegisters.MENU_TRANSPORTER.get(), ScreenTransporter::new);
		MenuScreens.register(DeferredRegisters.MENU_SPACETIME_ACCELERATOR.get(), ScreenSpacetimeAccelerator::new);
		MenuScreens.register(DeferredRegisters.MENU_NETWORK_POWER_SUPPLY.get(), ScreenNetworkPowerSupply::new);

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
		ItemProperties.register(DeferredRegisters.ITEM_MATTER_CONTAINERS.get(ContainerType.REGULAR).get(), CHARGE, (stack, world, entity, call) -> {
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
		ItemProperties.register(DeferredRegisters.ITEM_TRANSPORTER_FLASHDRIVE.get(), CHARGE,
				(stack, world, entity, call) -> {
					if (stack.hasTag() && stack.getTag().contains(UtilsNbt.BLOCK_POS)) {
						return 1;
					}
					return 0;
				});

	}

	@SubscribeEvent
	public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {

		event.registerBlockEntityRenderer(DeferredRegisters.TILE_CHARGER.get(), RendererCharger::new);
		event.registerBlockEntityRenderer(DeferredRegisters.TILE_INSCRIBER.get(), RendererInscriber::new);

	}

	@SubscribeEvent
	public static void onModelEvent(ModelRegistryEvent event) {
		ForgeModelBakery.addSpecialModel(MODEL_CHARGER);
	}

	@SubscribeEvent
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		ParticleEngine engine = Minecraft.getInstance().particleEngine;
		engine.register(DeferredRegisters.PARTICLE_REPLICATOR.get(), ParticleReplicator.Factory::new);
		engine.register(DeferredRegisters.PARTICLE_SHOCKWAVE.get(), ParticleShockwave.Factory::new);
	}

	private static ResourceLocation blockModel(String path) {
		return new ResourceLocation(References.ID + ":block/" + path);
	}

}
