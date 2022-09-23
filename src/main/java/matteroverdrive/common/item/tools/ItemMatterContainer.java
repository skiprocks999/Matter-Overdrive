package matteroverdrive.common.item.tools;

import java.util.List;

import matteroverdrive.References;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.CapabilityCreativeMatterStorage;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.registers.IBulkRegistryObject;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class ItemMatterContainer extends OverdriveItem {

	public final ContainerType container;

	public ItemMatterContainer(ContainerType type) {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN), false);
		container = type;
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (allowedIn(category)) {
			items.add(new ItemStack(this));
			if (container != ContainerType.CREATIVE) {
				ItemStack filled = new ItemStack(this);
				filled.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).ifPresent(h -> {
					h.receiveMatter(h.getMaxMatterStored(), false);
				});
				items.add(filled);
			}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		if (container == ContainerType.CREATIVE) {
			return new CapabilityCreativeMatterStorage(Double.MAX_VALUE, true, true);
		} else {
			return new CapabilityMatterStorage(container.capacity, true, true);
		}

	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (container == ContainerType.CREATIVE) {
			return false;
		}
		if (stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
			CapabilityMatterStorage cap = (CapabilityMatterStorage) stack
					.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).cast().resolve().get();
			return cap.getMatterStored() < cap.getMaxMatterStored();
		}
		return super.isBarVisible(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
			return (int) Math.round(stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).map(h -> {
				if (h.getMaxMatterStored() > 0) {
					return 13.0 * h.getMatterStored() / h.getMaxMatterStored();
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
		LazyOptional<ICapabilityMatterStorage> cap = stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE);
		if (cap.isPresent()) {
			CapabilityMatterStorage matter = ((CapabilityMatterStorage) cap.resolve().get());
			tag.put(matter.getSaveKey(), matter.serializeNBT());
		}
		return tag;
	}

	@Override
	public void readShareTag(ItemStack stack, CompoundTag nbt) {
		if (nbt != null) {
			stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).ifPresent(h -> {
				CapabilityMatterStorage matter = (CapabilityMatterStorage) h;
				matter.deserializeNBT(nbt.getCompound(matter.getSaveKey()));
				nbt.remove(matter.getSaveKey());
			});
		}
		super.readShareTag(stack, nbt);
	}

	@Override
	public void appendPostSuperTooltip(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		if (container == ContainerType.CREATIVE) {
			tooltips.add(UtilsText.tooltip("creativeenergystored").withStyle(ChatFormatting.AQUA));
		} else {
			stack.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).ifPresent(h -> {
				double max = h.getMaxMatterStored();
				int base = UtilsText.getBigBase(max);
				String stored = UtilsText.getFormattedBigMatter(h.getMatterStored(), base);
				String maxE = UtilsText.getFormattedBigMatter(max, base);
				tooltips.add(UtilsText.tooltip("matterstored", stored, maxE, UtilsText.getPrefixForBase(base))
						.withStyle(ChatFormatting.AQUA));
			});
		}
	}
	
	@Override
	public boolean isColored() {
		return true;
	}
	
	@Override
	public int getNumOfLayers() {
		return 3;
	}
	
	@Override
	public int getColor(ItemStack item, int layer) {
		if (layer == 2) {
			return Colors.MATTER.getColor();
		} else if (layer == 1) {
			return ((ItemMatterContainer)item.getItem()).container.bandColor.getColor();
		} else {
			return Colors.WHITE.getColor();
		}
	}

	public enum ContainerType implements IBulkRegistryObject {
		REGULAR(1000, Colors.YELLOW_STRIPES), CREATIVE(0, Colors.ORANGE_STRIPES);

		public final Colors bandColor;
		public final double capacity;

		private ContainerType(double capacity, Colors bandColor) {
			this.capacity = capacity;
			this.bandColor = bandColor;
		}

		@Override
		public String id() {
			return "matter_container_" + this.toString().toLowerCase();
		}

	}

}
