package inf214.portfolioSet1.cityTrafficLights;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class EmergencyVehicle extends Vehicle {

  private final City city;
  private int numDays;
  private int successfulDays;
  private CyclicBarrier barrier;

  public EmergencyVehicle(String name, City city, int numDays, CyclicBarrier barrier) {
    super(name);
    this.city = city;
    this.numDays = numDays;
    this.barrier = barrier;
  }

  @Override
  public boolean goToWork() throws InterruptedException {

    Thread.sleep(100);
    log("Requested special access");
    city.requestSpecialAccess();
    while (!city.isCityEmpty()) {
      Thread.onSpinWait();
    }
    log("City is empty");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    log("Performed special access");
    city.releaseSpecialAccess();

    return true;
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < this.numDays; i++) {
        // we will wait for all the other threads to reach this point before proceeding,
        // so that we all start the day at the same time
        barrier.await();
        log("starting day");
        if (goToWork()) {
          log(getName() + " got to the accident :)");
          successfulDays++;
        } else {
          log(getName() + "gave up on getting to the accident :(");
        }
      }
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int successfulPercent() {
    return (int) Math.round(100 * ((double) successfulDays) / numDays);
  }

  private void log(String s) {
    city.logAccess(getName() + ": " + s);
  }

}
