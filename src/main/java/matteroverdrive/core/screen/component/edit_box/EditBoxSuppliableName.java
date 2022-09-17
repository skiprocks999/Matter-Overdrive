package matteroverdrive.core.screen.component.edit_box;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.core.screen.GenericScreen;

public class EditBoxSuppliableName extends EditBoxOverdrive {

	private Supplier<String> suppliedText;
	private boolean firstRender = true;

	public EditBoxSuppliableName(EditBoxTextures texture, GenericScreen<?> gui, int x, int y, int width, int height, Supplier<String> text) {
		super(texture, gui, x, y, width, height);
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
