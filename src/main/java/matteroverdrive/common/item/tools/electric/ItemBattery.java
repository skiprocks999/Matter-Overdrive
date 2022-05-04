package matteroverdrive.common.item.tools.electric;

import java.util.ArrayList;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.core.capability.types.energy.CapabilityCreativeEnergyStorage;
import matteroverdrive.core.registers.IBulkRegistryObject;
import matteroverdrive.core.utils.UtilsRendering;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ItemBattery extends ItemElectric {

	private static final List<ItemBattery> BATTERIES = new ArrayList<>();

	private BatteryType type;

	public ItemBattery(BatteryType type) {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN), type.maxStorage, true, true);
		this.type = type;
		BATTERIES.add(this);
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (type == BatteryType.CREATIVE) {
			if (allowdedIn(category)) {
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
	public void applyTooltip(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		ItemBattery battery = (ItemBattery) stack.getItem();
		if (battery.type == BatteryType.CREATIVE) {
			tooltips.add(UtilsText.tooltip("creativeenergystored").withStyle(ChatFormatting.YELLOW));
		} else {
			super.applyTooltip(stack, level, tooltips, advanced);
		}
	}

	public enum BatteryType implements IBulkRegistryObject {

		REGULAR(525000, UtilsRendering.getRGBA(1, 191, 228, 230)),
		HIGHCAPACITY(1050000, UtilsRendering.getRGBA(1, 254, 203, 4)),
		CREATIVE(0, UtilsRendering.getRGBA(1, 230, 80, 20));

		public final int maxStorage;
		public final int color;

		private BatteryType(int maxStorage, int color) {
			this.maxStorage = maxStorage;
			this.color = color;
		}

	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ColorHandler {

		@SubscribeEvent
		public static void registerColoredBlocks(ColorHandlerEvent.Item event) {
			BATTERIES.forEach(item -> event.getItemColors().register((stack, index) -> {
				if (index == 1) {
					return item.type.color;
				} else {
					return 0xFFFFFF;
				}
			}, item));
		}
	}

}
