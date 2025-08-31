package inf214.portfolioSet1.cityParking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SupervisorTest {

  @Test
  void superviseParkingTest() {
    CityParking cps = new CityParking(2, 2, 2, 2);
    List<Valet> valets = cps.getValets();

    Valet valet0 = valets.get(0);
    Valet valet1 = valets.get(1);

    Supervisor sup0 = cps.grabSupervisor(valet0);
    Supervisor sup1 = cps.grabSupervisor(valet1);
    Camera camera0 = cps.grabCamera(valet0);
    Camera camera1 = cps.grabCamera(valet1);
    Car car0 = new Car("0");
    Car car1 = new Car("1");

    final PrintStream standardOut = System.out;
    final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));

    sup0.superviseParking(valet0, camera0, car0);
    assertEquals("Supervisor-1: valet-1 parking 0 with Camera-1", outputStreamCaptor.toString().trim());
    outputStreamCaptor.reset();
    sup1.superviseParking(valet1, camera1, car1);
    assertEquals("Supervisor-2: valet-2 parking 1 with Camera-2", outputStreamCaptor.toString().trim());
    outputStreamCaptor.reset();

    System.setOut(standardOut);
  }

  @Test
  void supervisePickupTest() {
    CityParking cps = new CityParking(2, 2, 2, 2);
    List<Valet> valets = cps.getValets();

    Valet valet0 = valets.get(0);
    Valet valet1 = valets.get(1);

    Supervisor sup0 = cps.grabSupervisor(valet0);
    Supervisor sup1 = cps.grabSupervisor(valet1);
    Camera camera0 = cps.grabCamera(valet0);
    Camera camera1 = cps.grabCamera(valet1);
    Car car0 = new Car("0");
    Car car1 = new Car("1");

    final PrintStream standardOut = System.out;
    final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));

    sup0.supervisePickUp(valet0, camera0, car0);
    assertEquals("Supervisor-1: valet-1 pickUp 0 with Camera-1", outputStreamCaptor.toString().trim());
    outputStreamCaptor.reset();
    sup1.supervisePickUp(valet1, camera1, car1);
    assertEquals("Supervisor-2: valet-2 pickUp 1 with Camera-2", outputStreamCaptor.toString().trim());
    outputStreamCaptor.reset();

    System.setOut(standardOut);
  }

  private String removeThreadNumber(Exception e) {
    String em = e.getMessage();
    int start = em.indexOf("["), end = em.indexOf("]");
    return em.substring(0, start) + em.substring(end + 1, em.length());
  }

  @Test
  void superviseParkingThrowsTest() {
    CityParking cps = new CityParking(1, 1, 1, 2);
    List<Valet> valets = cps.getValets();

    Valet valet = valets.get(0);

    Supervisor sup = cps.grabSupervisor(valet);
    Camera camera = cps.grabCamera(valet);
    Car car = new Car("0");

    NullPointerException e1 = assertThrowsExactly(NullPointerException.class, () -> {
      sup.superviseParking(null, camera, car);
    });
    assertEquals("valet: null, camera: Camera-1, car: 0", e1.getMessage());

    e1 = assertThrowsExactly(NullPointerException.class, () -> {
      sup.superviseParking(valet, null, car);
    });
    assertEquals("valet: Thread, camera: null, car: 0", removeThreadNumber(e1));

    e1 = assertThrowsExactly(NullPointerException.class, () -> {
      sup.superviseParking(valet, camera, null);
    });
    assertEquals("valet: Thread, camera: Camera-1, car: null", removeThreadNumber(e1));

    cps.releaseCamera(camera, valet);

    IllegalStateException e2 = assertThrowsExactly(IllegalStateException.class, () -> {
      sup.superviseParking(valet, camera, car);
    });
    assertEquals("Thread hasn't borrowed Camera-1", removeThreadNumber(e2));

    cps.grabCamera(valet);
    cps.releaseSupervisor(sup, valet);

    e2 = assertThrowsExactly(IllegalStateException.class, () -> {
      sup.superviseParking(valet, camera, car);
    });
    assertEquals("Supervisor-1 isn't helping Thread right now", removeThreadNumber(e2));

  }
}
