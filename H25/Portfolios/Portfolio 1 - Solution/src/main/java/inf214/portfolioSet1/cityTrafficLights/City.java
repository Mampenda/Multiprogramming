package inf214.portfolioSet1.cityTrafficLights;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import inf214.portfolioSet1.ThreadUtils;

public class City {

  /**
   * A1 (all methods using TrafficLight must also be updated to use Intersection)
   *
   * This class represents an intersection with a traffic light.
   *
   * It contains a TrafficLight object and provides synchronized methods to reset, check availability, get, and set the
   * traffic light.
   *
   * It ensures thread-safe access to the traffic light by wrapping around each traffic light so that different
   * threads can access different traffic lights simultaneously.
   */
  private class Intersection {

    TrafficLight trafficLight; // Traffic light field

    // Intersection constructor
    public Intersection(int useLimit) {
      this.trafficLight = new TrafficLight(useLimit);
    }

    // Synchronized functions for traffic light
    public synchronized void reset() {
      this.trafficLight.reset();
    }
    public synchronized boolean isAvailable() {
      return this.trafficLight != null;
    }

    // Getter and setter for traffic lights
    public synchronized TrafficLight getTrafficLight() {
      TrafficLight tmp = trafficLight;
      trafficLight = null;
      return tmp;
    }

    public synchronized void setTrafficLight(TrafficLight trafficLight) {
      this.trafficLight = trafficLight;
    }
  }

  // List of intersections (instead of lights)
  private final List<Intersection> intersections = new ArrayList<>();

  // Fields for managing access to the city
  private int timeDelay;
  private int day = 0;

  // Lock for special vehicle access (blocks ordinary vehicles when special vehicle is inside)
  private Lock lock = new ReentrantLock();

  // Atomic variables for thread-safe access in task A2
  private AtomicInteger numWorkersInside = new AtomicInteger(0);
  private AtomicBoolean specialAccess = new AtomicBoolean(false);

  // City constructor
  public City(int size, int useLimit, int timeDelay) {
    for (int i = 0; i < size; i++) {
      Intersection is = new Intersection(useLimit);
      intersections.add(is);
    }
    this.timeDelay = timeDelay;
  }

  //At the start of each day, we reset the trafficlights
  public synchronized void startDay() {
    for (Intersection intersection : intersections) {
      intersection.reset();
    }
    day++;
  }


  /** ==== Methods from Task A.1 ==== **/

  // Getters and setter
  public synchronized TrafficLight getTrafficLight(int i) {
    ThreadUtils.delay(timeDelay);
    return intersections.get(i).getTrafficLight();
  }

  public void setTrafficLight(int i, TrafficLight trafficLight) {
    ThreadUtils.delay(timeDelay);
    intersections.get(i).setTrafficLight(trafficLight);
  }

  public int getCitySize() {
    return intersections.size();
  }

  // Check if a traffic light is available
  public boolean isAvailable(int trafficLight) {
    return intersections.get(trafficLight).isAvailable();
  }


  /** ==== Methods from Task A.2 ==== **/

  // Ordinary vehicle request and release access
  public void requestAccess() {
    numWorkersInside.incrementAndGet();
    if (specialAccess.get()) {
      numWorkersInside.decrementAndGet();

      lock.lock();
      lock.unlock();
      numWorkersInside.incrementAndGet();
    }
  }

  public void releaseAccess() {
    numWorkersInside.decrementAndGet();
  }

  // Special vehicle request and release access
  public void requestSpecialAccess() {
    lock.lock();
    specialAccess.set(true);
  }

  public void releaseSpecialAccess() {
    specialAccess.set(false);
    lock.unlock();
  }

  // Check if city is empty
  public boolean isCityEmpty() {
    return (numWorkersInside.get() == 0);
  }

  // Logging function
  public void logAccess(String s) {
    System.out.printf("[Day %2d] %s%n", day, s);
  }
}
