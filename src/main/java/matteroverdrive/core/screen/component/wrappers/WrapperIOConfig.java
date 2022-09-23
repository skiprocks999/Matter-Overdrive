package matteroverdrive.core.screen.component.wrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.packet.NetworkHandler;
import matteroverdrive.core.packet.type.serverbound.misc.PacketUpdateCapabilitySides;
import matteroverdrive.core.packet.type.serverbound.misc.PacketUpdateCapabilitySides.CapabilityType;
import matteroverdrive.core.screen.GenericScreen;
import matteroverdrive.core.screen.component.button.ButtonIO;
import matteroverdrive.core.screen.component.button.ButtonIO.BlockSide;
import matteroverdrive.core.screen.component.button.ButtonIO.IOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class WrapperIOConfig {

	private Supplier<HashSet<Direction>> inputDirs;
	private Supplier<HashSet<Direction>> outputDirs;
	private Supplier<Boolean> input;
	private Supplier<Boolean> output;
	private Supplier<BlockPos> position;
	private ButtonIO[] buttons = new ButtonIO[6];
	private final CapabilityType type;
	public GenericScreen<?> gui;
	private int guiWidth;
	private int guiHeight;

	public WrapperIOConfig(GenericScreen<?> gui, int guiWidth, int guiHeight, Supplier<HashSet<Direction>> inputDirs,
			Supplier<HashSet<Direction>> outputDirs, Supplier<Boolean> input, Supplier<Boolean> output,
			Supplier<BlockPos> pos, CapabilityType type) {
		this.gui = gui;
		this.guiWidth = guiWidth;
		this.guiHeight = guiHeight;
		this.type = type;
		this.inputDirs = inputDirs;
		this.outputDirs = outputDirs;
		this.input = input;
		this.output = output;
		position = pos;
	}

	public void displayTooltip(PoseStack stack, Component tooltip, int xAxis, int yAxis) {
		gui.renderTooltip(stack, tooltip, xAxis, yAxis);
	}

	public void hideButtons() {
		for (ButtonIO button : buttons) {
			button.visible = false;
		}
	}

	public void showButtons() {
		for (ButtonIO button : buttons) {
			button.visible = true;
		}
	}

	public void initButtons() {
		buttons[0] = new ButtonIO(guiWidth + 19, guiHeight, () -> getModeForButton(BlockSide.TOP), BlockSide.TOP, this,
				input, output);
		buttons[1] = new ButtonIO(guiWidth + 38, guiHeight + 19, () -> getModeForButton(BlockSide.RIGHT),
				BlockSide.RIGHT, this, input, output);
		buttons[2] = new ButtonIO(guiWidth + 38, guiHeight + 38, () -> getModeForButton(BlockSide.BACK), BlockSide.BACK,
				this, input, output);
		buttons[3] = new ButtonIO(guiWidth + 19, guiHeight + 19, () -> getModeForButton(BlockSide.FRONT),
				BlockSide.FRONT, this, input, output);
		buttons[4] = new ButtonIO(guiWidth + 19, guiHeight + 38, () -> getModeForButton(BlockSide.BOTTOM),
				BlockSide.BOTTOM, this, input, output);
		buttons[5] = new ButtonIO(guiWidth, guiHeight + 19, () -> getModeForButton(BlockSide.LEFT), BlockSide.LEFT,
				this, input, output);
	}

	public ButtonIO[] getButtons() {
		return buttons;
	}

	public void childPressed() {
		boolean hasInput = input.get();
		List<Direction> inputs = null;
		if (hasInput) {
			inputs = new ArrayList<>();
			for (ButtonIO button : buttons) {
				if (button.mode == IOMode.INPUT) {
					inputs.add(button.side.mappedDir);
				}
			}
		}
		boolean hasOutput = output.get();
		List<Direction> outputs = null;
		if (hasOutput) {
			outputs = new ArrayList<>();
			for (ButtonIO button : buttons) {
				if (button.mode == IOMode.OUTPUT) {
					outputs.add(button.side.mappedDir);
				}
			}
		}
		NetworkHandler.sendToServer(
				new PacketUpdateCapabilitySides(position.get(), type, hasInput, hasOutput, inputs, outputs));
	}

	private IOMode getModeForButton(BlockSide side) {
		Direction mappedDir = side.mappedDir;
		Set<Direction> inDirs = inputDirs.get();
		if (inDirs != null && inDirs.contains(mappedDir)) {
			return IOMode.INPUT;
		}
		Set<Direction> outDirs = outputDirs.get();
		if (outDirs != null && outDirs.contains(mappedDir)) {
			return IOMode.OUTPUT;
		}
		return IOMode.NONE;
	}

}
