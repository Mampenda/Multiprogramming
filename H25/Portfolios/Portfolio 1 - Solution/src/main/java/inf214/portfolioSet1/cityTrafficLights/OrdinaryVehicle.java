package inf214.portfolioSet1.cityTrafficLights;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class OrdinaryVehicle extends Vehicle {

  private final City city;
  private final CyclicBarrier barrier;

  private final int numTrafficLights;
  private final int numDays;
  private final int numAttempts;

  private int successfulDays;

  public OrdinaryVehicle(String name, City city, int numAttempts, int numTrafficLights, int numDays,
      CyclicBarrier barrier) {
    super(name);
    this.city = city;
    this.numDays = numDays;
    this.numTrafficLights = numTrafficLights;
    this.numAttempts = numAttempts;
    this.barrier = barrier;
  }

  @Override
  public boolean goToWork() throws InterruptedException {
    List<TrafficLight> grabbed = new ArrayList<>();

    for (int i = 0; i < numAttempts; i++) {
      int j = ThreadLocalRandom.current().nextInt(city.getCitySize());

      city.requestAccess();

      if (city.isAvailable(j)) {
        TrafficLight light = city.getTrafficLight(j);

        if (light != null) {
          if (light.isUsable()) {
            grabbed.add(light);
            light.useLight();
            log("got: " + light + " (" + grabbed.size() + " so far today)");
          }
          city.setTrafficLight(j, light);
        } else {
          log("missed: " + j + " attempt: " + i + " :(");
        }

      } else {
        log("missed: " + j + " was not available");
      }

      city.releaseAccess();

      if (grabbed.size() >= numTrafficLights) {
        successfulDays++;
        return true;
      }
    }

    return false;
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < this.numDays; i++) {
        // we will wait for all the other threads to reach this point before proceeding,
        // so that we all start the day at the same time
        barrier.await();
        log("starting day");
        if (goToWork()) {
          log(getName() + " got to work :)");
        } else {
          log(getName() + " gave up on getting to work :(");
        }
      }
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int successfulPercent() {
    return (int) Math.round(100 * ((double) successfulDays) / numDays);
  }

  private void log(String s) {
    city.logAccess(getName() + ": " + s);
  }

}
