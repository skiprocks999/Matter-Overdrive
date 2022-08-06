package matteroverdrive.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import matteroverdrive.client.keys.handlers.KeyHandlerMatterScanner;
import matteroverdrive.client.render.rllhandler.RLLHandlerMatterScanner;
import matteroverdrive.client.render.tooltip.MatterValueTooltipHandler;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.eventhandler.client.AbstractKeyPressHandler;
import matteroverdrive.core.eventhandler.client.AbstractRenderLevelLastHandler;
import matteroverdrive.core.eventhandler.client.AbstractTooltipHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

	private static final List<AbstractRenderLevelLastHandler> RLL_HANDLERS = new ArrayList<>();

	private static final List<AbstractKeyPressHandler> KEY_PRESS_HANDLERS = new ArrayList<>();

	private static final List<AbstractTooltipHandler> TOOLTIP_HANDLERS = new ArrayList<>();

	protected static void init() {
		RLL_HANDLERS.add(new RLLHandlerMatterScanner());

		KEY_PRESS_HANDLERS.add(new KeyHandlerMatterScanner());

		TOOLTIP_HANDLERS.add(new MatterValueTooltipHandler());
	}

	@SubscribeEvent
	public static void handleTooltipEvents(ItemTooltipEvent event) {
		List<Component> tooltips = event.getToolTip();
		ItemStack item = event.getItemStack();
		// Note player can be null
		Player player = event.getEntity();

		for (AbstractTooltipHandler handler : TOOLTIP_HANDLERS) {
			handler.handleTooltips(tooltips, item, player);
		}
	}

	@SubscribeEvent
	public static void handleKeyPressEvents(Key event) {

		Minecraft minecraft = Minecraft.getInstance();
		int key = event.getKey();
		int scanCode = event.getScanCode();
		int action = event.getAction();

		for (AbstractKeyPressHandler handler : KEY_PRESS_HANDLERS) {
			handler.handleKeyPress(minecraft, scanCode, key, action);
		}

	}

	@SubscribeEvent
	public static void handleRenderLevelLastEvents(RenderLevelStageEvent event) {

		Minecraft minecraft = Minecraft.getInstance();
		PoseStack matrix = event.getPoseStack();
		LevelRenderer renderer = event.getLevelRenderer();
		Matrix4f projMatrix = event.getProjectionMatrix();
		float partialTicks = event.getPartialTick();
		int stateNS = event.getRenderTick();

		for (AbstractRenderLevelLastHandler handler : RLL_HANDLERS) {
			handler.handleRendering(minecraft, renderer, matrix, projMatrix, partialTicks, stateNS);
		}

	}

	@SubscribeEvent
	public static void handlerTransporterArrival(MovementInputUpdateEvent event) {
		Entity entity = event.getEntity();
		if (MatterOverdriveConfig.accurate_transporter.get()) {
			entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
				if (h.getTransporterTimer() > 0) {
					Input input = event.getInput();
					input.down = false;
					input.forwardImpulse = 0.0F;
					input.jumping = false;
					input.left = false;
					input.leftImpulse = 0.0F;
					input.right = false;
					input.shiftKeyDown = false;
					input.up = false;
				}
			});
		}
	}

}
