package matteroverdrive.common.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext.Builder;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockOverdrive extends Block {

	private boolean useLootTable;

	public BlockOverdrive(Properties properties, boolean useLootTable) {
		super(properties);
		this.useLootTable = useLootTable;
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, Builder pBuilder) {
		if (useLootTable) {
			return super.getDrops(pState, pBuilder);
		}
		return Arrays.asList(new ItemStack(this));
	}

}
