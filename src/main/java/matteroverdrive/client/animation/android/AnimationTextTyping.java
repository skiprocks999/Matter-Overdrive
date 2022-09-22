package matteroverdrive.client.animation.android;

import matteroverdrive.client.animation.android.segment.AnimationSegmentText;

public class AnimationTextTyping extends AnimationTimeline<AnimationSegmentText> {

  public AnimationTextTyping(boolean loopable, int duration) {
    super(loopable, duration);
  }

  public String getString() {
    AnimationSegmentText segment = getCurrentSegment();
    if (segment != null) {
      return segment.getText(time);
    }
    return "";
  }

}
