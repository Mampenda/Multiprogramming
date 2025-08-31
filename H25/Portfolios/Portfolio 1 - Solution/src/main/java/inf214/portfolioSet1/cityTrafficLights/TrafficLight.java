package inf214.portfolioSet1.cityTrafficLights;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TrafficLight objects.
 * 
 * DO NOT make changes to this class!
 */
public class TrafficLight {

  private static int i; // for naming

  /**
   * How many times can a traffic light be used in a day?
   */
  private final int useLimit;

  /**
   * Number of times used in a day?
   */
  private AtomicInteger nDone = new AtomicInteger(0);

  /**
   * Name, for debug purposes
   */
  String name;

  public TrafficLight(int useLimit) {
    this.name = "trafficLight-" + i++;
    this.useLimit = useLimit;
  }

  public void reset() {
    nDone.set(0);
  }

  public boolean isUsable() {
    return nDone.get() < useLimit;
  }

  public void useLight() {
    // The AtomicInteger is thread-safe. We increment its value, and check what the
    // previous value was:
    //
    // This is a very simple form of lock, based on the atomic test-and-set
    // instruction which is typically available in hardware.
    if (nDone.getAndIncrement() >= useLimit) {
      throw new IllegalStateException(this + " has been used to many times!");
    }
  }

  public String toString() {
    return name;
  }
}
