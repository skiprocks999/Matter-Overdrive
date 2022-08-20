package matteroverdrive.core.packet.type.serverbound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.tile.GenericTile;
import matteroverdrive.core.utils.UtilsMatter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketUpdateCapabilitySides {

	private final BlockPos pos;
	private final CapabilityType type;
	private final List<Direction> inDirs;
	private final List<Direction> outDirs;
	private final boolean input;
	private final boolean output;

	public PacketUpdateCapabilitySides(BlockPos pos, CapabilityType type, boolean input, boolean output,
			@Nullable List<Direction> inDirs, @Nullable List<Direction> outDirs) {
		this.pos = pos;
		this.type = type;
		this.inDirs = inDirs;
		this.outDirs = outDirs;
		this.input = input;
		this.output = output;
	}

	public static void handle(PacketUpdateCapabilitySides message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = ctx.getSender().getLevel();
			if (world != null) {
				BlockEntity tile = world.getBlockEntity(message.pos);
				if (tile instanceof GenericTile generic) {
					if (generic.hasCapability(message.type.capability)) {
						switch (message.type) {
						case ITEM:
							CapabilityInventory inv = generic.exposeCapability(message.type.capability);
							if (message.input) {
								inv.setInputDirs(message.inDirs);
							}
							if (message.output) {
								inv.setOutputDirs(message.outDirs);
							}
							inv.refreshCapability();
							generic.setChanged();
							break;
						case ENERGY:
							CapabilityEnergyStorage energy = generic.exposeCapability(message.type.capability);
							if (message.input) {
								energy.setInputDirs(message.inDirs);
							}
							if (message.output) {
								energy.setOutputDirs(message.outDirs);
							}
							energy.refreshCapability();
							generic.setChanged();
							break;
						case MATTER:
							CapabilityMatterStorage matter = generic.exposeCapability(message.type.capability);
							if (message.input) {
								matter.setInputDirs(message.inDirs);
							}
							if (message.output) {
								matter.setOutputDirs(message.outDirs);
							}
							matter.refreshCapability();
							generic.setChanged();
							UtilsMatter.updateAdjacentMatterCables(generic);
							break;
						default:
							break;
						}
					}
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketUpdateCapabilitySides pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeEnum(pkt.type);
		buf.writeBoolean(pkt.input);
		if (pkt.input) {
			buf.writeInt(pkt.inDirs.size());
			for (Direction dir : pkt.inDirs) {
				buf.writeEnum(dir);
			}
		}
		buf.writeBoolean(pkt.output);
		if (pkt.output) {
			buf.writeInt(pkt.outDirs.size());
			for (Direction dir : pkt.outDirs) {
				buf.writeEnum(dir);
			}
		}
	}

	public static PacketUpdateCapabilitySides decode(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		CapabilityType type = buf.readEnum(CapabilityType.class);
		boolean input = buf.readBoolean();
		List<Direction> inDirs = null;
		if (input) {
			inDirs = new ArrayList<>();
			int inDirSize = buf.readInt();
			for (int i = 0; i < inDirSize; i++) {
				inDirs.add(buf.readEnum(Direction.class));
			}
		}
		boolean output = buf.readBoolean();
		List<Direction> outDirs = null;
		if (output) {
			outDirs = new ArrayList<>();
			int outDirSize = buf.readInt();
			for (int i = 0; i < outDirSize; i++) {
				outDirs.add(buf.readEnum(Direction.class));
			}
		}
		return new PacketUpdateCapabilitySides(pos, type, input, output, inDirs, outDirs);
	}

	public static enum CapabilityType {
		ITEM(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY), MATTER(MatterOverdriveCapabilities.MATTER_STORAGE),
		ENERGY(CapabilityEnergy.ENERGY);

		public final Capability<?> capability;

		private CapabilityType(Capability<?> cap) {
			capability = cap;
		}
	}

}
