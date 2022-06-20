package matteroverdrive.core.utils;

import javax.annotation.Nullable;

import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class UtilsItem {

	public static boolean compareItems(Item a, Item b) {
		return ItemStack.isSame(new ItemStack(a), new ItemStack(b));
	}
	
	@Nullable
	public static IEnergyStorage getEnergyCap(ItemStack stack){
		LazyOptional<IEnergyStorage> lazy = stack.getCapability(CapabilityEnergy.ENERGY);
		if(lazy.isPresent()) {
			return lazy.resolve().get();
		}
		return null;
	}
	
	@Nullable
	public static CapabilityEnergyStorage getEnergyStorageCap(ItemStack stack) {
		IEnergyStorage storage = getEnergyCap(stack);
		if(storage instanceof CapabilityEnergyStorage cap) {
			return cap;
		}
		return null;
	}
	
	public static ItemStack getStackFromHand(Player player, Class<?> clazz) {
		ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(!main.isEmpty() && clazz.getName().equals(main.getClass().getName())) {
			return main;
		}
		ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
		if(!off.isEmpty() && clazz.getName().equals(main.getClass().getName())) {
			return off;
		}
		return ItemStack.EMPTY;
	}

}
