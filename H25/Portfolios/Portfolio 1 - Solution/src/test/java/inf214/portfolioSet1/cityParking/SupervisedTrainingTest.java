package inf214.portfolioSet1.cityParking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityParking.Request.RequestTypes;

public class SupervisedTrainingTest {

  private CityParking sim;
  private List<Valet> valets;
  private List<Request> requests;
  private int numValets;
  private int numParkingSpots;
  private int numSupervisors;
  private int numCameras;
  private List<Car> parkedCars;
  private List<String> pickedUpCars;

  public void setupSimulation() {
    sim = new CityParking(numValets, numSupervisors, numCameras, numParkingSpots);

    valets = sim.getValets();
    requests = new ArrayList<>();

    parkedCars = new ArrayList<>();
    pickedUpCars = new ArrayList<>();
  }

  @Test
  void parkingLotTest() {
    numParkingSpots = 1;
    ParkingLot lot = new ParkingLot(numParkingSpots);

    assertTrue(lot.reserveParking());
    assertFalse(lot.reserveParking());

    Car car = new Car("BT1");
    assertFalse(lot.isParked("BT1"));

    lot.parkCar(car);
    assertTrue(lot.isParked("BT1"));

    assertEquals(car, lot.pickupCar("BT1"));
    assertTrue(lot.reserveParking());
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

    Map<String, String> parkLog = sim.getParkingLog();

    for (Car car : parkedCars) {
      assertTrue(parkLog.containsKey(car.getRegNr() + ":parking"));
      assertTrue(car.isParked());
      assertFalse(car.isPickedUp());
    }
  }

  @Test
  public void allCarsPickedUpTest() {
    numValets = 5;
    numParkingSpots = 10;
    numSupervisors = 3;
    numCameras = 3;
    setupSimulation();

    for (int j = 0; j < 10; j++) {
      for (int i = numParkingSpots * j; i < numParkingSpots * (j + 1); i++) {
        String name = "BT" + i;
        Car car = new Car(name);
        requests.add(new Request(RequestTypes.PARK, car));
        parkedCars.add(car);
      }

      for (int i = numParkingSpots * j; i < numParkingSpots * (j + 1); i++) {
        String name = "BT" + i;
        requests.add(new Request(RequestTypes.PICKUP, name));
        pickedUpCars.add(name);
      }
    }

    for (int i = 0; i < numValets; i++)
      requests.add(new Request(RequestTypes.END_OF_DAY));

    System.out.println(requests.size());
    sim.addRequestsToQueue(requests);

    try (var wd = new ThreadUtils.WatchDog(valets)) { // guard against deadlocks
      ThreadUtils.startAll(valets);
      ThreadUtils.waitForAll(valets);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    Map<String, String> parkLog = sim.getParkingLog();

    for (Car car : parkedCars) {
      assertTrue(parkLog.containsKey(car + ":parking"));
      assertTrue(parkLog.containsKey(car + ":pickUp"));
      assertTrue(car.isParked());
      assertTrue(car.isPickedUp());
    }
  }

  @Test
  public void induceDeadlockTest() {
    numValets = 2;
    numParkingSpots = 1;
    numSupervisors = 1;
    numCameras = 1;
    setupSimulation();

    valets = sim.getValets();

    for (int i = 0; i < 100; i++) {
      String name = "BT" + i;
      Car car = new Car(name);

      requests.add(new Request(RequestTypes.PICKUP, name));
      requests.add(new Request(RequestTypes.PARK, car));
      parkedCars.add(car);
      pickedUpCars.add(name);
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

    Map<String, String> parkLog = sim.getParkingLog();

    for (Car car : parkedCars) {
      assertTrue(parkLog.containsKey(car + ":parking"));
      assertTrue(parkLog.containsKey(car + ":pickUp"));
      assertTrue(car.isPickedUp());
      assertTrue(car.isParked());
    }
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

    for (int i = 0; i < 1000; i++) {
      String name = generateRegNr(randRegNr) + i;
      Car car = new Car(name);
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

    Map<String, String> parkLog = sim.getParkingLog();

    Set<String> supervisors = new HashSet<>();
    Set<String> cameras = new HashSet<>();
    Set<String> valets = new HashSet<>();

    for (Car car : parkedCars) {
      assertTrue(parkLog.containsKey(car + ":parking"));
      assertTrue(parkLog.containsKey(car + ":pickUp"));
      assertTrue(car.isPickedUp());
      assertTrue(car.isParked());

      String[] log = parkLog.get(car + ":parking").split(":");
      int endVal = log[2].indexOf(",") + 1;
      String valet = log[2].substring(log[2].indexOf("[") + 1, endVal);
      supervisors.add(log[0]);
      cameras.add(log[1]);
      valets.add(valet);
    }

    assertEquals(numSupervisors, supervisors.size(), "We assume that all supervisors will be used, and no more");
    assertEquals(numCameras, cameras.size(), "We assume that all cameras will be used, and no more");
    assertEquals(numValets, valets.size(), "We assume that all valets will be used, and no more");
  }
}
