package matteroverdrive.common.item;

import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.registers.IBulkRegistryObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemUpgrade extends OverdriveItem {

	public UpgradeType type;

	public ItemUpgrade(UpgradeType type) {
		super(new Item.Properties().tab(References.MAIN).stacksTo(16));
		this.type = type;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltips, isAdvanced);
		if (Screen.hasShiftDown()) {
			if (type.speedBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.speedbonus",
						(int) (type.speedBonus * 100) + "%")
								.withStyle(type.speedBonus < 1 ? ChatFormatting.RED : ChatFormatting.GREEN));
			}
			if (type.matterStorageBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.mattstorebonus",
						(int) (type.matterStorageBonus * 100) + "%")
								.withStyle(type.matterStorageBonus < 1 ? ChatFormatting.RED : ChatFormatting.GREEN));
			}
			if (type.matterUsageBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.mattusebonus",
						(int) (type.matterUsageBonus * 100) + "%")
								.withStyle(type.matterUsageBonus < 1 ? ChatFormatting.GREEN : ChatFormatting.RED));
			}
			if (type.failureChanceBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.failurebonus",
						(int) (type.failureChanceBonus * 100) + "%")
								.withStyle(type.failureChanceBonus < 1 ? ChatFormatting.GREEN : ChatFormatting.RED));
			}
			if (type.powerStorageBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.powstorebonus",
						(int) (type.powerStorageBonus * 100) + "%")
								.withStyle(type.powerStorageBonus < 1 ? ChatFormatting.RED : ChatFormatting.GREEN));
			}
			if (type.powerUsageBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.powusebonus",
						(int) (type.powerUsageBonus * 100) + "%")
								.withStyle(type.powerUsageBonus < 1 ? ChatFormatting.GREEN : ChatFormatting.RED));
			}
			if (type.rangeBonus != 1.0) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.rangebonus",
						(int) (type.rangeBonus * 100) + "%")
								.withStyle(type.rangeBonus < 1 ? ChatFormatting.RED : ChatFormatting.GREEN));
			}
			if (type == UpgradeType.MUFFLER) {
				tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.mufflerupgrade")
						.withStyle(ChatFormatting.GREEN));
			}
		} else {
			tooltips.add(new TranslatableComponent("tooltip.matteroverdrive.upgradeinfo",
					new TranslatableComponent("tooltip.matteroverdrive.upgradeshift").withStyle(ChatFormatting.YELLOW))
							.withStyle(ChatFormatting.GRAY));
		}

	}

	public enum UpgradeType implements IBulkRegistryObject {

		SPEED(1.25, 1, 1.25, 1.25, 1, 1.25, 1), POWER(0.5, 1, 1, 1.25, 1, 0.75, 1),
		FAIL_SAFE(0.75, 1, 1.25, 0.5, 1, 1.25, 1), RANGE(1, 1, 1.5, 1, 1, 1.5, 4), POWER_STORAGE(1, 1, 1, 1, 2, 1, 1),
		HYPER_SPEED(1.85, 1, 2, 1.25, 1, 2, 1), MATTER_STORAGE(1, 2, 1, 1, 1, 1, 1), MUFFLER(1, 1, 1, 1, 1, 1, 1);

		public final double speedBonus;
		public final double matterStorageBonus;
		public final double matterUsageBonus;
		public final double failureChanceBonus;
		public final double powerStorageBonus;
		public final double powerUsageBonus;
		public final double rangeBonus;

		private UpgradeType(double speedBonus, double matterStorageBonus, double matterUsageBonus,
				double failureChanceBonus, double powerStorageBonus, double powerUsageBonus, double rangeBonus) {
			this.speedBonus = speedBonus;
			this.matterStorageBonus = matterStorageBonus;
			this.matterUsageBonus = matterUsageBonus;
			this.failureChanceBonus = failureChanceBonus;
			this.powerStorageBonus = powerStorageBonus;
			this.powerUsageBonus = powerUsageBonus;
			this.rangeBonus = rangeBonus;
		}

	}

}
