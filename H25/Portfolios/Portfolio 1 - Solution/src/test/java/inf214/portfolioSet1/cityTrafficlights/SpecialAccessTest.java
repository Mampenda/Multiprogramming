package inf214.portfolioSet1.cityTrafficlights;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.Test;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityTrafficLights.City;
import inf214.portfolioSet1.cityTrafficLights.TrafficLight;

public class SpecialAccessTest {

  @Test
  void restrictedAccessTest() {
    City rs = new City(1, 1, 0);
    rs.requestSpecialAccess();

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
      rs.requestAccess();
      light.setLight(rs.getTrafficLight(0));
    });

    t1.start();

    ThreadUtils.delay(500);
    assertNull(light.getLight());

    rs.releaseSpecialAccess();
    ThreadUtils.delay(500);
    assertNotNull(light.getLight());
  }

  // Add test to check that special vehicle can't access city until empty
  @Test
  void isCityEmptyTest() {
    ReentrantLock lock = new ReentrantLock();
    int numTrafficLights = 256;
    int numthrds = 256;

    City city = new City(numTrafficLights, 1, 0);
    List<IllegalStateException> caught = Collections.synchronizedList(new ArrayList<>());
    city.startDay();
    List<Thread> threads = new ArrayList<>();
    final int numAccesses = numTrafficLights / numthrds;

    for (int i = 0; i < numthrds; i++) {
      final int y = i;
      threads.add(new Thread(() -> {
        try {
          for (int j = y * numAccesses; j < (y + 1) * numAccesses; j++) {
            city.requestAccess();
            try {
              lock.lock();
            } finally {
              lock.unlock();
            }
            TrafficLight tf = city.getTrafficLight(j);

            if (tf != null) {
              if (tf.isUsable()) {
                tf.useLight();
              }
              city.setTrafficLight(j, tf);
            }
            city.releaseAccess();
          }
        } catch (IllegalStateException e) {
          caught.add(e);
        }
      }));
    }

    try {
      lock.lock();
      ThreadUtils.startAll(threads);
      ThreadUtils.delay(200);

      assertFalse(city.isCityEmpty());
      lock.unlock();

      ThreadUtils.waitForAll(threads);
      assertTrue(city.isCityEmpty());

    } finally {
    }
  }

  @Test
  void waitForAccessTest() throws InterruptedException {
    class correctTermination {
      private boolean firstCheck = false;
      private boolean secondCheck = false;

      void setFirst() {
        firstCheck = true;
      }

      void setSecond() {
        secondCheck = true;
      }

      boolean getChecked() {
        return firstCheck && secondCheck;
      }
    }

    ReentrantLock lock = new ReentrantLock();
    int numTrafficLights = 256;
    int numthrds = 256;

    City city = new City(numTrafficLights, 1, 0);
    List<IllegalStateException> caught = Collections.synchronizedList(new ArrayList<>());
    city.startDay();
    List<Thread> threads = new ArrayList<>();
    final int numAccesses = numTrafficLights / numthrds;

    for (int i = 0; i < numthrds; i++) {
      final int y = i;
      threads.add(new Thread(() -> {
        try {
          for (int j = y * numAccesses; j < (y + 1) * numAccesses; j++) {
            city.requestAccess();
            try {
              lock.lock();
            } finally {
              lock.unlock();
            }
            TrafficLight tf = city.getTrafficLight(j);

            if (tf != null) {
              if (tf.isUsable()) {
                tf.useLight();
              }
              city.setTrafficLight(j, tf);
            }
            city.releaseAccess();
          }
        } catch (IllegalStateException e) {
          caught.add(e);
        }
      }));
    }

    final correctTermination ct = new correctTermination();

    Thread t = new Thread(() -> {
      try {
        lock.lock();
        ThreadUtils.delay(200);

        if (!city.isCityEmpty())
          ct.setFirst();

        lock.unlock();

        while (!city.isCityEmpty())
          Thread.onSpinWait();

        if (city.isCityEmpty())
          ct.setSecond();

      } finally {
      }
    });

    t.start();
    ThreadUtils.delay(100);
    ThreadUtils.startAll(threads);
    ThreadUtils.waitForAll(threads);
    t.join();

    assertTrue(ct.getChecked());
  }
}
