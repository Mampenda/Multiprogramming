package inf214.portfolioSet1.cityTrafficlights;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import inf214.portfolioSet1.cityTrafficLights.OrdinaryVehicle;
import inf214.portfolioSet1.cityTrafficLights.Vehicle;
import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityTrafficLights.City;
import inf214.portfolioSet1.cityTrafficLights.TrafficLight;

public class LockedForSafetyTest {

  @Test
  void TrafficlightSyncTest() {
    int numTrafficLights = 2_048;
    int numthrds = 256;

    City city = new City(numTrafficLights, 1, 0);
    List<IllegalStateException> caught = Collections.synchronizedList(new ArrayList<>());
    city.startDay();
    List<Thread> threads = new ArrayList<>();

    for (int i = 0; i < numthrds; i++) {
      threads.add(new Thread(() -> {
        try {
          for (int j = 0; j < city.getCitySize(); j++) {
            TrafficLight tf = city.getTrafficLight(j);

            if (tf != null) {
              if (tf.isUsable()) {
                tf.useLight();
              }
              city.setTrafficLight(j, tf);
            }
          }
        } catch (IllegalStateException e) {
          caught.add(e);
        }
      }));
    }
    ThreadUtils.startAll(threads);
    ThreadUtils.waitForAll(threads);

    if (!caught.isEmpty())
      throw caught.get(0);
  }

  @Test
  @Timeout(10)
  void trafficlightSpeedTest() {
    int numTrafficLights = 8_192;
    int numthrds = 1024;

    City city = new City(numTrafficLights, 1, 0);
    List<IllegalStateException> caught = Collections.synchronizedList(new ArrayList<>());
    city.startDay();
    List<Thread> threads = new ArrayList<>();

    for (int i = 0; i < numthrds; i++) {
      threads.add(new Thread(() -> {
        try {
          for (int j = 0; j < city.getCitySize(); j++) {
            TrafficLight tf = city.getTrafficLight(j);

            if (tf != null) {
              if (tf.isUsable()) {
                tf.useLight();
              }
              city.setTrafficLight(j, tf);
            }
          }
        } catch (IllegalStateException e) {
          caught.add(e);
        }
      }));
    }
    ThreadUtils.startAll(threads);
    ThreadUtils.waitForAll(threads);

    if (!caught.isEmpty())
      throw caught.get(0);

    for (int i = 0; i < numTrafficLights; i++) {
      assertFalse(city.getTrafficLight(i).isUsable());
    }
  }

  @Test
  void trafficLightDelayTest() {
    City city = new City(10, 1, 200);

    class Light {
      TrafficLight light;

      void setLight(TrafficLight light) {
        this.light = light;
      }

      TrafficLight getLight() {
        return this.light;
      }
    }

    final Light light = new Light();

    Thread t1 = new Thread(() -> {
      light.setLight(city.getTrafficLight(0));
      ThreadUtils.delay(500);
      city.setTrafficLight(0, light.getLight());
    });

    t1.start();
    ThreadUtils.delay(100);
    assertNull(light.getLight());

    ThreadUtils.delay(200);
    assertNotNull(light.getLight());
    assertNull(city.getTrafficLight(0));

    ThreadUtils.delay(250);
    assertNotNull(city.getTrafficLight(0));
  }

  @Test
  @Timeout(10)
  void trafficlightLocationSpeedTest() {
    int numTrafficLights = 3_072;
    int numthrds = 1024;

    City city = new City(numTrafficLights, 1, 1);
    List<IllegalStateException> caught = Collections.synchronizedList(new ArrayList<>());
    city.startDay();
    List<Thread> threads = new ArrayList<>();
    final int numAccesses = numTrafficLights / numthrds;

    for (int i = 0; i < numthrds; i++) {
      final int y = i;
      threads.add(new Thread(() -> {
        try {
          for (int j = y * numAccesses; j < (y + 1) * numAccesses; j++) {
            TrafficLight tf = city.getTrafficLight(j);

            if (tf != null) {
              if (tf.isUsable()) {
                tf.useLight();
              }
              city.setTrafficLight(j, tf);
            }
          }
        } catch (IllegalStateException e) {
          caught.add(e);
        }
      }));
    }
    ThreadUtils.startAll(threads);
    ThreadUtils.waitForAll(threads);

    if (!caught.isEmpty())
      throw caught.get(0);

    for (int i = 0; i < numTrafficLights; i++) {
      assertFalse(city.getTrafficLight(i).isUsable());
    }
  }

  @Test
  void simultaneousGetTest() {
    int numTrafficLights = 2_048;
    int numthrds = 256;
    City city = new City(numTrafficLights, 1, 0);
    city.startDay();

    AtomicInteger totalGotten = new AtomicInteger(0);
    CyclicBarrier barrier = new CyclicBarrier(numthrds, () -> {
    });

    List<Thread> threads = new ArrayList<>();

    for (int i = 0; i < numthrds; i++) {
      threads.add(new Thread(() -> {
        try {
          for (int j = 0; j < city.getCitySize(); j++) {
            barrier.await();
            TrafficLight tf = city.getTrafficLight(j);

            if (tf != null) {
              totalGotten.incrementAndGet();
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }));
    }

    ThreadUtils.startAll(threads);
    ThreadUtils.waitForAll(threads);

    assertEquals(numTrafficLights, totalGotten.get(), "Some threads has managed to grab the same light");
  }

  @Test
  void lightsArePutBackTest() {
    int numTrafficLights = 1;
    int numAttempts = 1;
    int numDays = 1;
    int numthrds = 1;
    List<Vehicle> vehicles = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(numthrds, () -> {
    });

    City city = new City(numTrafficLights, 1, 0);
    city.startDay();
    // String name, City city, int numAttempts, int numTrafficLights, int numDays,
    // CyclicBarrier barrier
    vehicles.add(new OrdinaryVehicle("OrdinaryVehicle-1", city, numAttempts, numTrafficLights, numDays, barrier));

    try (var wd = new ThreadUtils.WatchDog(vehicles, barrier)) {
      // start all threads
      ThreadUtils.startAll(vehicles);
      // wait for them to finish
      ThreadUtils.waitForAll(vehicles);
      assertTrue(vehicles.get(0).successfulPercent() == 100);

    } catch (InterruptedException e) {
    }

    TrafficLight tf = city.getTrafficLight(0);
    assertNotNull(tf);
    assertFalse(tf.isUsable());
  }
}
