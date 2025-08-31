package inf214.portfolioSet1.cityParking;

public class Car {
  private final String name;
  private boolean parked = false, pickedUp = false;

  public Car(String name) {
    this.name = name;
  }

  public void park() {
    this.parked = true;
  }

  public void pickUp() {
    this.pickedUp = true;
  }

  public String getRegNr() {
    return name;
  }

  public boolean isParked() {
    return this.parked;
  }

  public boolean isPickedUp() {
    return this.pickedUp;
  }

  public String toString() {
    return name;
  }
}
