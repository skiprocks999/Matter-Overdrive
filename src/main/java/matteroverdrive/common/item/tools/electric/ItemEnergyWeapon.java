package matteroverdrive.common.item.tools.electric;

import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ItemEnergyWeapon extends ItemElectric {

	private int defaultUsage = 0;

	public ItemEnergyWeapon(Properties properties, boolean hasShiftTip, int maxStorage, boolean hasInput, boolean hasOutput,
			int defaultUsage) {
		super(properties, hasShiftTip, maxStorage, hasInput, hasOutput);
		this.defaultUsage = defaultUsage;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide) {
			ItemStack handStack = player.getItemInHand(hand);
			handStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> {
				((CapabilityEnergyStorage) h).removeEnergy(defaultUsage);
			});
		}
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

}
