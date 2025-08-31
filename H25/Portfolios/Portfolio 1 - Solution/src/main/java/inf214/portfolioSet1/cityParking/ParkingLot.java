package inf214.portfolioSet1.cityParking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class ParkingLot {

  // Number of parking spots in the parking lot
  private final int numParkingSpots;

  // Number of currently parked cars
  private AtomicInteger numParkedCars = new AtomicInteger(0);

  // Map of parked cars, key is the registration number
  private ConcurrentMap<String, Car> parkingLot = new ConcurrentHashMap<String, Car>();

  // Constructor
  public ParkingLot(int numParkingSpots) {
    this.numParkingSpots = numParkingSpots;
  }

  // We can also solve this by synchronizing the function instead of using an AtomicInteger.
  public boolean reserveParking() {

    // Increment the number of parked cars if there is space (i.e., only if it's not at max capacity)
    IntUnaryOperator op = (x) -> {
      if (x < numParkingSpots) {
        return x + 1;
      } else {
        return x;
      }
    };

    // Return true if we successfully reserved a spot
    // (i.e., if the number of parked cars before the update was less than the number of parking spots)
    return (numParkedCars.getAndUpdate(op) < numParkingSpots);
  }

  /**
   * Attempts to park the car in the parking lot. Returns true if there was space
   * and the car is parked, false if the parking lot was full
   *
   * @param car the car to park.
   * @return true/false wheter the car got parked.
   */
  public void parkCar(Car car) {
    car.park();
    parkingLot.put(car.getRegNr(), car);
  }

  public boolean isParked(String regNr) {
    return parkingLot.containsKey(regNr);
  }

  /**
   * Attempts to pick up the car in the parking lot. Returns the car object if we found the car and the car is picked
   * up, null if car was missing from the parking lot.
   *
   * @param regNr the registration number of the car we want to pickup.
   * @return a Car object
   */
  public Car pickupCar(String regNr) {
    Car car = parkingLot.get(regNr);
    if (car != null) {
      parkingLot.remove(regNr, car);
      numParkedCars.decrementAndGet();
      car.pickUp();
    }
    return car;
  }
}

/*
 * Original version, we might give the students this one and they will have to
 * change it into the new one.
 *
 *
 * public boolean parkCar(Car car) {
 *
 *   IntUnaryOperator op = (x) -> {
 *     if (x < numParkingSpots) { return x + 1; }
 *     else { return x; }
 *   };
 *
 *   if (numParkedCars.getAndUpdate(op) < numParkingSpots) {
 *     parkingLot.put(car.getRegNr(), car);
 *     car.parkCar();
 *     return true;
 *   }
 *
 *   return false;
 * }
 */