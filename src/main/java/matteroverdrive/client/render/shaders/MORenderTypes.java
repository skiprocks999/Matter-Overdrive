package matteroverdrive.client.render.shaders;

import java.io.IOException;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import matteroverdrive.References;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = Dist.CLIENT)
public class MORenderTypes extends RenderType {

	/* LOGGER INSTANCE & RANDOM SOURCE*/
	public static final Logger LOGGER = LoggerFactory.getLogger("Matter Overdrive: Shaders");
	private static final RandomSource RANDOM_SOURCE = RandomSource.create();

	/* SHADER INSTANCES */
	private static ShaderInstance shaderGreaterAlpha;
	private static ShaderInstance androidShader;
	private static ShaderInstance renderStationShader;

	/* UNIFORMS */
	private static Uniform uniformAlphaCutoff;

	/* TEXTURE RESOURCE LOCS */
	private static final ResourceLocation ANDROID_TEXTURE = new ResourceLocation(References.ID, "textures/entity/android_colorless.png");
	private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(References.ID,"textures/fx/hologram_beam.png");

	/* SHADER RESOURCE LOCS */
	private static final ResourceLocation BASE_STATION_LOC = new ResourceLocation(References.ID, "base_station");
	private static final ResourceLocation ANDROID_STATION_LOC = new ResourceLocation(References.ID, "android_station");
	private static final ResourceLocation GREATER_ALPHA_LOC = new ResourceLocation(References.ID, "greater_alpha");

	/* SHADER STATE SHARDS */

	private static final ShaderStateShard RENDERTYPE_GREATER_ALPHA = new ShaderStateShard(() -> shaderGreaterAlpha);

	/* RENDER TYPES */
	public static final RenderType BASE_STATION = RenderType.create(BASE_STATION_LOC.toString(),
					DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
					RenderType.CompositeState.builder()
									.setTextureState(new RenderStateShard.TextureStateShard(GLOW_TEXTURE, false, false))
									.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
										RenderSystem.enableBlend();
										RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.SRC_ALPHA);
									}, () -> {
										RenderSystem.disableBlend();
										RenderSystem.defaultBlendFunc();
									}))
									.setCullState(new RenderStateShard.CullStateShard(false))
									.setLightmapState(new RenderStateShard.LightmapStateShard(true))
									.setShaderState(new RenderStateShard.ShaderStateShard(MORenderTypes::getRenderStationShader))
									.setOverlayState(new RenderStateShard.OverlayStateShard(true))
									.createCompositeState(false));

	public static final RenderType ANDROID_STATION = RenderType.create(ANDROID_STATION_LOC.toString(),
					DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					RenderType.CompositeState.builder()
									.setTextureState(new RenderStateShard.TextureStateShard(ANDROID_TEXTURE, false, false))
									.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
										RenderSystem.enableBlend();
										RenderSystem.blendFuncSeparate(
														GlStateManager.SourceFactor.SRC_ALPHA,
														GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
														GlStateManager.SourceFactor.ONE,
														GlStateManager.DestFactor.ZERO
										);
									}, () -> {
										RenderSystem.disableBlend();
										RenderSystem.defaultBlendFunc();
									}))
									.setCullState(new RenderStateShard.CullStateShard(false))
									.setLightmapState(new RenderStateShard.LightmapStateShard(true))
									.setOverlayState(new RenderStateShard.OverlayStateShard(true))
									.setShaderState(new RenderStateShard.ShaderStateShard(MORenderTypes::getAndroidShader))
									.createCompositeState(false));

	public static final RenderType GREATER_ALPHA = RenderType.create(GREATER_ALPHA_LOC.toString(),
			DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false,
			CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_GREATER_ALPHA)
					.setTextureState(BLOCK_SHEET).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.createCompositeState(false));

	@SubscribeEvent
	public static void onRegisterShaders(final RegisterShadersEvent event) {
		try {
			event.registerShader(new ShaderInstance(
							event.getResourceManager(),
							new ResourceLocation(References.ID, "android_station_shader"),
							DefaultVertexFormat.NEW_ENTITY),
							shader -> { androidShader = shader;}
			);
			event.registerShader(new ShaderInstance(
							event.getResourceManager(),
							new ResourceLocation(References.ID, "render_station_shader"),
							DefaultVertexFormat.POSITION_TEX_COLOR),
							shader -> { renderStationShader = shader;}
			);
			event.registerShader(new ShaderInstance(
							event.getResourceManager(),
							GREATER_ALPHA_LOC,
							DefaultVertexFormat.POSITION_COLOR_TEX),
							shader -> { shaderGreaterAlpha = shader; uniformAlphaCutoff = shader.getUniform("AlphaCutoff");}
			);
		} catch (IOException err) {
			LOGGER.warn(err.getMessage());
		}
	}



	public static RenderType getRenderTypeAlphaCutoff(float cutoff) {
		setRenderTypeAlphaCutoff(cutoff);
		return GREATER_ALPHA;
	}

	public static void setRenderTypeAlphaCutoff(float cutoff) {
		uniformAlphaCutoff.set(Math.min(cutoff, 1.0F));
	}

	public static ShaderInstance getAndroidShader() {
		if (androidShader == null) throw new IllegalArgumentException("Tried getting Android Shader before it was compiled");
		return androidShader;
	}

	public static ShaderInstance getRenderStationShader() {
		if (renderStationShader == null) throw new IllegalArgumentException("Tried getting RenderStationShader before it was compiled");
		return renderStationShader;
	}

	public static ShaderInstance getShaderGreaterAlpha() {
		if (shaderGreaterAlpha == null) throw new IllegalArgumentException("Tried getting Greater Alpha shader before it was compiled");
		return shaderGreaterAlpha;
	}

	private MORenderTypes(String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling,
			boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
		throw new UnsupportedOperationException();
	}

}
