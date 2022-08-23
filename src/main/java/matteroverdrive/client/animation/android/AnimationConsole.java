package matteroverdrive.client.animation.android;

import matteroverdrive.client.animation.android.segment.AnimationSegmentText;

public class AnimationConsole extends AnimationTimeline<AnimationSegmentText> {

  public AnimationConsole(boolean loopable, int duration) {
    super(loopable, duration);
  }

  public String getString() {
    StringBuilder str = new StringBuilder();
    for (AnimationSegmentText text : getPreviousSegments())
      str.append(text.getText(time)).append("\n");
    AnimationSegmentText segment = getCurrentSegment();
    if (segment != null) {
      str.append(segment.getText(time)).append("\n");
    }
    boolean areAllDone = true;
    for (AnimationSegmentText animationSegmentText : this.getSegments()) {
      if (!animationSegmentText.isAnimationDone(this.time)) {
        areAllDone = false;
        break;
      }
    }
    if (areAllDone)
      str.append(getFinalSegment().getText(time));
    return str.toString();
  }

}
