package matteroverdrive.core.cable.types.matter_network;

import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.utils.UtilsMatter;
import matteroverdrive.core.utils.UtilsTile;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class MatterNetworkUtils {

	public static MatterNetworkEMPack recieveEM(BlockEntity reciever, Direction connection, 
			MatterNetworkEMPack sending, boolean debug) {
		
		double matterRecieved = 0;
		if(UtilsMatter.isMatterReceiver(reciever, connection)) {
			LazyOptional<ICapabilityMatterStorage> lazy = reciever.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE, connection).cast();
			if(lazy.isPresent()) {
				ICapabilityMatterStorage matter = lazy.resolve().get();
				matterRecieved = matter.receiveMatter(sending.matter(), debug);
			}
		}
		
		int feRecieved = 0;
		if(UtilsTile.isFEReciever(reciever, connection)) {
			LazyOptional<IEnergyStorage> lazy = reciever.getCapability(CapabilityEnergy.ENERGY, connection).cast();
			if(lazy.isPresent()) {
				IEnergyStorage energy = lazy.resolve().get();
				feRecieved = energy.receiveEnergy(sending.fe(), debug);
			}
		}
		
		return new MatterNetworkEMPack(feRecieved, matterRecieved);
	}
	
}
