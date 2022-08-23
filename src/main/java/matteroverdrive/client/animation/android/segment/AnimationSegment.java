package matteroverdrive.client.animation.android.segment;

public class AnimationSegment {

  private int begin;
  private int length;

  public AnimationSegment(int begin, int length) {
    this.begin = begin;
    this.length = length;
  }

  public AnimationSegment(int length) {
    this.length = length;
  }

  public int getBegin() {
    return begin;
  }

  public void setBegin(int begin) {
    this.begin = begin;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public boolean isDone(int time) {
    return (time - this.getBegin()) >= this.getLength();
  }

  public boolean isAnimationDone(int time) {
    return isDone(time);
  }

}
