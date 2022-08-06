package matteroverdrive.core.component.addons.matter;

import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import matteroverdrive.common.item.tools.ItemMatterContainer;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.capability.types.matter.ICapabilityMatterStorage;
import matteroverdrive.core.component.addons.MatterStorageComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MatterScreenAddon extends BasicScreenAddon {

	private ICapabilityMatterStorage storage;

	private IAsset asset;

	public MatterScreenAddon(int posX, int posY, ICapabilityMatterStorage storage) {
		super(posX, posY);
		this.storage = storage;
	}

	@Override
	public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY,
			int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void drawForegroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY,
			int mouseX, int mouseY, float partialTicks) {
	}

	@Override
	public List<Component> getTooltipLines() {
		List<Component> tooltips = new ArrayList<>();
		tooltips.add(Component.empty()
				// Default Gold Color
				.withStyle(ChatFormatting.GOLD)
				// Default Matter Text
				.append(Component.translatable("tooltip.matteroverdrive.matterstorage.matter"))
				.append(storage.getMatterStored() > 0 ?
				// If it has stored matter add the stored tooltip
						Component.translatable("tooltip.matteroverdrive.matterstorage.stored")
								.withStyle(ChatFormatting.WHITE)
						:
						// If it doesn't have stored matter then add the empty tooltip
						Component.translatable("tooltip.matteroverdrive.matterstorage.empty")
								.withStyle(ChatFormatting.WHITE)));
		// Add the amount tooltip
		tooltips.add(Component.translatable("tooltip.matteroverdrive.matterstorage.amount")
				// Default Gold Color
				.withStyle(ChatFormatting.GOLD)
				// Add Decimal-Formatted information with ROOT locale
				.append(Component.literal(
						ChatFormatting.WHITE + DecimalFormat.getInstance(Locale.ROOT).format(storage.getMatterStored())
								+ ChatFormatting.GOLD + "/" + ChatFormatting.WHITE
								+ DecimalFormat.getInstance(Locale.ROOT).format(storage.getMaxMatterStored())
								+ ChatFormatting.DARK_AQUA + "mb")));

		if (Minecraft.getInstance().player != null) {
			ItemStack carried = Minecraft.getInstance().player.containerMenu.getCarried();
			// If the item held on the cursor is a capability holder of the MatterStorage
			// cap.
			if (!carried.isEmpty() && carried.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
				// Then get the capability
				carried.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).ifPresent(storageCap -> {
					// Check that it's also an ItemMatterContainer
					boolean isStorage = carried.getItem() instanceof ItemMatterContainer;
					// If it's an ItemMatterContainer then get the maxMatterStored, otherwise
					// default to Double.MAX_VALUE
					double amount = isStorage ? storageCap.getMaxMatterStored() : Double.MAX_VALUE;
					// Simulate both ways for fill and drain to check if the carried item can fill
					// or drain into the storage.
					boolean canFillFromItem = storage.receiveMatter(storageCap.extractMatter(amount, true),
							true) == amount;
					boolean canDrainFromItem = storageCap.receiveMatter(storage.extractMatter(amount, true),
							true) == amount;
					// Add appropriate tooltips where needed.
					if (canFillFromItem)
						tooltips.add(Component.translatable("tooltip.matteroverdrive.matterstorage.can_fill_from_item")
								.withStyle(ChatFormatting.BLUE));
					if (canDrainFromItem)
						tooltips.add(Component.translatable("tooltip.matteroverdrive.matterstorage.can_drain_from_item")
								.withStyle(ChatFormatting.GOLD));
					if (canFillFromItem)
						tooltips.add(Component.translatable("tooltip.matteroverdrive.matterstorage.action_fill")
								.withStyle(ChatFormatting.DARK_GRAY));
					if (canDrainFromItem)
						tooltips.add(Component.translatable("tooltip.matteroverdrive.matterstorage.action_drain")
								.withStyle(ChatFormatting.DARK_GRAY));
					if (!canFillFromItem && !canDrainFromItem)
						tooltips.add(Component.translatable("tooltip.matteroverdrive.matterstorage.no_action"));
				});
			} else {
				// If the item doesn't have the MatterStorage Capability then just state that
				// the item has no storage.
				tooltips.add(Component.translatable("tooltips.matteroverdrive.matterstorage.no_storage"));
			}
		}
		// Finally return the tooltip list.
		return tooltips;
	}

	@Override
	public int getXSize() {
		return asset != null ? asset.getArea().width : 0;
	}

	@Override
	public int getYSize() {
		return asset != null ? asset.getArea().height : 0;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!Minecraft.getInstance().player.containerMenu.getCarried().isEmpty()
				&& Minecraft.getInstance().player.containerMenu.getCarried()
						.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).isPresent()) {
			ItemStack carried = Minecraft.getInstance().player.containerMenu.getCarried();
			Screen screen = Minecraft.getInstance().screen;
			if (screen instanceof AbstractContainerScreen<?> abstractScreen
					&& abstractScreen.getMenu() instanceof ILocatable locatable) {
				if (!isMouseOver(mouseX - abstractScreen.getGuiLeft(), mouseY - abstractScreen.getGuiTop()))
					return false;
				Minecraft.getInstance().getSoundManager()
						.play(new SimpleSoundInstance(SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 1f, 1f,
								RandomSource.create(), Minecraft.getInstance().player.blockPosition()));
				CompoundTag tag = new CompoundTag();
				if (storage instanceof MatterStorageComponent<?> component) {
					tag.putString("Name", component.getName());
				} else
					tag.putBoolean("Invalid", true);
				carried.getCapability(MatterOverdriveCapabilities.MATTER_STORAGE).ifPresent(storageCap -> {
					boolean isStorage = carried.getItem() instanceof ItemMatterContainer;
					double amount = isStorage ? storageCap.getMaxMatterStored() : Double.MAX_VALUE;
					boolean canFillFromItem = storage.receiveMatter(storageCap.extractMatter(amount, true),
							true) == amount;
					boolean canDrainFromItem = storageCap.receiveMatter(storage.extractMatter(amount, true),
							true) == amount;
					if (canFillFromItem && button == 0)
						tag.putBoolean("Fill", true);
					if (canDrainFromItem && button == 1)
						tag.putBoolean("Fill", false);
				});
				Titanium.NETWORK.get()
						.sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), -3, tag));
				return true;
			}
		}
		return false;
	}
}
