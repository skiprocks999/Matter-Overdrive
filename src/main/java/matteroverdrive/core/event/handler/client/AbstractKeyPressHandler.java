package matteroverdrive.core.event.handler.client;

import net.minecraft.client.Minecraft;

public abstract class AbstractKeyPressHandler {

	public AbstractKeyPressHandler() {

	}

	public abstract void handleKeyPress(Minecraft minecraft, int scanCode, int key, int action);

}
