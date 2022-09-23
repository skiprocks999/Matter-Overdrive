package matteroverdrive.common.item.tools.electric;

import java.util.List;

import matteroverdrive.References;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.capability.types.energy.CapabilityCreativeEnergyStorage;
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

public class ItemBattery extends ItemElectric {

	public final BatteryType type;

	public ItemBattery(BatteryType type) {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN), false, type.maxStorage, true, true);
		this.type = type;
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (type == BatteryType.CREATIVE) {
			if (allowedIn(category)) {
				items.add(new ItemStack(this));
			}
		} else {
			super.fillItemCategory(category, items);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		ItemBattery battery = (ItemBattery) stack.getItem();
		if (battery.type == BatteryType.CREATIVE) {
			return new CapabilityCreativeEnergyStorage(Integer.MAX_VALUE, true, true);
		} else {
			return super.initCapabilities(stack, nbt);
		}

	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		ItemBattery battery = (ItemBattery) stack.getItem();
		if (battery.type == BatteryType.CREATIVE) {
			return false;
		}
		return super.isBarVisible(stack);
	}

	@Override
	public void appendPostSuperTooltip(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		ItemBattery battery = (ItemBattery) stack.getItem();
		if (battery.type == BatteryType.CREATIVE) {
			tooltips.add(UtilsText.tooltip("creativeenergystored").withStyle(ChatFormatting.YELLOW));
		} else {
			super.appendPostSuperTooltip(stack, level, tooltips, advanced);
		}
	}
	
	@Override
	public boolean isColored() {
		return true;
	}
	
	@Override
	public int getNumOfLayers() {
		return 2;
	}
	
	@Override
	public int getColor(ItemStack item, int layer) {
		if (layer == 1) {
			return ((ItemBattery)item.getItem()).type.color.getColor();
		} else {
			return Colors.WHITE.getColor();
		}
	}

	public enum BatteryType implements IBulkRegistryObject {

		REGULAR(525000, Colors.MATTER),
		HIGHCAPACITY(1050000, Colors.YELLOW_STRIPES),
		CREATIVE(0, Colors.HOLO_RED);

		public final int maxStorage;
		public final Colors color;

		private BatteryType(int maxStorage, Colors color) {
			this.maxStorage = maxStorage;
			this.color = color;
		}

		@Override
		public String id() {
			return "battery_" + this.toString().toLowerCase();
		}

	}

}
