package matteroverdrive.core.screen.component;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.References;
import matteroverdrive.common.item.ItemUpgrade.UpgradeType;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.ScreenComponentIcon.IconType;
import matteroverdrive.core.screen.component.utils.OverdriveScreenComponent;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.registry.ItemRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ScreenComponentSlot extends OverdriveScreenComponent {

	private final SlotType type;
	private int color = UtilsRendering.getRGBA(255, 255, 255, 255);
	private static final String BASE_TEXTURE_LOC = References.ID + ":textures/gui/slot/";
	private IconType icon = null;
	private ScreenComponentIcon iconComp = null;

	private UpgradeType[] upgradeSlotTypes;

	public ScreenComponentSlot(final SlotType type, final GenericScreen<?> gui, final int x, final int y,
			final int[] screenNumbers) {
		super(new ResourceLocation(BASE_TEXTURE_LOC + type.getName()), gui, x, y, type.width, type.height,
				screenNumbers);
		this.type = type;
	}

	public ScreenComponentSlot(final SlotType type, final IconType icon, final GenericScreen<?> gui, final int x,
			final int y, final int[] screenNumbers) {
		super(new ResourceLocation(BASE_TEXTURE_LOC + type.getName()), gui, x, y, type.width, type.height,
				screenNumbers);
		this.type = type;
		this.icon = icon;
	}

	@Override
	public void initScreenSize() {
		super.initScreenSize();
		if (icon != IconType.NONE) {
			int widthOffset;
			if (isMainSlot()) {
				widthOffset = (int) ((22 - icon.getTextWidth()) / 2);
			} else {
				widthOffset = (int) ((type.getWidth() - icon.getTextWidth()) / 2);
			}
			int heightOffset = (int) ((type.getHeight() - icon.getTextHeight()) / 2);
			this.iconComp = new ScreenComponentIcon(icon, gui, this.x + widthOffset + type.getXOffset(),
					this.y + heightOffset + type.getYOffset(), this.screenNumbers);
		}
	}

	public ScreenComponentSlot setUpgrades(UpgradeType[] types) {
		this.upgradeSlotTypes = types;
		return this;
	}

	@Override
	public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (type != SlotType.NONE) {
			UtilsRendering.bindTexture(resource);
			UtilsRendering.color(color);
			blit(stack, this.x + type.getXOffset(), this.y + type.getYOffset(), type.getTextureX(), type.getTextureY(),
					type.getWidth(), type.getHeight(), type.getWidth(), type.getHeight());

			UtilsRendering.color(UtilsRendering.getRGBA(255, 255, 255, 255));
			if (iconComp != null) {
				iconComp.renderBackground(stack, mouseX, mouseY, partialTicks);
			}
		}
	}

	@Override
	public void renderTooltip(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (upgradeSlotTypes != null && Screen.hasControlDown()) {
			List<FormattedCharSequence> components = new ArrayList<>();
			for (UpgradeType upgrade : upgradeSlotTypes) {
				components.add(Component.translatable(ItemRegistry.ITEM_UPGRADES.get(upgrade).get().getDescriptionId())
						.getVisualOrderText());
			}
			gui.renderTooltip(stack, components, mouseX, mouseY);
		}
	}

	private boolean isMainSlot() {
		return type == SlotType.MAIN || type == SlotType.MAIN_ACTIVE || type == SlotType.MAIN_DARK;
	}

	public enum SlotType {
		NONE(""), SMALL("slot_small"), BIG(22, 22, 0, 0, "slot_big", -2, -2),
		BIG_DARK(22, 22, 0, 0, "slot_big_dark", -2, -2), HOLO("slot_holo"), HOLO_BG("slot_holo_with_bg"),
		MAIN(37, 22, 0, 0, "slot_big_main", -2, -2), MAIN_DARK(37, 22, 0, 0, "slot_big_main_dark", -2, -2),
		MAIN_ACTIVE(37, 22, 0, 0, "slot_big_main_active", -2, -2), VANILLA("slot_vanilla");

		private final int width;
		private final int height;
		private final int textureX;
		private final int textureY;
		private final int xOffset;
		private final int yOffset;
		private final String name;

		private SlotType(int width, int height, int textureX, int textureY, String name, int xOffset, int yOffset) {
			this.width = width;
			this.height = height;
			this.textureX = textureX;
			this.textureY = textureY;
			this.name = name + ".png";
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		private SlotType(String name) {
			this(18, 18, 0, 0, name, 0, 0);
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getTextureX() {
			return textureX;
		}

		public int getTextureY() {
			return textureY;
		}

		public String getName() {
			return name;
		}

		public int getXOffset() {
			return xOffset;
		}

		public int getYOffset() {
			return yOffset;
		}

		public String getTextureLoc() {
			return BASE_TEXTURE_LOC + getName();
		}

	}

}
