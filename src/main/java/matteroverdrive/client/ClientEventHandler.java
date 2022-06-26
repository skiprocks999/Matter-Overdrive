package matteroverdrive.client;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.config.MatterOverdriveConfig;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.PacketToggleMatterScanner;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
	
	private static final float[] HOLO_DEFAULT = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
	private static final float[] HOLO_RED = UtilsRendering.getColorArray(UtilsRendering.HOLO_RED);
	private static final float[] HOLO_GREEN = UtilsRendering.getColorArray(UtilsRendering.HOLO_GREEN);
	
	@SubscribeEvent
	public static void matterTooltipApplier(ItemTooltipEvent event) {
		if (Screen.hasShiftDown()) {
			event.getToolTip().add(getMatterTooltip(event.getItemStack()));
		}
	}

	private static Component getMatterTooltip(ItemStack stack) {
		if (UtilsMatter.isRawDust(stack)) {
			double val = UtilsNbt.readMatterVal(stack);
			return val > 0
					? UtilsText.tooltip("potmatterval",
							new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.LIGHT_PURPLE))
							.withStyle(ChatFormatting.BLUE)
					: UtilsText.tooltip("potmatterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
							.withStyle(ChatFormatting.BLUE);
		} else if (UtilsMatter.isRefinedDust(stack)) {
			double val = UtilsNbt.readMatterVal(stack);
			return val > 0
					? UtilsText
							.tooltip("matterval",
									new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.GOLD))
							.withStyle(ChatFormatting.BLUE)
					: UtilsText.tooltip("matterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
							.withStyle(ChatFormatting.BLUE);
		} else {
			Double val = MatterRegister.INSTANCE.getClientMatterValue(stack);
			return val == null
					? UtilsText.tooltip("matterval", UtilsText.tooltip("nomatter").withStyle(ChatFormatting.RED))
							.withStyle(ChatFormatting.BLUE)
					: UtilsText
							.tooltip("matterval",
									new TextComponent(UtilsText.formatMatterValue(val)).withStyle(ChatFormatting.GOLD))
							.withStyle(ChatFormatting.BLUE);
		}

	}
	
	@SubscribeEvent
	public static void handlerTransporterArrival(MovementInputUpdateEvent event) {
		Entity entity = event.getEntity();
		if(MatterOverdriveConfig.accurate_transporter.get()) {
			entity.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).ifPresent(h -> {
				if(h.getTransporterTimer() > 0) {
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
	
	@SubscribeEvent
	public static void keyPressEvents(KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		
		if (KeyBinds.toggleMatterScanner.matches(event.getKey(), event.getScanCode()) && KeyBinds.toggleMatterScanner.isDown()) {
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner) {
				handleScannerToggle(stack, InteractionHand.MAIN_HAND, player.getUUID());
			} else {
				stack = player.getItemInHand(InteractionHand.OFF_HAND);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemMatterScanner) {
					handleScannerToggle(stack, InteractionHand.OFF_HAND, player.getUUID());
				}
			}
			
		}
	}

	private static void handleScannerToggle(ItemStack stack, InteractionHand hand, UUID uuid) {
		CapabilityEnergyStorage storage = UtilsItem.getEnergyStorageCap(stack);
		if(storage != null && storage.getEnergyStored() > 0) {
			NetworkHandler.CHANNEL.sendToServer(new PacketToggleMatterScanner(uuid, hand));
		}
	}
	
	@SubscribeEvent
	public static void renderMatterScannerOverlay(RenderLevelLastEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		PoseStack matrix = event.getPoseStack();
		MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
		VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());
		
		Player player = minecraft.player;
		BlockHitResult trace = Item.getPlayerPOVHitResult(player.level, player, net.minecraft.world.level.ClipContext.Fluid.ANY);
		boolean[] scannerStatus = scannerHeldOnUse(player);
		if(trace.getType() != Type.MISS && trace.getType() != Type.ENTITY && scannerStatus[0] && scannerStatus[1]) {
			matrix.pushPose();
			BlockPos pos = trace.getBlockPos();
			
			matrix.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			
			rotateMatrixForScanner(matrix, player.getDirection(), trace.getDirection());
			
			matrix.translate(-0.5, -0.5, -0.5);
			
			TextureAtlasSprite holoGrid = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_HOLO_GRID);
			float[] holo_uv = {holoGrid.getU0(), holoGrid.getU1(), holoGrid.getV0(), holoGrid.getV1()};
			float[] holo_color = UtilsRendering.getColorArray(UtilsRendering.TEXT_BLUE);
			holo_color[3] = 0.2F;
			
			TextureAtlasSprite spinner = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_SPINNER);
			float[] spinner_uv = {spinner.getU0(), spinner.getU1(), spinner.getV0(), spinner.getV1()};
			float[] spinner_color = getSpinnerColor(player, scannerStatus[2]);
		
			AABB box = new AABB(0, 0, 0, 1, 1, 1).inflate(0.05);
		
			Matrix4f matrix4f = matrix.last().pose();
			Matrix3f matrix3f = matrix.last().normal();
			
			float[] coords = UtilsRendering.getCoordsFromAABB(box);
			
			AABB spinnerBox;
			
			switch(trace.getDirection()) {
			case DOWN:
				UtilsRendering.renderBottomOfBox(builder, coords, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				spinnerBox = new AABB(0.35, - 0.01, 0.35, 0.65, 0, 0.65);
				UtilsRendering.renderBottomOfBox(builder, UtilsRendering.getCoordsFromAABB(spinnerBox), spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case UP:
				UtilsRendering.renderTopOfBox(builder, coords, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				spinnerBox = new AABB(0.35, 1, 0.35, 0.65,1.01, 0.65);
				UtilsRendering.renderTopOfBox(builder, UtilsRendering.getCoordsFromAABB(spinnerBox), spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case EAST:
				UtilsRendering.renderEastOfBox(builder, coords, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				spinnerBox = new AABB(1.0, 0.35, 0.35, 1.01, 0.65, 0.65);
				UtilsRendering.renderEastOfBox(builder, UtilsRendering.getCoordsFromAABB(spinnerBox), spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case WEST:
				UtilsRendering.renderWestOfBox(builder, coords, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				spinnerBox = new AABB(-0.01, 0.35, 0.35, 0, 0.65, 0.65);
				UtilsRendering.renderWestOfBox(builder, UtilsRendering.getCoordsFromAABB(spinnerBox), spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case NORTH:
				UtilsRendering.renderNorthOfBox(builder, coords, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				spinnerBox = new AABB(0.35, 0.35, -0.01, 0.65, 0.65, 0);
				UtilsRendering.renderNorthOfBox(builder, UtilsRendering.getCoordsFromAABB(spinnerBox), spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			case SOUTH:
				UtilsRendering.renderSouthOfBox(builder, coords, holo_color, holo_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				spinnerBox = new AABB(0.35, 0.35, 1.0, 0.65, 0.65, 1.01);
				UtilsRendering.renderSouthOfBox(builder, UtilsRendering.getCoordsFromAABB(spinnerBox), spinner_color, spinner_uv, matrix4f, matrix3f, 255, OverlayTexture.NO_OVERLAY);
				break;
			}
			matrix.popPose();
		}
		
		
	}
	
	private static boolean[] scannerHeldOnUse(Player player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean held = false;
		boolean on = false;
		boolean inUse = false;
		if(stack.getItem() instanceof ItemMatterScanner scanner) {
			held = true;
			on = scanner.isOn(stack);
			inUse = player.isUsingItem() && player.getUseItem().getItem() instanceof ItemMatterScanner;
		} else {
			stack = player.getItemInHand(InteractionHand.OFF_HAND);
			if(stack.getItem() instanceof ItemMatterScanner scanner) {
				held = true;
				on = scanner.isOn(stack);
				inUse = player.isUsingItem() && player.getUseItem().getItem() instanceof ItemMatterScanner;
			}
		}
		return new boolean[] {held, on, inUse};
	}
	
	private static void rotateMatrixForScanner(PoseStack matrix, Direction playerDir, Direction traceDir) {
		if(traceDir == Direction.NORTH || traceDir == Direction.SOUTH) {
			switch(playerDir) {
			case SOUTH:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 180.0F, true));
				break;
			case EAST:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 90.0F, true));
				break;
			case WEST:
				matrix.mulPose(new Quaternion(new Vector3f(0, 1, 0), 270.0F, true));
				break;
			default:
				break;
			}
		}
		
	}
	
	private static float[] getSpinnerColor(Player player, boolean isInUse) {
		if(isInUse) {
			int count = player.getUseItemRemainingTicks();
			int maxCount = player.getUseItem().getUseDuration();

			float ratio = ((float) count / (float) maxCount);
			float iRatio = 1.0F - ratio;
			
			return new float[] { HOLO_RED[0] * ratio + HOLO_GREEN[0] * iRatio, HOLO_RED[1] * ratio + HOLO_GREEN[1] * iRatio,
					HOLO_RED[2] * ratio + HOLO_GREEN[2] * iRatio, HOLO_RED[3] * ratio + HOLO_GREEN[3] * iRatio };

		} else {
			return HOLO_DEFAULT;
		}
	}

}
