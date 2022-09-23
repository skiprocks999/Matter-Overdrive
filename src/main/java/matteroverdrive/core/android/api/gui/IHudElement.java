package matteroverdrive.core.android.api.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.android.api.ICapabilityAndroid;

public interface IHudElement {

  boolean isVisible(ICapabilityAndroid android);

  void drawElement(PoseStack stack, ICapabilityAndroid androidPlayer, Window resolution, float ticks);

  int getWidth(Window resolution, ICapabilityAndroid androidPlayer);

  int getHeight(Window resolution, ICapabilityAndroid androidPlayer);

  void setX(int x);

  void setY(int y);

  void setBaseColor(Colors color);

  void setBackgroundAlpha(float alpha);

  HudPosition getPosition();

  String getName();

}
