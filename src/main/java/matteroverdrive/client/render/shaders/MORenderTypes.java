package matteroverdrive.client.render.shaders;

import java.io.IOException;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import matteroverdrive.References;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = Dist.CLIENT)
public class MORenderTypes extends RenderType {

	/* SHADER INSTANCES */
	private static ShaderInstance shaderGreaterAlpha;
    
	/* UNIFORMS */
	private static Uniform uniformAlphaCutoff;
    
	/* RESOURCE LOCS */
	
	private static final ResourceLocation GREATER_ALPHA_LOC = new ResourceLocation(References.ID, "greater_alpha");
 
	/* SHADER STATE SHARDS */
	
	private static final ShaderStateShard RENDERTYPE_GREATER_ALPHA = new ShaderStateShard(() -> shaderGreaterAlpha);
    
	/* RENDER TYPES */
	
	public static final RenderType GREATER_ALPHA = RenderType.create(
            GREATER_ALPHA_LOC.toString(),
            DefaultVertexFormat.POSITION_COLOR_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_GREATER_ALPHA)
                    .setTextureState(BLOCK_SHEET)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(false)
    );

    @SubscribeEvent
    public static void onRegisterShaders(final RegisterShadersEvent event) throws IOException
    {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceManager(),
                        GREATER_ALPHA_LOC,
                        DefaultVertexFormat.POSITION_COLOR_TEX
                ),
                shader ->
                {
                    shaderGreaterAlpha = shader;
                    uniformAlphaCutoff = shader.getUniform("AlphaCutoff");
                }
        );
    }
    
    public static RenderType getRenderTypeAlphaCutoff(float cutoff) {
    	setRenderTypeAlphaCutoff(cutoff);
    	return GREATER_ALPHA;
    }
    
    public static void setRenderTypeAlphaCutoff(float cutoff) {
    	uniformAlphaCutoff.set(Math.min(cutoff, 1.0F));
    }
    
    public static ShaderInstance getShaderGreaterAlpha()
    {
        return shaderGreaterAlpha;
    }
	
	private MORenderTypes(String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling,
			boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
		throw new UnsupportedOperationException();
	}

}
