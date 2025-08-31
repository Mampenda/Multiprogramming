package inf214.portfolioSet1.cityParking;

public class Supervisor extends Reservable<Valet, Supervisor> {

  CityParking cps;

  public Supervisor(String name, CityParking cps) {
    super(name);
    this.cps = cps;
  }

  private void supervise(Valet valet, Camera camera, Car car) {
    if (valet == null || camera == null || car == null)
      throw new NullPointerException("valet: " + valet + ", camera: " + camera + ", car: " + car);
    if (!isReservedBy(valet))
      throw new IllegalStateException(this + " isn't helping " + valet + " right now");
    if (!camera.isReservedBy(valet))
      throw new IllegalStateException(valet + " hasn't borrowed " + camera);
  }

  public void superviseParking(Valet valet, Camera camera, Car car) {
    supervise(valet, camera, car);
    cps.logParking("parking", valet, this, camera, car);
  }

  public void supervisePickUp(Valet valet, Camera camera, Car car) {
    supervise(valet, camera, car);
    cps.logParking("pickUp", valet, this, camera, car);
  }
}
