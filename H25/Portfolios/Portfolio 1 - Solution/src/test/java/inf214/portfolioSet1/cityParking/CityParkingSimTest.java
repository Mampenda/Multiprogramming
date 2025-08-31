package inf214.portfolioSet1.cityParking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.*;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityParking.Request.RequestTypes;

public class CityParkingSimTest {

  CityParking sim;
  List<Valet> valets;
  List<Request> requests;
  int numValets;
  int numParkingSpots;
  int numSupervisors;
  int numCameras;
  List<String> checkLog;
  List<Car> parkedCars;
  List<String> pickedUpCars;

  // @BeforeEach
  public void setupSimulation() {
    sim = new CityParking(numValets, numSupervisors, numCameras, numParkingSpots);

    valets = sim.getValets();
    requests = new ArrayList<>();

    checkLog = new ArrayList<>();
    parkedCars = new ArrayList<>();
    pickedUpCars = new ArrayList<>();
  }

  @Test
  void parkingQueueTest() {
    numValets = 5;
    numParkingSpots = 20;
    numSupervisors = 2;
    numCameras = 3;
    setupSimulation();

    assertEquals(0, sim.parkingQueue().size());

    Car car;
    for (int i = 1; i <= 10; i++) {
      car = new Car("BT" + i);
      requests.add(new Request(RequestTypes.PARK, car));
      parkedCars.add(car);
    }
    // Pickup 5 cars
    for (int i = 1; i <= 5; i++)
      requests.add(new Request(RequestTypes.PICKUP, "BT" + i));

    // Park another 10
    for (int i = 11; i <= 20; i++)
      requests.add(new Request(RequestTypes.PARK, new Car("RB" + i)));

    // Add END_OF_DAY orders for all threads.
    for (int i = 1; i <= numValets; i++)
      requests.add(new Request(RequestTypes.END_OF_DAY));

    sim.addRequestsToQueue(requests);

    assertEquals(30, sim.parkingQueue().size());

    for (int i = 0; i < 30; i++) {
      Request request = sim.parkingQueue().takeRequest();
      assertEquals(requests.get(i).car(), request.car());
      assertEquals(requests.get(i).type(), request.type());
      assertEquals(requests.get(i).regNr(), request.regNr());
    }

    assertEquals(0, sim.parkingQueue().size());

    sim.addRequestsToQueue(requests);

    assertEquals(30, sim.parkingQueue().size());
    sim.parkingQueue().clear();
    assertEquals(0, sim.parkingQueue().size());
  }

  @Test
  void getValetsTest() {
    numValets = 5;
    numParkingSpots = 20;
    numSupervisors = 2;
    numCameras = 3;
    setupSimulation();

    assertEquals(5, sim.getValets().size());
  }

  @Test
  public void demoTest() {
    numValets = 5;
    numParkingSpots = 20;
    numSupervisors = 2;
    numCameras = 3;
    setupSimulation();

    List<Car> parkedCars = new ArrayList<>();

    Car car;
    for (int i = 1; i <= 10; i++) {
      car = new Car("BT" + i);
      requests.add(new Request(RequestTypes.PARK, car));
      parkedCars.add(car);
    }
    // Pickup 5 cars
    for (int i = 1; i <= 5; i++)
      requests.add(new Request(RequestTypes.PICKUP, "BT" + i));

    // Park another 10
    for (int i = 11; i <= 20; i++)
      requests.add(new Request(RequestTypes.PARK, new Car("RB" + i)));

    // Add END_OF_DAY orders for all threads.
    for (int i = 1; i <= numValets; i++)
      requests.add(new Request(RequestTypes.END_OF_DAY));

    try (var wd = new ThreadUtils.WatchDog(valets)) { // guard against deadlocks
      // all the valets will start parking cars
      ThreadUtils.startAll(valets);

      // in the main thread, we slowly add cars to the queue
      for (int i = 0; i < requests.size(); i++) {
        sim.addRequestsToQueue(requests.get(i));
        try {
          Thread.sleep(ThreadLocalRandom.current().nextInt(50) + 1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // wait until all threads are done
      ThreadUtils.waitForAll(valets);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
  }

}
