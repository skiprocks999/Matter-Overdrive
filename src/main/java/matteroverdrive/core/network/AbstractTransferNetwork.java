package matteroverdrive.core.network;

import java.util.ArrayList;

import matteroverdrive.core.network.cable.IAbstractCable;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractTransferNetwork<C extends IAbstractCable<?>, T, A, EMIT> extends AbstractNetwork<C, T, A> {

	public abstract EMIT emit(EMIT transfer, ArrayList<BlockEntity> ignored, boolean debug);
	
}
