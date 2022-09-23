package matteroverdrive.client;

import java.util.HashMap;

import matteroverdrive.References;
import matteroverdrive.client.ClientReferences.AtlasTextures;
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
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.ItemRegistry;
import matteroverdrive.registry.MenuRegistry;
import matteroverdrive.registry.ParticleRegistry;
import matteroverdrive.registry.TileRegistry;
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

	public static final ResourceLocation CHARGE = new ResourceLocation("charge");

	/* MODELS */

	public static final ResourceLocation MODEL_CHARGER = blockModel("charger_renderer");

	/* TEXTURES */

	public static final HashMap<AtlasTextures, TextureAtlasSprite> CACHED_TEXTUREATLASSPRITES = new HashMap<>();

	public static void init() {

		MenuScreens.register(MenuRegistry.MENU_TRITANIUM_CRATE.get(), ScreenTritaniumCrate::new);
		MenuScreens.register(MenuRegistry.MENU_SOLAR_PANEL.get(), ScreenSolarPanel::new);
		MenuScreens.register(MenuRegistry.MENU_MATTER_DECOMPOSER.get(), ScreenMatterDecomposer::new);
		MenuScreens.register(MenuRegistry.MENU_MATTER_RECYCLER.get(), ScreenMatterRecycler::new);
		MenuScreens.register(MenuRegistry.MENU_CHARGER.get(), ScreenCharger::new);
		MenuScreens.register(MenuRegistry.MENU_MICROWAVE.get(), ScreenMicrowave::new);
		MenuScreens.register(MenuRegistry.MENU_INSCRIBER.get(), ScreenInscriber::new);
		MenuScreens.register(MenuRegistry.MENU_TRANSPORTER.get(), ScreenTransporter::new);
		MenuScreens.register(MenuRegistry.MENU_SPACETIME_ACCELERATOR.get(), ScreenSpacetimeAccelerator::new);
		MenuScreens.register(MenuRegistry.MENU_CHUNKLOADER.get(), ScreenChunkloader::new);
		MenuScreens.register(MenuRegistry.MENU_PATTERN_STORAGE.get(), ScreenPatternStorage::new);
		MenuScreens.register(MenuRegistry.MENU_MATTER_REPLICATOR.get(), ScreenMatterReplicator::new);
		MenuScreens.register(MenuRegistry.MENU_PATTERN_MONITOR.get(), ScreenPatternMonitor::new);
		MenuScreens.register(MenuRegistry.MENU_MATTER_ANALYZER.get(), ScreenMatterAnalyzer::new);

		ItemProperties.register(ItemRegistry.ITEM_BATTERIES.get(BatteryType.REGULAR).get(), CHARGE,
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
		ItemProperties.register(ItemRegistry.ITEM_BATTERIES.get(BatteryType.HIGHCAPACITY).get(), CHARGE,
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
		ItemProperties.register(ItemRegistry.ITEM_MATTER_CONTAINERS.get(ContainerType.REGULAR).get(), CHARGE,
				(stack, world, entity, call) -> {
					return stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).map(m -> {
						double chargeRatio = m.getMaxMatterStored() > 0 ? m.getMatterStored() / m.getMaxMatterStored()
								: 0.0;
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
		ItemProperties.register(ItemRegistry.ITEM_TRANSPORTER_FLASHDRIVE.get(), CHARGE,
				(stack, world, entity, call) -> {
					if (stack.hasTag() && stack.getTag().contains(UtilsNbt.BLOCK_POS)) {
						return 1;
					}
					return 0;
				});
		ItemProperties.register(ItemRegistry.ITEM_MATTER_SCANNER.get(), CHARGE, (stack, world, entity, call) -> {
			if (stack.hasTag() && stack.getTag().getBoolean("on")) {
				return 1;
			}
			return 0;
		});

		ClientEventHandler.init();
		UtilsText.init();

	}

	@SubscribeEvent
	public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {

		event.registerBlockEntityRenderer(TileRegistry.TILE_CHARGER.get(), RendererCharger::new);
		event.registerBlockEntityRenderer(TileRegistry.TILE_INSCRIBER.get(), RendererInscriber::new);
		event.registerBlockEntityRenderer(TileRegistry.TILE_PATTERN_MONITOR.get(), RendererPatternMonitor::new);
		event.registerBlockEntityRenderer(TileRegistry.TILE_MATTER_REPLICATOR.get(), RendererMatterReplicator::new);

	}

	@SubscribeEvent
	public static void onModelEvent(ModelEvent.RegisterAdditional event) {
		event.register(MODEL_CHARGER);
	}

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.register(ParticleRegistry.PARTICLE_REPLICATOR.get(), ParticleReplicator.Factory::new);
		event.register(ParticleRegistry.PARTICLE_SHOCKWAVE.get(), ParticleShockwave.Factory::new);
		event.register(ParticleRegistry.PARTICLE_VENT.get(), ParticleVent.Factory::new);
	}

	private static ResourceLocation blockModel(String path) {
		return new ResourceLocation(References.ID + ":block/" + path);
	}

	@SubscribeEvent
	public static void addCustomTextureAtlases(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			for(AtlasTextures atlas : AtlasTextures.values()) {
				event.addSprite(atlas.getTexture());
			}
		}
	}

	@SubscribeEvent
	public static void cacheCustomTextureAtlases(TextureStitchEvent.Post event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			for(AtlasTextures atlas : AtlasTextures.values()) {
				ClientRegister.CACHED_TEXTUREATLASSPRITES.put(atlas, event.getAtlas().getSprite(atlas.getTexture()));
			}
		}
	}

}
