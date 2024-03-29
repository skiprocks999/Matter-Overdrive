package matteroverdrive.core.screen.component.wrappers;

import java.util.function.Consumer;
import java.util.function.Supplier;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.common.tile.transporter.utils.TransporterLocationWrapper;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.button.ButtonOverdrive;
import matteroverdrive.core.screen.component.edit_box.EditBoxOverdrive;
import matteroverdrive.core.screen.component.edit_box.EditBoxSuppliableName;
import matteroverdrive.core.screen.component.edit_box.EditBoxOverdrive.EditBoxTextures;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsText;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WrapperTransporterLocationEditer {

	private EditBoxSuppliableName nameBox;
	private EditBoxOverdrive xCoordinateBox;
	private EditBoxOverdrive yCoordinateBox;
	private EditBoxOverdrive zCoordinateBox;
	private ButtonOverdrive[] incButtons = new ButtonOverdrive[3];
	private ButtonOverdrive[] decButtons = new ButtonOverdrive[3];
	private ButtonOverdrive resetLocation;
	private ButtonOverdrive importFlashdriveData;

	private GenericScreen<?> gui;
	private Supplier<TileTransporter> transporterSupplier;
	private int currIndex;

	private int xOffset;
	private int yOffset;

	private static final Component PLUS = Component.literal("+");
	private static final Component MINUS = Component.literal("-");

	public WrapperTransporterLocationEditer(GenericScreen<?> gui, int xOffset, int yOffset,
			Supplier<TileTransporter> transporterSupplier) {
		this.gui = gui;
		this.transporterSupplier = transporterSupplier;
	}

	public void setCurrIndex(int index) {
		currIndex = index;
	}

	public int getCurrIndex() {
		return currIndex;
	}

	public void initButtons() {
		int guiWidth = gui.getXPos();
		int guiHeight = gui.getYPos();

		nameBox = new EditBoxSuppliableName(EditBoxTextures.OVERDRIVE_EDIT_BOX, gui, guiWidth + 65 + xOffset, guiHeight + 30 + yOffset, 120, 15, () -> {
			return transporterSupplier.get().locationManager.getLocation(currIndex).getName().getString();
		});
		nameBox.setTextColor(Colors.WHITE.getColor());
		nameBox.setTextColorUneditable(Colors.WHITE.getColor());
		nameBox.setMaxLength(15);
		nameBox.setResponder(string -> {
			nameBox.setFocus(true);
			xCoordinateBox.setFocus(false);
			yCoordinateBox.setFocus(false);
			zCoordinateBox.setFocus(false);
			sendNameChange(string);
		});

		xCoordinateBox = new EditBoxSuppliableName(EditBoxTextures.OVERDRIVE_EDIT_BOX, gui, guiWidth + 94 + xOffset, guiHeight + 50 + yOffset, 70, 15,
				() -> {
					return transporterSupplier.get().locationManager.getLocation(currIndex).getDestination().getX()
							+ "";
				});
		xCoordinateBox.setTextColor(Colors.WHITE.getColor());
		xCoordinateBox.setTextColorUneditable(Colors.WHITE.getColor());
		// the world border is 30M....
		xCoordinateBox.setMaxLength(9);
		xCoordinateBox.setFilter(EditBoxOverdrive.INTEGER_BOX);
		xCoordinateBox.setResponder(string -> {
			nameBox.setFocus(false);
			xCoordinateBox.setFocus(true);
			yCoordinateBox.setFocus(false);
			zCoordinateBox.setFocus(false);
			sendCoordinateChange(string, 0, currIndex);
		});

		yCoordinateBox = new EditBoxSuppliableName(EditBoxTextures.OVERDRIVE_EDIT_BOX, gui, guiWidth + 94 + xOffset, guiHeight + 70 + yOffset, 70, 15,
				() -> {
					return transporterSupplier.get().locationManager.getLocation(currIndex).getDestination().getY()
							+ "";
				});
		yCoordinateBox.setTextColor(Colors.WHITE.getColor());
		yCoordinateBox.setTextColorUneditable(Colors.WHITE.getColor());
		yCoordinateBox.setMaxLength(9);
		yCoordinateBox.setFilter(EditBoxOverdrive.INTEGER_BOX);
		yCoordinateBox.setResponder(string -> {
			nameBox.setFocus(false);
			xCoordinateBox.setFocus(false);
			yCoordinateBox.setFocus(true);
			zCoordinateBox.setFocus(false);
			sendCoordinateChange(string, 1, currIndex);
		});

		zCoordinateBox = new EditBoxSuppliableName(EditBoxTextures.OVERDRIVE_EDIT_BOX, gui, guiWidth + 94 + xOffset, guiHeight + 90 + yOffset, 70, 15,
				() -> {
					return transporterSupplier.get().locationManager.getLocation(currIndex).getDestination().getZ()
							+ "";
				});
		zCoordinateBox.setTextColor(Colors.WHITE.getColor());
		zCoordinateBox.setTextColorUneditable(Colors.WHITE.getColor());
		zCoordinateBox.setMaxLength(9);
		zCoordinateBox.setFilter(EditBoxOverdrive.INTEGER_BOX);
		zCoordinateBox.setResponder(string -> {
			nameBox.setFocus(false);
			xCoordinateBox.setFocus(false);
			yCoordinateBox.setFocus(false);
			zCoordinateBox.setFocus(true);
			sendCoordinateChange(string, 2, currIndex);
		});

		for (int i = 0; i < incButtons.length; i++) {
			final int ref = i;
			incButtons[i] = new ButtonOverdrive(gui, 79 + xOffset, 50 + 20 * i + yOffset, 15, 15, () -> PLUS, button -> {
				handleIncDec(ref, 1);
			}).setLeft().setColor(Colors.WHITE.getColor()).setSound(getIncDecSound());
		}
		for (int i = 0; i < decButtons.length; i++) {
			final int ref = i;
			decButtons[i] = new ButtonOverdrive(gui, 164 + xOffset, 50 + 20 * i + yOffset, 15, 15, () -> MINUS, button -> {
				handleIncDec(ref, -1);
			}).setRight().setColor(Colors.WHITE.getColor()).setSound(getIncDecSound());
		}
		resetLocation = new ButtonOverdrive(gui, 133 + xOffset, 125 + yOffset, 60, 20, () -> UtilsText.gui("resetpos"),
				button -> {
					TileTransporter transporter = transporterSupplier.get();
					if(transporter != null) {
						nameBox.setValue(UtilsText.gui("unknown").getString());
						xCoordinateBox.setValue("0");
						yCoordinateBox.setValue("-1000");
						zCoordinateBox.setValue("0");
						transporter.locationManager.getAllLocations()[currIndex] = new TransporterLocationWrapper();
						transporter.getPropertyManager().updateServerBlockEntity(transporter.locationManagerProp, transporter.locationManager.serializeNbt());
					}
				}).setSound(getDefaultSound());

		importFlashdriveData = new ButtonOverdrive(gui, 57 + xOffset, 125 + yOffset, 60, 20, () -> UtilsText.gui("importpos"),
				button -> {
					TileTransporter transporter = transporterSupplier.get();
					if(transporter != null) {
						ItemStack flashdrive = transporter.getInventoryCap().getStackInSlot(1);
						if (!flashdrive.isEmpty() && flashdrive.hasTag()
								&& flashdrive.getTag().contains(UtilsNbt.BLOCK_POS)) {
							CompoundTag tag = flashdrive.getTag();
							BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(UtilsNbt.BLOCK_POS));
							ResourceKey<Level> dimension = UtilsNbt
									.readDimensionFromTag(tag.getCompound(UtilsNbt.DIMENSION));
							xCoordinateBox.setValue(pos.getX() + "");
							yCoordinateBox.setValue(pos.getY() + "");
							zCoordinateBox.setValue(pos.getZ() + "");
							TransporterLocationWrapper wrapper = transporter.locationManager.getLocation(currIndex);
							wrapper.setDestination(pos);
							wrapper.setDimension(dimension);
							transporter.getPropertyManager().updateServerBlockEntity(transporter.locationManagerProp, transporter.locationManager.serializeNbt());
						}
					}
				}).setSound(getDefaultSound());
	}

	public void tickEditBoxes() {
		nameBox.tick();
		xCoordinateBox.tick();
		yCoordinateBox.tick();
		zCoordinateBox.tick();
	}

	public void updateButtons(boolean status) {
		nameBox.visible = status;
		xCoordinateBox.visible = status;
		yCoordinateBox.visible = status;
		zCoordinateBox.visible = status;
		for (int i = 0; i < incButtons.length; i++) {
			incButtons[i].visible = status;
			decButtons[i].visible = status;
		}
		resetLocation.visible = status;
		importFlashdriveData.visible = status;
	}

	public void addRenderingData(GenericScreen<?> screen) {
		screen.addEditBox(nameBox);
		screen.addEditBox(xCoordinateBox);
		screen.addEditBox(yCoordinateBox);
		screen.addEditBox(zCoordinateBox);
		for (int i = 0; i < incButtons.length; i++) {
			screen.addButton(incButtons[i]);
			screen.addButton(decButtons[i]);
		}
		screen.addButton(resetLocation);
		screen.addButton(importFlashdriveData);
	}

	private void sendNameChange(String name) {
		TileTransporter transporter = transporterSupplier.get();
		if(transporter != null) {
			TransporterLocationWrapper wrapper = transporter.locationManager.getLocation(currIndex);
			wrapper.setName(name);
			transporter.getPropertyManager().updateServerBlockEntity(transporter.locationManagerProp, transporter.locationManager.serializeNbt());
		}
	}

	private void sendCoordinateChange(String coordinate, int coord, int currIndex) {
		TileTransporter transporter = transporterSupplier.get();
		if(transporter != null) {
			int val = 0;
			if (coordinate.length() > 0 && !(coordinate.length() == 1 && coordinate.charAt(0) == '-')) {
				val = Integer.valueOf(coordinate);
			}
			TransporterLocationWrapper wrapper = transporter.locationManager.getLocation(currIndex);
			BlockPos pos = wrapper.getDestination();
			switch (coord) {
			case 0: // X
				pos = new BlockPos(val, pos.getY(), pos.getZ());
				break;
			case 1: // Y
				pos = new BlockPos(pos.getX(), val, pos.getZ());
				break;
			case 2: // Z
				pos = new BlockPos(pos.getX(), pos.getY(), val);
				break;
			}
			wrapper.setDestination(pos);
			transporter.getPropertyManager().updateServerBlockEntity(transporter.locationManagerProp, transporter.locationManager.serializeNbt());
		}
	}

	private void handleIncDec(int coord, int sign) {
		TileTransporter transporter = transporterSupplier.get();
		if(transporter != null) {
			TransporterLocationWrapper wrapper = transporter.locationManager.getLocation(currIndex);
			BlockPos pos = wrapper.getDestination();
			switch (coord) {
			case 0:
				pos = pos.offset(sign, 0, 0);
				xCoordinateBox.setValue(pos.getX() + "");
				break;
			case 1:
				pos = pos.offset(0, sign, 0);
				yCoordinateBox.setValue(pos.getY() + "");
				break;
			case 2:
				pos = pos.offset(0, 0, sign);
				zCoordinateBox.setValue(pos.getZ() + "");
				break;
			}
			wrapper.setDestination(pos);
			transporter.getPropertyManager().updateServerBlockEntity(transporter.locationManagerProp, transporter.locationManager.serializeNbt());
		}
	}

	private Consumer<SoundManager> getDefaultSound() {
		return manager -> manager.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_LOUD3.get(), 1.0F, 1.0F));
	}

	private Consumer<SoundManager> getIncDecSound() {
		float pitch = MatterOverdrive.RANDOM.nextFloat(0.9F, 1.1F);
		return manager -> manager.play(SimpleSoundInstance.forUI(SoundRegistry.SOUND_BUTTON_SOFT0.get(), 1.0F, pitch));
	}

	public boolean areEditBoxesActive() {
		return nameBox.isVisible() || xCoordinateBox.isVisible() || yCoordinateBox.isVisible()
				|| zCoordinateBox.isVisible();
	}

}
