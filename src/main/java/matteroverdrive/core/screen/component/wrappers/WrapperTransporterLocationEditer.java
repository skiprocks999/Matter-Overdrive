package matteroverdrive.core.screen.component.wrappers;

import java.util.function.Consumer;
import java.util.function.Supplier;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.SoundRegister;
import matteroverdrive.common.tile.transporter.TileTransporter;
import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.PacketUpdateTransporterLocationInfo;
import matteroverdrive.core.packet.type.PacketUpdateTransporterLocationInfo.PacketType;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.IScreenWrapper;
import matteroverdrive.core.screen.component.edit_box.EditBoxOverdrive;
import matteroverdrive.core.screen.component.edit_box.EditBoxSuppliableName;
import matteroverdrive.core.screen.component.utils.OverdriveTextureButton;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.core.utils.UtilsText;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WrapperTransporterLocationEditer {

	private EditBoxSuppliableName nameBox;
	private EditBoxOverdrive xCoordinateBox;
	private EditBoxOverdrive yCoordinateBox;
	private EditBoxOverdrive zCoordinateBox;
	private OverdriveTextureButton[] incButtons = new OverdriveTextureButton[3];
	private OverdriveTextureButton[] decButtons = new OverdriveTextureButton[3];
	private OverdriveTextureButton resetLocation;
	private OverdriveTextureButton importFlashdriveData;

	private int xLoc;
	private int yLoc;
	private IScreenWrapper gui;
	private Supplier<TileTransporter> transporterSupplier;
	private int currIndex;
	
	private static final TextComponent PLUS = new TextComponent("+");
	private static final TextComponent MINUS = new TextComponent("-");

	public WrapperTransporterLocationEditer(IScreenWrapper gui, int xLoc, int yLoc, Supplier<TileTransporter> transporterSupplier) {
		this.gui = gui;
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.transporterSupplier = transporterSupplier;
	}
	
	public void setCurrIndex(int index) {
		currIndex = index;
	}
	
	public int getCurrIndex() {
		return currIndex;
	}

	public void initButtons() {
		nameBox = new EditBoxSuppliableName(xLoc + 65, yLoc + 30, 120, 15, gui, () -> {
			return transporterSupplier.get().CLIENT_LOCATIONS[currIndex].getName().getContents();
		});
		nameBox.setTextColor(UtilsRendering.WHITE);
		nameBox.setTextColorUneditable(UtilsRendering.WHITE);
		nameBox.setMaxLength(15);
		nameBox.setResponder(string -> {
			nameBox.setFocus(true);
			xCoordinateBox.setFocus(false);
			yCoordinateBox.setFocus(false);
			zCoordinateBox.setFocus(false);
			sendNameChange(string);
		});
		
		xCoordinateBox = new EditBoxSuppliableName(xLoc + 94, yLoc + 50, 70, 15, gui, () -> {
			return transporterSupplier.get().CLIENT_LOCATIONS[currIndex].getDestination().getX() + "";
		});
		xCoordinateBox.setTextColor(UtilsRendering.WHITE);
		xCoordinateBox.setTextColorUneditable(UtilsRendering.WHITE);
		//the world border is 30M....
		xCoordinateBox.setMaxLength(9);
		xCoordinateBox.setFilter(EditBoxOverdrive.INTEGER_BOX);
		xCoordinateBox.setResponder(string -> {
			nameBox.setFocus(false);
			xCoordinateBox.setFocus(true);
			yCoordinateBox.setFocus(false);
			zCoordinateBox.setFocus(false);
			sendCoordinateChange(string, 0, currIndex, getCurrentPos());
		});
		
		yCoordinateBox = new EditBoxSuppliableName(xLoc + 94, yLoc + 70, 70, 15, gui, () -> {
			return transporterSupplier.get().CLIENT_LOCATIONS[currIndex].getDestination().getY() + "";
		});
		yCoordinateBox.setTextColor(UtilsRendering.WHITE);
		yCoordinateBox.setTextColorUneditable(UtilsRendering.WHITE);
		yCoordinateBox.setMaxLength(9);
		yCoordinateBox.setFilter(EditBoxOverdrive.INTEGER_BOX);
		yCoordinateBox.setResponder(string -> {
			nameBox.setFocus(false);
			xCoordinateBox.setFocus(false);
			yCoordinateBox.setFocus(true);
			zCoordinateBox.setFocus(false);
			sendCoordinateChange(string, 1, currIndex, getCurrentPos());
		});
		
		zCoordinateBox = new EditBoxSuppliableName(xLoc + 94, yLoc + 90, 70, 15, gui, () -> {
			return transporterSupplier.get().CLIENT_LOCATIONS[currIndex].getDestination().getZ() + "";
		});
		zCoordinateBox.setTextColor(UtilsRendering.WHITE);
		zCoordinateBox.setTextColorUneditable(UtilsRendering.WHITE);
		zCoordinateBox.setMaxLength(9);
		zCoordinateBox.setFilter(EditBoxOverdrive.INTEGER_BOX);
		zCoordinateBox.setResponder(string -> {
			nameBox.setFocus(false);
			xCoordinateBox.setFocus(false);
			yCoordinateBox.setFocus(false);
			zCoordinateBox.setFocus(true);
			sendCoordinateChange(string, 2, currIndex, getCurrentPos());
		});
		
		for(int i = 0; i < incButtons.length; i++) {
			final int ref = i;
			incButtons[i] = new OverdriveTextureButton(xLoc + 79, yLoc + 50 + 20 * i, 15, 15, PLUS, button -> {
				handleIncDec(ref, 1);
			}).setLeft().setColor(UtilsRendering.WHITE).setSound(getIncDecSound());
		}
		for(int i = 0; i < decButtons.length; i++) {
			final int ref = i;
			decButtons[i] = new OverdriveTextureButton(xLoc + 164, yLoc + 50 + 20 * i, 15, 15, MINUS, button -> {
				handleIncDec(ref, -1);
			}).setRight().setColor(UtilsRendering.WHITE).setSound(getIncDecSound());
		}
		resetLocation = new OverdriveTextureButton(xLoc + 133, yLoc + 125, 60, 20, UtilsText.gui("resetpos"), button -> {
			nameBox.setValue("");
			xCoordinateBox.setValue("0");
			yCoordinateBox.setValue("-1000");
			zCoordinateBox.setValue("0");
			NetworkHandler.CHANNEL.sendToServer(
					new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.RESET_DESTINATION));
		}).setSound(getDefaultSound());
		
		importFlashdriveData = new OverdriveTextureButton(xLoc + 57, yLoc + 125, 60, 20, UtilsText.gui("importpos"), button -> {
			TileTransporter transporter = transporterSupplier.get();
			ItemStack flashdrive = transporter.clientInventory.getStackInSlot(0);
			if(!flashdrive.isEmpty() && flashdrive.hasTag() && flashdrive.getTag().contains(UtilsNbt.BLOCK_POS)) {
				CompoundTag tag = flashdrive.getTag();
				BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(UtilsNbt.BLOCK_POS));
				ResourceKey<Level> dimension = UtilsNbt.readDimensionFromTag(tag.getCompound(UtilsNbt.DIMENSION));
				xCoordinateBox.setValue(pos.getX() + "");
				yCoordinateBox.setValue(pos.getY() + "");
				zCoordinateBox.setValue(pos.getZ() + "");
				NetworkHandler.CHANNEL.sendToServer(
						new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.UPDATE_DESTINATION, pos, dimension));
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
		for(int i = 0; i < incButtons.length; i++) {
			incButtons[i].visible = status;
			decButtons[i].visible = status;
		}
		resetLocation.visible = status;
		importFlashdriveData.visible = status;
	}
	
	public void addRenderingData(GenericScreen<?> screen) {
		screen.addExternalWidget(nameBox);
		screen.addExternalWidget(xCoordinateBox);
		screen.addExternalWidget(yCoordinateBox);
		screen.addExternalWidget(zCoordinateBox);
		for(int i = 0; i < incButtons.length; i++) {
			screen.addExternalWidget(incButtons[i]);
			screen.addExternalWidget(decButtons[i]);
		}
		screen.addExternalWidget(resetLocation);
		screen.addExternalWidget(importFlashdriveData);
	}
	
	private void sendNameChange(String name) {
		NetworkHandler.CHANNEL.sendToServer(
				new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.UPDATE_NAME, name));
	}
	
	private void sendCoordinateChange(String coordinate, int coord, int currIndex, BlockPos oldPos) {
		int val = 0;
		if(coordinate.length() > 0 && !(coordinate.length() == 1 && coordinate.charAt(0) == '-')) {
			val = Integer.valueOf(coordinate);
		} 
		NetworkHandler.CHANNEL.sendToServer(
				new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.UPDATE_DESTINATION, makeNewPos(coord, val, oldPos)));
	}
	
	private BlockPos getCurrentPos() {
		return transporterSupplier.get().CLIENT_LOCATIONS[currIndex].getDestination();
	}
	
	private BlockPos makeNewPos(int coord, int val, BlockPos oldPos) {
		BlockPos newPos = null;
		switch(coord) {
		case 0: //X
			newPos = new BlockPos(val, oldPos.getY(), oldPos.getZ());
			break;
		case 1: //Y
			newPos = new BlockPos(oldPos.getX(), val, oldPos.getZ());
			break;
		case 2: //Z
			newPos = new BlockPos(oldPos.getX(), oldPos.getY(), val);
			break;
		}
		return newPos;
	}
	
	private void handleIncDec(int coord, int sign) {
		TileTransporter transporter = transporterSupplier.get();
		switch(coord) {
		case 0:
			int newX = transporter.CLIENT_LOCATIONS[currIndex].getDestination().getX() + sign;
			NetworkHandler.CHANNEL.sendToServer(
					new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.UPDATE_DESTINATION, makeNewPos(0, newX, getCurrentPos())));
			xCoordinateBox.setValue(newX + "");
			break;
		case 1:
			int newY = transporter.CLIENT_LOCATIONS[currIndex].getDestination().getY() + sign;
			NetworkHandler.CHANNEL.sendToServer(
					new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.UPDATE_DESTINATION, makeNewPos(1, newY, getCurrentPos())));
			yCoordinateBox.setValue(newY + "");
			break;
		case 2:
			int newZ = transporter.CLIENT_LOCATIONS[currIndex].getDestination().getZ() + sign;
			NetworkHandler.CHANNEL.sendToServer(
					new PacketUpdateTransporterLocationInfo(transporterSupplier.get().getBlockPos(), currIndex, PacketType.UPDATE_DESTINATION, makeNewPos(2, newZ, getCurrentPos())));
			zCoordinateBox.setValue(newZ + "");
			break;
		}
	}
	
	private Consumer<SoundManager> getDefaultSound(){
		return manager -> manager.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_LOUD3.get(), 1.0F, 1.0F));
	}
	
	private Consumer<SoundManager> getIncDecSound(){
		float pitch = MatterOverdrive.RANDOM.nextFloat(0.9F, 1.1F);
		return manager -> manager.play(SimpleSoundInstance.forUI(SoundRegister.SOUND_BUTTON_SOFT0.get(), 1.0F, pitch));
	}
	
	public boolean areEditBoxesActive() {
		return nameBox.isVisible() || xCoordinateBox.isVisible() || yCoordinateBox.isVisible() || zCoordinateBox.isVisible();
	}

}
