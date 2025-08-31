package inf214.portfolioSet1.cityParking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityParking.Request.RequestTypes;

public class RegistratrationNumberStatTest {

  private CityParking sim;
  private List<Valet> valets;
  private List<Request> requests;
  private int numValets;
  private int numParkingSpots;
  private int numSupervisors;
  private int numCameras;
  private List<Car> parkedCars;

  public void setupSimulation() {
    sim = new CityParking(numValets, numSupervisors, numCameras, numParkingSpots);

    valets = sim.getValets();
    requests = new ArrayList<>();

    parkedCars = new ArrayList<>();
  }

  @Test
  public void simpleTest() {
    numValets = 5;
    numParkingSpots = 10;
    numSupervisors = 2;
    numCameras = 2;
    setupSimulation();

    for (int i = 0; i < 10; i++) {
      String name = "BT" + i;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    for (int i = 0; i < 12; i++) {
      String name = "RB" + i + 20;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    for (int i = 0; i < 7; i++) {
      String name = "LD" + i + 50;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    for (int i = 0; i < numValets; i++)
      requests.add(new Request(RequestTypes.END_OF_DAY));

    sim.addRequestsToQueue(requests);

    try (var wd = new ThreadUtils.WatchDog(valets)) { // guard against deadlocks
      ThreadUtils.startAll(valets);
      ThreadUtils.waitForAll(valets);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    assertEquals(10, sim.queryDatabase("BT"));
    assertEquals(12, sim.queryDatabase("RB"));
    assertEquals(7, sim.queryDatabase("LD"));
  }

  private String generateRegNr(Random random) {
    int leftLimit = 65; // letter 'A'
    int rightLimit = 90; // letter 'Z'
    int targetStringLength = 2;
    StringBuilder buffer = new StringBuilder(targetStringLength);
    int randomLimitedInt;
    for (int i = 0; i < targetStringLength; i++) {
      randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    return buffer.toString();
  }

  @Test
  @Timeout(5)
  void parkingConsistencyTest() {
    numValets = 20;
    numParkingSpots = 100;
    numSupervisors = 12;
    numCameras = 15;
    setupSimulation();

    List<Request> pickUpList = new ArrayList<>();
    List<Request> parkCarList = new ArrayList<>();

    Random randRegNr = new Random(42);
    Random randScenario = new Random(43);
    int numParkedCars = 0;

    valets = sim.getValets();

    Map<String, Integer> actual = new HashMap<>();

    for (int i = 0; i < 2000; i++) {
      String name = generateRegNr(randRegNr);
      actual.putIfAbsent(name, 0);
      actual.computeIfPresent(name, (k, v) -> v + 1);
      Car car = new Car(name + i);
      parkedCars.add(car);
      parkCarList.add(new Request(RequestTypes.PARK, car));
      pickUpList.add(new Request(RequestTypes.PICKUP, car.getRegNr()));
    }

    while (!pickUpList.isEmpty()) {
      if (numParkedCars >= numParkingSpots) {
        requests.add(pickUpList.remove(pickUpList.size() - 1));
        numParkedCars--;
        continue;
      }
      if (randScenario.nextBoolean() && pickUpList.size() > parkCarList.size()) {
        requests.add(pickUpList.remove(pickUpList.size() - 1));
        numParkedCars--;
      } else if (parkCarList.size() > 0) {
        requests.add(parkCarList.remove(parkCarList.size() - 1));
        numParkedCars++;
      } else {
        requests.add(pickUpList.remove(pickUpList.size() - 1));
        numParkedCars--;
      }
    }

    for (int i = 0; i < numValets; i++)
      requests.add(new Request(RequestTypes.END_OF_DAY));

    sim.addRequestsToQueue(requests);

    try (var wd = new ThreadUtils.WatchDog(valets)) { // guard against deadlocks
      ThreadUtils.startAll(valets);
      ThreadUtils.waitForAll(valets);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    for (Map.Entry<String, Integer> entry : actual.entrySet()) {
      assertEquals(entry.getValue(), sim.queryDatabase(entry.getKey()));
    }
  }

  private void addCarRequest(String name) {
    Car car = new Car(name);
    sim.addRequestsToQueue(new Request(RequestTypes.PARK, car));
    sim.addRequestsToQueue(new Request(RequestTypes.PICKUP, name));
  }

  @Test
  public void statCheckDuringDayTest() {
    numValets = 5;
    numParkingSpots = 10;
    numSupervisors = 2;
    numCameras = 2;
    setupSimulation();

    for (int i = 0; i < 10; i++) {
      String name = "BT" + i;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    for (int i = 0; i < 12; i++) {
      String name = "RB" + i + 20;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    for (int i = 0; i < 7; i++) {
      String name = "LD" + i + 50;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    sim.addRequestsToQueue(requests);

    try (var wd = new ThreadUtils.WatchDog(valets)) { // guard against deadlocks
      ThreadUtils.startAll(valets);
      ThreadUtils.delay(1000);
      assertEquals(10, sim.queryDatabase("BT"));
      assertEquals(12, sim.queryDatabase("RB"));
      assertEquals(7, sim.queryDatabase("LD"));

      addCarRequest("BT100");
      addCarRequest("RB101");
      addCarRequest("LD102");
      addCarRequest("BT103");

      for (int i = 0; i < numValets; i++)
        sim.addRequestsToQueue(new Request(RequestTypes.END_OF_DAY));

      ThreadUtils.waitForAll(valets);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    assertEquals(12, sim.queryDatabase("BT"));
    assertEquals(13, sim.queryDatabase("RB"));
    assertEquals(8, sim.queryDatabase("LD"));
  }

  @Test
  void unseenRegNrTest() {
    numValets = 5;
    numParkingSpots = 10;
    numSupervisors = 2;
    numCameras = 2;
    setupSimulation();

    for (int i = 0; i < 10; i++) {
      String name = "BT" + i;
      Car car = new Car(name);
      requests.add(new Request(RequestTypes.PARK, car));
      requests.add(new Request(RequestTypes.PICKUP, name));
      parkedCars.add(car);
    }

    for (int i = 0; i < numValets; i++)
      requests.add(new Request(RequestTypes.END_OF_DAY));

    sim.addRequestsToQueue(requests);

    try (var wd = new ThreadUtils.WatchDog(valets)) { // guard against deadlocks
      ThreadUtils.startAll(valets);
      ThreadUtils.waitForAll(valets);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    assertEquals(10, sim.queryDatabase("BT"));
    assertEquals(0, sim.queryDatabase("BO"));
  }

}
