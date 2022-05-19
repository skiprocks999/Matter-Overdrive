package matteroverdrive.core.screen.component.edit_box;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.IScreenWrapper;

public class EditBoxSuppliableName extends EditBoxOverdrive {

	private Supplier<String> suppliedText;
	private boolean firstRender = true;

	public EditBoxSuppliableName(int pX, int pY, int pWidth, int pHeight, IScreenWrapper gui, Supplier<String> text) {
		super(pX, pY, pWidth, pHeight, gui);
		suppliedText = text;
	}

	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		if (firstRender) {
			firstRender = false;
			setValue(suppliedText.get());
		}
		super.renderButton(stack, mouseX, mouseY, partialTick);
	}

}
