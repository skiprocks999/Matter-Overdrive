package matteroverdrive.core.android.api.gui;

import com.mojang.blaze3d.platform.Window;

import matteroverdrive.client.ClientReferences;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.core.android.api.ICapabilityAndroid;
import net.minecraft.client.Minecraft;

public abstract class HudElement implements IHudElement{

  protected Minecraft mc;
  protected String name;
  protected int posX;
  protected int posY;
  protected int width;
  protected int height;
  protected Colors baseColor;
  protected float backgroundAlpha;

  public HudElement(String name, int width, int height) {
    this.name = name;
    this.width = width;
    this.height = height;
    mc = Minecraft.getInstance();
    baseColor = ClientReferences.Colors.HOLO;
  }

  @Override
  public int getWidth(Window resolution, ICapabilityAndroid androidPlayer) {
    return width;
  }

  @Override
  public int getHeight(Window resolution, ICapabilityAndroid androidPlayer) {
    return height;
  }

  @Override
  public void setX(int x) {
    this.posX = x;
  }

  @Override
  public void setY(int y) {
    this.posY = y;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setBaseColor(Colors color) {
    this.baseColor = color;
  }

  public abstract HudPosition getPosition();

  public void setBackgroundAlpha(float alpha) {
    this.backgroundAlpha = alpha;
  }
}
