package inf214.portfolioSet1.cityTrafficlights;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.Test;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityTrafficLights.City;
import inf214.portfolioSet1.cityTrafficLights.OrdinaryVehicle;
import inf214.portfolioSet1.cityTrafficLights.TrafficLight;
import inf214.portfolioSet1.cityTrafficLights.Vehicle;

public class CityTest {

  @Test
  void isAvailableTest() {
    City city = new City(1, 1, 0);

    assertTrue(city.isAvailable(0));

    TrafficLight tf = city.getTrafficLight(0);
    assertFalse(city.isAvailable(0));

    city.setTrafficLight(0, tf);
    assertTrue(city.isAvailable(0));
  }

  @Test
  void isNullTest() {
    City city = new City(1, 1, 0);

    TrafficLight tf1 = city.getTrafficLight(0);
    assertNotNull(tf1);
    assertNull(city.getTrafficLight(0));

    city.setTrafficLight(0, tf1);
    assertNotNull(city.getTrafficLight(0));
  }

  @Test
  void getCitySizeTest() {
    City city = new City(1, 1, 0);
    assertEquals(1, city.getCitySize());

    city = new City(1024, 1, 0);
    assertEquals(1024, city.getCitySize());

    city.startDay();
    assertEquals(1024, city.getCitySize());
  }

  @Test
  void startDayTest() {
    City city = new City(10, 5, 0);

    for (int i = 0; i < 10; i++) {
      TrafficLight tl = city.getTrafficLight(i);

      for (int j = 0; j < 5; j++) {
        assertTrue(tl.isUsable());
        tl.useLight();
      }
      assertFalse(tl.isUsable());
      city.setTrafficLight(i, tl);
    }

    city.startDay();

    for (int i = 0; i < 10; i++) {
      TrafficLight tl = city.getTrafficLight(i);

      for (int j = 0; j < 5; j++) {
        assertTrue(tl.isUsable());
        tl.useLight();
      }
      assertFalse(tl.isUsable());
      city.setTrafficLight(i, tl);
    }
  }

  @Test
  void dayIncrementTest() {
    City city = new City(10, 5, 0);

    final PrintStream standardOut = System.out;
    final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));

    for (int i = 0; i < 10; i++) {
      city.logAccess("");
      assertEquals("[Day  " + i + "]", outputStreamCaptor.toString().trim());
      outputStreamCaptor.reset();
      city.startDay();
    }
    System.setOut(standardOut);
  }

  private List<Vehicle> makeVehicles(City city, int numNormVehicles, int numJobs, int numAttempts, int days,
      CyclicBarrier barrier) {
    List<Vehicle> vehicles = new ArrayList<>();
    for (int i = 0; i < numNormVehicles; i++) {
      vehicles.add(new OrdinaryVehicle("NormalWorker-" + i, city, numAttempts, numJobs, days, barrier));
    }
    return vehicles;
  }

  private CyclicBarrier makeBarrier(int numVehicles, City city) {
    // the barrier is a synchronization point for all the vehicle threads, so they
    // all start the "day" at the same time
    CyclicBarrier barrier = new CyclicBarrier(numVehicles, () -> {
      // this gets executed when all the vehicles are ready for a new day
      city.startDay();
    });
    return barrier;
  }

  /**
   * This test might fail some times, so try and rerun it several times. If it
   * continues to fail, there is something wrong with your code.
   */
  @Test
  void demoTest() {
    int numSuccessfulDays = 0;
    int numThreads = 5;
    for (int i = 0; i < 20; i++) {
      int numSuccessfulThreads = 0;
      var city = new City(25, 3, 0);
      CyclicBarrier barrier = makeBarrier(5, city);
      var vehicles = makeVehicles(city, numThreads, 2, 10, 50, barrier);

      final PrintStream standardOut = System.out;
      final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outputStreamCaptor));

      try (var wd = new ThreadUtils.WatchDog(vehicles, barrier)) {
        // start all threads
        ThreadUtils.startAll(vehicles);
        // wait for them to finish
        ThreadUtils.waitForAll(vehicles);
      } catch (InterruptedException e) {
      }

      outputStreamCaptor.reset();
      System.setOut(standardOut);

      for (Vehicle p : vehicles) {
        if (p.successfulPercent() >= 50)
          numSuccessfulThreads++;
      }
      if (numSuccessfulThreads == numThreads) {
        numSuccessfulDays++;
      }
    }
    // Why 12 you ask?
    // Don't ask...
    assertTrue(numSuccessfulDays > 12);
  }
}
