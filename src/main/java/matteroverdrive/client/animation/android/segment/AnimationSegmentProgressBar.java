package matteroverdrive.client.animation.android.segment;

public class AnimationSegmentProgressBar extends AnimationSegmentText {

  private int barStep;
  private int barAmount;

  public AnimationSegmentProgressBar(String string, int begin, int animationType) {
    super(string, begin, animationType);
    this.barStep = 1;
    this.barAmount = 1;
    if (string.contains("$bar")) {
      String barInfo = string.substring(string.indexOf("$bar") + "$bar".length() + 1, string.lastIndexOf("]"));
      this.setString(string.substring(0, string.indexOf("$bar")));
      this.barStep = Integer.parseInt(barInfo.split(",")[0]);
      this.barAmount = Integer.parseInt(barInfo.split(",")[1]);
    }
  }

  @Override
  public String getText(int time) {
    return super.getText(time) + (isDone(time) ? getBar(time) : "");
  }

  private String getBar(int time) {
    StringBuilder builder = new StringBuilder("[");
    int relative = Math.min((time - this.getBegin() - this.getLength()) / barStep, barAmount);
    for (int i = 0; i < relative; ++i) {
      builder = builder.append("==");
    }
    for (int i = relative; i < barAmount; ++i) {
      builder = builder.append("   ");
    }
    builder = builder.append("]   ").append(relative).append("/").append(barAmount);
    return builder.toString();
  }

  @Override
  public boolean isAnimationDone(int time) {
    return (time - this.getBegin() - this.getLength()) / barStep > barAmount;
  }

}
