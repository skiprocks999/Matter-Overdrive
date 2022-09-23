package matteroverdrive.core.packet.type.serverbound.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.capability.types.item.CapabilityInventory;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
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

public class PacketUpdateCapabilitySides extends AbstractOverdrivePacket<PacketUpdateCapabilitySides> {

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

	@Override
	public boolean handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerLevel world = ctx.getSender().getLevel();
			if (world != null) {
				BlockEntity tile = world.getBlockEntity(pos);
				if (tile instanceof GenericTile generic) {
					if (generic.hasCapability(type.capability)) {
						switch (type) {
						case ITEM:
							CapabilityInventory inv = generic.exposeCapability(type.capability);
							if (input) {
								inv.setInputDirs(inDirs);
							}
							if (output) {
								inv.setOutputDirs(outDirs);
							}
							inv.refreshCapability();
							generic.setChanged();
							break;
						case ENERGY:
							CapabilityEnergyStorage energy = generic.exposeCapability(type.capability);
							if (input) {
								energy.setInputDirs(inDirs);
							}
							if (output) {
								energy.setOutputDirs(outDirs);
							}
							energy.refreshCapability();
							generic.setChanged();
							break;
						case MATTER:
							CapabilityMatterStorage matter = generic.exposeCapability(type.capability);
							if (input) {
								matter.setInputDirs(inDirs);
							}
							if (output) {
								matter.setOutputDirs(outDirs);
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
		return true;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(type);
		buf.writeBoolean(input);
		if (input) {
			buf.writeInt(inDirs.size());
			for (Direction dir : inDirs) {
				buf.writeEnum(dir);
			}
		}
		buf.writeBoolean(output);
		if (output) {
			buf.writeInt(outDirs.size());
			for (Direction dir : outDirs) {
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
