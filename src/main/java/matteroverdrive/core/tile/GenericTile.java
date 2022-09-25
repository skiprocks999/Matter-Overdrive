package matteroverdrive.core.tile;

import matteroverdrive.References;
import matteroverdrive.core.block.GenericEntityBlock;
import matteroverdrive.core.capability.IOverdriveCapability;
import matteroverdrive.core.property.IPropertyManaged;
import matteroverdrive.core.property.manager.BlockEntityPropertyManager;
import matteroverdrive.core.tile.utils.ITickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class GenericTile extends BlockEntity
		implements Nameable, ITickableTile, IPropertyManaged {

	private HashMap<Capability<?>, IOverdriveCapability> capabilities = new HashMap<>();

	public boolean hasMenu = false;
	private MenuProvider menu;

	private boolean isTickable = false;

	protected long ticks = 0;
	
	private boolean shouldSaveData = false;

	/**
	 * Property Manager for the BlockEntity. See: <a href=
	 * "https://github.com/BrassGoggledCoders/Transport/blob/develop/1.16.x/src/main/java/xyz/brassgoggledcoders/transport/container/locomotive/SteamLocomotiveContainer.java">...</a>
	 * For implementation example.
	 */
	protected final BlockEntityPropertyManager propertyManager;

	protected GenericTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.propertyManager = new BlockEntityPropertyManager(this);
	}

	public void setMenuProvider(MenuProvider menu) {
		hasMenu = true;
		this.menu = menu;
	}

	public MenuProvider getMenuProvider() {
		return menu;
	}

	public void setTickable() {
		isTickable = true;
	}
	
	@Override
	public boolean canTick() {
		return isTickable;
	}
	
	public boolean shouldSaveData() {
		return shouldSaveData;
	}
	
	/**
	 * This is a less efficient solution than just using the bitwise boolean operator |
	 * 
	 * However, being forced to use a specific boolean operator will ultimately lead to
	 * someone forgetting and causing a bug that takes forever to track down.
	 * 
	 * Maybe we can revisit the concept when we are looking at making performance improvements
	 * @param bool
	 */
	public void setShouldSaveData(boolean...changes) {
		for(boolean bool : changes) {
			if(bool) {
				shouldSaveData = true;
				break;
			}
		}
	}
	
	protected void resetShouldSaveData() {
		shouldSaveData = false;
	}

	@Override
	// this is about as fast as I can get it without hard-coding if-elses which I
	// don't want to do
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		IOverdriveCapability capability = capabilities.get(cap);
		return capability == null ? LazyOptional.empty() : capability.getCapability(cap, side);
	}

	public <T> void addCapability(@Nonnull Capability<T> key, @Nonnull IOverdriveCapability cap) {
		if (capabilities.containsKey(key)) {
			throw new RuntimeException("error: capability type " + cap.getSaveKey() + " already added");
		}
		capabilities.put(key, cap);
	}

	public <T> boolean hasCapability(Capability<T> cap) {
		return capabilities.containsKey(cap);
	}

	@Nullable
	public <T extends IOverdriveCapability, A> T exposeCapability(Capability<A> cap) {
		return (T) capabilities.get(cap);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide()) {
			for (IOverdriveCapability cap : capabilities.values()) {
				cap.onLoad(this);
			}
			this.propertyManager.sendBlockEntityChanges(this.getBlockPos());
		}
	}

	public void refreshCapabilities() {
		for (IOverdriveCapability cap : capabilities.values()) {
			cap.refreshCapability();
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (IOverdriveCapability cap : capabilities.values()) {
			cap.invalidateCapability();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		for (IOverdriveCapability cap : capabilities.values()) {
			tag.put(cap.getSaveKey(), cap.serializeNBT());
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		for (IOverdriveCapability cap : capabilities.values()) {
			cap.deserializeNBT(tag.getCompound(cap.getSaveKey()));
		}

	}

	@Override
	public CompoundTag getUpdateTag() {
		if (!level.isClientSide()) {
			// call me a butcher, because I'm hacking this game
			this.propertyManager.sendBlockEntityChanges(this.getBlockPos());
		}
		CompoundTag tag = super.getUpdateTag();
		//getFirstContactData(tag);
		saveAdditional(tag);
		return tag;
	}

	/**
	 * When the BlockEntity is marked as "Changed" call super and then send Property
	 * updates.
	 */
	@Override
	public void setChanged() {
		super.setChanged();
		if (!level.isClientSide()) {
			this.propertyManager.sendBlockEntityChanges(this.getBlockPos());
		}

	}

	public MutableComponent getContainerName(String name) {
		return Component.translatable("container." + name);
	}

	public Direction getFacing() {
		Level world = getLevel();
		BlockState state = world.getBlockState(getBlockPos());
		if (state.hasProperty(GenericEntityBlock.FACING)) {
			return state.getValue(GenericEntityBlock.FACING);
		}

		return Direction.UP;
	}

	public SimpleContainerData getCoordsData() {
		SimpleContainerData array = new SimpleContainerData(3);
		array.set(0, worldPosition.getX());
		array.set(1, worldPosition.getY());
		array.set(2, worldPosition.getZ());
		return array;
	}

	@Override
	// TODO allow translations
	public Component getName() {
		return Component.literal(References.ID + ".default.tile.name");
	}

	@Override
	public long getTicks() {
		return ticks;
	}

	@Override
	public void incrementTicks() {
		ticks++;
	};

	@Override
	public BlockEntityPropertyManager getPropertyManager() {
		return this.propertyManager;
	}
	
	@Override
	public void tick(Level world) {
		tickCommon();
		incrementTicks();
		if (world.isClientSide) {
			tickClient();
		} else {
			tickServer();
			if(shouldSaveData()) {
				setChanged();
				resetShouldSaveData();
			}
		}
	}
	
	public void onBlockBroken(Level world, BlockPos pos) {
		
	}
	
	//returns 
	public InteractionResult useClient(Player player, InteractionHand hand, BlockHitResult hit) {
		return hasMenu ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}
	
	public InteractionResult useServer(Player player, InteractionHand hand, BlockHitResult hit) {
		if(hasMenu) {
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
			player.openMenu(getMenuProvider());
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

}
