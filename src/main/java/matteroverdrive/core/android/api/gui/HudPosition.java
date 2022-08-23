package matteroverdrive.core.android.api.gui;

public enum HudPosition {

  TOP_LEFT(0, 0, "Top Left"),
  TOP_CENTER(0.5f, 0, "Top Center"),
  TOP_RIGHT(1, 0, "Top Right"),
  MIDDLE_LEFT(0, 0.5f, "Middle Left"),
  MIDDLE_CENTER(0.5f, 0.5f, "Middle Center"),
  MIDDLE_RIGHT(1f, 0.5f, "Middle Right"),
  BOTTOM_LEFT(0, 1, "Bottom Left"),
  BOTTOM_CENTER(0.5f, 1, "Bottom Center"),
  BOTTOM_RIGHT(1, 1, "Bottom Right");

  private final float x;
  private final float y;
  private final String name;

  private HudPosition(float x, float y, String name) {
    this.x = x;
    this.y = y;
    this.name = name;
  }

  public static String[] getNames() {
    String[] names = new String[values().length];
    for (int i = 0; i < values().length; i++) {
      names[i] = values()[i].name;
    }
    return names;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public String getName() {
    return name;
  }

}
