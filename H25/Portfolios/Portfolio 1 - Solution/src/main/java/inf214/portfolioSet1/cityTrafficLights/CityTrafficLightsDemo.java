package inf214.portfolioSet1.cityTrafficLights;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

import inf214.portfolioSet1.ThreadUtils;

public class CityTrafficLightsDemo {

  /**
   * How large the city is.
   */
  public static final int CITYSIZE = 25;
  /**
   * Number of times a trafficLight can be used before running out of power.
   */
  public static final int USE_LIMIT = 5;
  /**
   * Number of attempts a Ordinary vehicle will make before giving up.
   */
  public static final int ATTEMPT_LIMIT = 10;
  /**
   * Number of times a trafficLight a Ordinary vehicle will have to use
   * succesfully.
   */
  public static final int NUM_JOBS = 5;
  /**
   * Number of cars that will attempt to travel.
   */
  public static final int NUM_ORD_VEHICLES = 5;
  /**
   * Number of emergency vehicles that will travel in a day.
   * 
   * This will always be one.
   */
  public static final int NUM_EMERGENCY_VEHICLES = 1;

  public static final int TOTAL_DAYS = 2;

  CyclicBarrier barrier;
  City city;

  public boolean logging = true;

  private List<Vehicle> workers = new ArrayList<Vehicle>();
  private int day = 0;

  public static void main(String[] args) throws InterruptedException {
    var sim = new CityTrafficLightsDemo();
    sim.makeSharedResource(CITYSIZE, ATTEMPT_LIMIT, USE_LIMIT);
    sim.makeBarrier(NUM_ORD_VEHICLES + NUM_EMERGENCY_VEHICLES);
    sim.makeWorkers(NUM_ORD_VEHICLES, NUM_JOBS, ATTEMPT_LIMIT);
    long t = System.currentTimeMillis();
    sim.runSimulation();
    t = System.currentTimeMillis() - t;
    System.out.printf("Complete in %.3f s%n", t / 1000.0);
  }

  private void makeSharedResource(int size, int attempts, int usesPerDay) {
    this.city = new City(size, usesPerDay, 100);
  }

  private void makeBarrier(int numVehicles) {
    // the barrier is a synchronization point for all the car threads, so they
    // all start the "day" at the same time
    barrier = new CyclicBarrier(numVehicles, () -> {
      // this gets executed when all the cars are ready for a new day
      ++day;
      log("-----------------------------------------");
      city.startDay();
    });
  }

  private List<Vehicle> makeWorkers(int numVehicles, int numTrafficLights, int numAttempts) {
    for (int i = 0; i < numVehicles; i++) {
      workers
          .add(new OrdinaryVehicle("NormalWorker-" + i, this.city, numAttempts, numTrafficLights, TOTAL_DAYS, barrier));
    }
    // This is commented out for the first part.
    workers.add(new EmergencyVehicle("SpecialWorker-" + 1, this.city, TOTAL_DAYS, barrier));
    return workers;
  }

  private void runSimulation() {
    // the watchdog will stop the simulation if it detects a deadlock
    try (var wd = new ThreadUtils.WatchDog(workers, barrier)) {
      // start all threads
      ThreadUtils.startAll(workers);
      // wait for them to finish
      ThreadUtils.waitForAll(workers);
      log("");
      System.out.println("Overall success: " + workers.stream()
          .map(p -> (p.getName() + ": " + p.successfulPercent() + "%")).collect(Collectors.joining(",  ")));

    } catch (InterruptedException e) {
    }
  }

  private void log(String s) {
    if (logging)
      System.out.printf("[Day %2d] %s%n", day, s);
  }
}
