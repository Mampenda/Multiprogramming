package inf214.portfolioSet1.cityParking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityParking.Request.RequestTypes;

public class Demo {

  private static int numValets = 5;
  private static int numSupervisors = 2;
  private static int numCameras = 3;
  private static int numParkingSpots = 20;

  public static void main(String[] args) {
    runDemoSimulation();
  }

  public static void runDemoSimulation() {
    CityParking sim = new CityParking(numValets, numSupervisors, numCameras, numParkingSpots);

    List<Request> allOrders = new ArrayList<>();

    // Park 10 cars
    for (int i = 1; i <= 10; i++)
      allOrders.add(new Request(RequestTypes.PARK, new Car("BT" + i)));
    // Pickup 5 cars
    for (int i = 1; i <= 5; i++)
      allOrders.add(new Request(RequestTypes.PICKUP, "BT" + i));
    // Park another 10
    for (int i = 11; i <= 20; i++)
      allOrders.add(new Request(RequestTypes.PARK, new Car("RB" + i)));

    // Add END_OF_DAY orders for all threads.
    for (int i = 1; i <= numValets; i++)
      allOrders.add(new Request(RequestTypes.END_OF_DAY));

    try (var wd = new ThreadUtils.WatchDog(sim.getValets())) { // guard against deadlocks
      // all the valets will start parking cars
      ThreadUtils.startAll(sim.getValets());

      // in the main thread, we slowly add cars to the queue
      for (int i = 0; i < allOrders.size(); i++) {
        sim.addRequestsToQueue(allOrders.get(i));
        try {
          Thread.sleep(ThreadLocalRandom.current().nextInt(50) + 1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // wait until all threads are done
      ThreadUtils.waitForAll(sim.getValets());
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
  }
}
