package matteroverdrive.core.packet.type.clientbound;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.common.tile.matter_network.TilePatternMonitor;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.android.AndroidEnergy;
import matteroverdrive.core.capability.types.entity_data.CapabilityEntityData;
import matteroverdrive.core.capability.types.entity_data.ICapabilityEntityData;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.property.IPropertyManaged;
import matteroverdrive.core.property.PropertyManager;
import matteroverdrive.core.property.PropertyType;
import matteroverdrive.core.sound.item.TickableSoundMatterScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

	public static void handlePacketAndroidEnergySync(int energy, int maxEnergy) {
		Minecraft.getInstance().player.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
			if (energyStorage instanceof AndroidEnergy) {
				((AndroidEnergy) energyStorage).setEnergy(energy);
			}
		});
	}

	public static void handlePacketAndroidSyncAll(CompoundTag tag) {
		Minecraft.getInstance().player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA)
				.ifPresent(iAndroid -> iAndroid.deserializeNBT(tag));
	}

	public static void handlePacketAndroidTurningTimeSync(int time) {
		Minecraft.getInstance().player.getCapability(MatterOverdriveCapabilities.ANDROID_DATA)
				.ifPresent(iAndroid -> iAndroid.setTurningTime(time));
	}
	
	public static void handlePacketUpdateClientContainerProperties(List<Triple<PropertyType<?>, Short, Object>> updates, short windowId) {
		LocalPlayer playerEntity = Minecraft.getInstance().player;
		if (playerEntity != null) {
			AbstractContainerMenu container = playerEntity.containerMenu;
			if (container.containerId == windowId) {
				if (container instanceof IPropertyManaged managed) {
					PropertyManager propertyManager = managed.getPropertyManager();
					for (Triple<PropertyType<?>, Short, Object> update : updates) {
						propertyManager.update(update.getLeft(), update.getMiddle(), update.getRight());
					}
				} else {
					MatterOverdrive.LOGGER.info("Container is not instance of IPropertyManaged");
				}
			}
		}
	}
	
	public static void handlePacketUpdateClientEntityProperties(List<Triple<PropertyType<?>, Short, Object>> updates, int entityId) {
		LocalPlayer playerEntity = Minecraft.getInstance().player;
		if (playerEntity != null) {
			Entity entity = playerEntity.level.getEntity(entityId);
			if (entity instanceof IPropertyManaged managed) {
				PropertyManager propertyManager = managed.getPropertyManager();
				for (Triple<PropertyType<?>, Short, Object> update : updates) {
					propertyManager.update(update.getLeft(), update.getMiddle(), update.getRight());
				}
			} else {
				MatterOverdrive.LOGGER.info("Entity is not instance of IPropertyManaged");
			}
		}
	}
	
	public static void handlePacketUpdateClientTileProperties(List<Triple<PropertyType<?>, Short, Object>> updates, BlockPos pos) {
		LocalPlayer playerEntity = Minecraft.getInstance().player;
		if (playerEntity != null) {
			BlockEntity entity = playerEntity.level.getBlockEntity(pos);
			if (entity instanceof IPropertyManaged managed) {
				PropertyManager propertyManager = managed.getPropertyManager();
				for (Triple<PropertyType<?>, Short, Object> update : updates) {
					propertyManager.update(update.getLeft(), update.getMiddle(), update.getRight());
				}
			} else {
				MatterOverdrive.LOGGER.info("BlockEntity is not instance of IPropertyManaged");
			}
		}
	}

	public static void handlePacketUpdateMNScreen(BlockPos monitorPos) {
		Level world = Minecraft.getInstance().level;
		if (world != null) {
			BlockEntity tile = world.getBlockEntity(monitorPos);
			if (tile instanceof TilePatternMonitor monitor) {
//				monitor.handleScreenUpdate(monitorPos, world);
			}
		}
	}
}
