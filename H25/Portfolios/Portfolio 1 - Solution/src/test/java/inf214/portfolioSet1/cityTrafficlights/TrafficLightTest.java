package inf214.portfolioSet1.cityTrafficlights;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import inf214.portfolioSet1.cityTrafficLights.TrafficLight;

public class TrafficLightTest {

  @Test
  void isUsableTrafficlightTest() {
    TrafficLight light = new TrafficLight(1);

    assertTrue(light.isUsable());
    light.useLight();
    assertFalse(light.isUsable());

    light = new TrafficLight(5);

    for (int i = 0; i < 5; i++) {
      assertTrue(light.isUsable());
      light.useLight();
    }
    assertFalse(light.isUsable());
  }

  @Test
  void resetTrafficlightTest() throws Exception {
    TrafficLight light = new TrafficLight(1);

    assertTrue(light.isUsable());
    light.useLight();
    assertFalse(light.isUsable());
    light.reset();
    assertTrue(light.isUsable());

    light = new TrafficLight(5);
    for (int i = 0; i < 5; i++) {
      assertTrue(light.isUsable());
      light.useLight();
    }
    assertFalse(light.isUsable());

    Field field = TrafficLight.class.getDeclaredField("nDone");

    field.setAccessible(true);
    AtomicInteger nDone = (AtomicInteger) field.get(light);

    assertEquals(5, nDone.get());

    light.reset();

    assertEquals(0, nDone.get());
    field.setAccessible(false);

    light = new TrafficLight(5);
    for (int i = 0; i < 5; i++) {
      assertTrue(light.isUsable());
      light.useLight();
    }
    assertFalse(light.isUsable());
  }

  @Test
  void trafficLightNameTest() throws Exception {

    Field field = TrafficLight.class.getDeclaredField("i");

    field.setAccessible(true);
    field.set(null, 0);

    TrafficLight light0 = new TrafficLight(1);
    TrafficLight light1 = new TrafficLight(1);

    assertEquals(light0.toString(), "trafficLight-0");
    assertEquals(light1.toString(), "trafficLight-1");

    field.set(null, 0);
    field.setAccessible(false);
  }

  @Test
  void trafficLightThrowsTest() {
    TrafficLight light = new TrafficLight(1);

    assertTrue(light.isUsable());
    light.useLight();
    assertFalse(light.isUsable());
    assertThrowsExactly(IllegalStateException.class, () -> {
      light.useLight();
    });
  }
}
