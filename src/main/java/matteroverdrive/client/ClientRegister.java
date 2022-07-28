package matteroverdrive.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.client.particle.replicator.ParticleReplicator;
import matteroverdrive.client.particle.shockwave.ParticleShockwave;
import matteroverdrive.client.particle.vent.ParticleVent;
import matteroverdrive.client.render.tile.RendererCharger;
import matteroverdrive.client.render.tile.RendererInscriber;
import matteroverdrive.client.render.tile.RendererMatterReplicator;
import matteroverdrive.client.render.tile.RendererPatternMonitor;
import matteroverdrive.client.screen.ScreenCharger;
import matteroverdrive.client.screen.ScreenInscriber;
import matteroverdrive.client.screen.ScreenMatterAnalyzer;
import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.client.screen.ScreenMatterRecycler;
import matteroverdrive.client.screen.ScreenMatterReplicator;
import matteroverdrive.client.screen.ScreenMicrowave;
import matteroverdrive.client.screen.ScreenPatternMonitor;
import matteroverdrive.client.screen.ScreenPatternStorage;
import matteroverdrive.client.screen.ScreenChunkloader;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.client.screen.ScreenSpacetimeAccelerator;
import matteroverdrive.client.screen.ScreenTransporter;
import matteroverdrive.client.screen.ScreenTritaniumCrate;
import matteroverdrive.common.item.tools.ItemMatterContainer.ContainerType;
import matteroverdrive.common.item.tools.electric.ItemBattery.BatteryType;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientRegister {

	private static final ResourceLocation CHARGE = new ResourceLocation("charge");

	/* MODELS */

	public static final ResourceLocation MODEL_CHARGER = blockModel("charger_renderer");
	public static final ResourceLocation MODEL_MATTER_REPLICATOR_INTERIOR = blockModel("matter_replicator_interior");

	/* TEXTURES */
	
	public static final HashMap<ResourceLocation, TextureAtlasSprite> CACHED_TEXTUREATLASSPRITES = new HashMap<>();
	private static final List<ResourceLocation> CUSTOM_BLOCK_TEXTURES = new ArrayList<>();
	
	private static final String CUSTOM_LOC = References.ID + ":atlas/";
	
	public static final ResourceLocation TEXTURE_HOLO_GRID = new ResourceLocation(CUSTOM_LOC + "holo_grid");
	//rotating matricies is a pain in the ass fight me
	public static final ResourceLocation TEXTURE_HOLO_PATTERN_MONITOR = new ResourceLocation(CUSTOM_LOC + "pattern_monitor_holo");
	public static final ResourceLocation TEXTURE_HOLO_PATTERN_MONITOR_90 = new ResourceLocation(CUSTOM_LOC + "pattern_monitor_holo_90");
	public static final ResourceLocation TEXTURE_HOLO_PATTERN_MONITOR_180 = new ResourceLocation(CUSTOM_LOC + "pattern_monitor_holo_180");
	public static final ResourceLocation TEXTURE_HOLO_PATTERN_MONITOR_270 = new ResourceLocation(CUSTOM_LOC + "pattern_monitor_holo_270");
	public static final ResourceLocation TEXTURE_SPINNER = new ResourceLocation(CUSTOM_LOC + "spinner");
	public static final ResourceLocation TEXTURE_HOLO_GLOW = new ResourceLocation(CUSTOM_LOC + "holo_monitor_glow");
	public static final ResourceLocation TEXTURE_CONNECTION_ICON = new ResourceLocation(CUSTOM_LOC + "connection_icon");
	
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
		MenuScreens.register(DeferredRegisters.MENU_CHUNKLOADER.get(), ScreenChunkloader::new);
		MenuScreens.register(DeferredRegisters.MENU_PATTERN_STORAGE.get(), ScreenPatternStorage::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_REPLICATOR.get(), ScreenMatterReplicator::new);
		MenuScreens.register(DeferredRegisters.MENU_PATTERN_MONITOR.get(), ScreenPatternMonitor::new);
		MenuScreens.register(DeferredRegisters.MENU_MATTER_ANALYZER.get(), ScreenMatterAnalyzer::new);

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
		ItemProperties.register(DeferredRegisters.ITEM_MATTER_SCANNER.get(), CHARGE, 
				(stack, world, entity, call) -> {
					if(stack.hasTag() && stack.getTag().getBoolean("on")) {
						return 1;
					}
					return 0;
		});
		
		ClientEventHandler.init();

	}

	@SubscribeEvent
	public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {

		event.registerBlockEntityRenderer(DeferredRegisters.TILE_CHARGER.get(), RendererCharger::new);
		event.registerBlockEntityRenderer(DeferredRegisters.TILE_INSCRIBER.get(), RendererInscriber::new);
		event.registerBlockEntityRenderer(DeferredRegisters.TILE_PATTERN_MONITOR.get(), RendererPatternMonitor::new);
		event.registerBlockEntityRenderer(DeferredRegisters.TILE_MATTER_REPLICATOR.get(), RendererMatterReplicator::new);
		
	}

	@SubscribeEvent
	public static void onModelEvent(ModelEvent.RegisterAdditional event) {
		event.register(MODEL_CHARGER);
		event.register(MODEL_MATTER_REPLICATOR_INTERIOR);
	}

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.register(DeferredRegisters.PARTICLE_REPLICATOR.get(), ParticleReplicator.Factory::new);
		event.register(DeferredRegisters.PARTICLE_SHOCKWAVE.get(), ParticleShockwave.Factory::new);
		event.register(DeferredRegisters.PARTICLE_VENT.get(), ParticleVent.Factory::new);
	}

	private static ResourceLocation blockModel(String path) {
		return new ResourceLocation(References.ID + ":block/" + path);
	}
	
	static {
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_HOLO_GRID);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_90);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_180);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_HOLO_PATTERN_MONITOR_270);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_SPINNER);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_HOLO_GLOW);
		CUSTOM_BLOCK_TEXTURES.add(ClientRegister.TEXTURE_CONNECTION_ICON);
	}

	@SubscribeEvent
	public static void addCustomTextureAtlases(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			CUSTOM_BLOCK_TEXTURES.forEach(h -> event.addSprite(h));
		}
	}

	@SubscribeEvent
	public static void cacheCustomTextureAtlases(TextureStitchEvent.Post event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			for (ResourceLocation loc : CUSTOM_BLOCK_TEXTURES) {
				ClientRegister.CACHED_TEXTUREATLASSPRITES.put(loc, event.getAtlas().getSprite(loc));
			}
		}
	}


}
