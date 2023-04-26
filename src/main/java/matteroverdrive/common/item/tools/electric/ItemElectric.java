package matteroverdrive.common.item.tools.electric;

import java.util.List;

import javax.annotation.Nullable;

import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.capability.types.energy.CapabilityEnergyStorage;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemElectric extends OverdriveItem {

	private int maxStorage;
	private boolean hasInput;
	private boolean hasOutput;

	public ItemElectric(Properties properties, boolean hasShiftTip, int maxStorage, boolean hasInput, boolean hasOutput) {
		super(properties, hasShiftTip);
		this.maxStorage = maxStorage;
		this.hasInput = hasInput;
		this.hasOutput = hasOutput;
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (allowedIn(category)) {
			items.add(new ItemStack(this));
			ItemStack filled = new ItemStack(this);
			filled.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
				h.receiveEnergy(h.getMaxEnergyStored(), false);
			});
			items.add(filled);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new CapabilityEnergyStorage(maxStorage, hasInput, hasOutput);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
			CapabilityEnergyStorage cap = (CapabilityEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY).cast()
					.resolve().get();
			return cap.getEnergyStored() < cap.getMaxEnergyStored();
		}
		return super.isBarVisible(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
			return (int) Math.round(stack.getCapability(CapabilityEnergy.ENERGY).map(h -> {
				if (h.getMaxEnergyStored() > 0) {
					return 13.0 * h.getEnergyStored() / h.getMaxEnergyStored();
				}
				return 13.0;
			}).orElse(13.0));
		}
		return super.getBarWidth(stack);
	}

	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag tag = super.getShareTag(stack);
		if (tag == null) {
			tag = new CompoundTag();
		}
		// had to expose cap because it whined about tag not being effectively final
		LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
		if (cap.isPresent()) {
			CapabilityEnergyStorage energy = ((CapabilityEnergyStorage) cap.resolve().get());
			tag.put(energy.getSaveKey(), energy.serializeNBT());
		}
		return tag;
	}

	@Override
	public void readShareTag(ItemStack stack, CompoundTag nbt) {
		if (nbt != null) {
			stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
				CapabilityEnergyStorage energy = (CapabilityEnergyStorage) h;
				energy.deserializeNBT(nbt.getCompound(energy.getSaveKey()));
				nbt.remove(energy.getSaveKey());
			});
		}
		super.readShareTag(stack, nbt);
	}

	@Override
	public void appendPostSuperTooltip(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
			int max = h.getMaxEnergyStored();
			int base = UtilsText.getBigBase(max);
			String stored = UtilsText.getFormattedBigPower(h.getEnergyStored(), base);
			String maxE = UtilsText.getFormattedBigPower(max, base);
			tooltips.add(UtilsText.tooltip("energystored", stored, maxE, UtilsText.getPrefixForBase(base))
					.withStyle(ChatFormatting.YELLOW));
		});
	}
	
	@Nullable
	public CapabilityEnergyStorage getEnergyCap(ItemStack stack) {
		return (CapabilityEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY).cast().resolve().get();
	}

}
