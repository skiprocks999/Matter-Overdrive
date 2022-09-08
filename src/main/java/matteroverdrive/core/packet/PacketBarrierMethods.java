package matteroverdrive.core.packet;

import java.util.HashMap;
import java.util.UUID;

import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.capability.types.entity_data.ICapabilityEntityData;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.sound.item.TickableSoundMatterScanner;
import matteroverdrive.core.tile.GenericTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class PacketBarrierMethods {

	public static void handlePacketClientMatterValues(HashMap<Item, Double> values) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null && minecraft.player != null) {
			MatterRegister.INSTANCE.setClientValues(values);
		}
	}

	public static void handlePacketClientMNData(CompoundTag data, BlockPos monitorPos) {
		Level world = Minecraft.getInstance().level;
		if (world != null) {
			BlockEntity tile = world.getBlockEntity(monitorPos);
			if (tile != null && tile instanceof TilePatternMonitor monitor) {
				monitor.handleNetworkData(data, world);
			}
		}
	}

	public static void handlePacketPlayMatterScannerSound(UUID id, InteractionHand hand) {
		Minecraft minecraft = Minecraft.getInstance();
		Level world = minecraft.level;
		if (world != null) {
			Player player = minecraft.player;
			if (player.getUUID().equals(id) && player.getItemInHand(hand).getItem() instanceof ItemMatterScanner) {
				minecraft.getSoundManager().play(new TickableSoundMatterScanner(hand, id));
			}
		}
	}

	public static void handlePacketSyncClientEntityCapability(UUID id, CapabilityEntityData clientCapability) {
		Minecraft minecraft = Minecraft.getInstance();
		Level world = minecraft.level;
		if (world != null) {
			Player player = minecraft.player;
			if (player.getUUID().equals(id)
					&& player.getCapability(MatterOverdriveCapabilities.ENTITY_DATA).isPresent()) {
				LazyOptional<ICapabilityEntityData> lazy = player.getCapability(MatterOverdriveCapabilities.ENTITY_DATA)
						.cast();
				CapabilityEntityData cap = (CapabilityEntityData) lazy.resolve().get();
				cap.copyFromOther(clientCapability);
			}
		}
	}

	public static void handlePacketUpdateTile(CompoundTag data, boolean isGui, BlockPos pos) {
		/*
		ClientLevel world = Minecraft.getInstance().level;
		if (world != null) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof GenericTile generic) {
				if (isGui && generic.hasMenuData) {
					generic.readMenuData(data);
				} else if (!isGui && generic.hasRenderData) {
					generic.readRenderData(data);
				}
			}
		}
		*/
	}

}
