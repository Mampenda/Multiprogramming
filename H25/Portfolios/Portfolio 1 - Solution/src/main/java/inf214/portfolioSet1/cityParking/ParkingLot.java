package inf214.portfolioSet1.cityParking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class ParkingLot {

    // Fields of the class
    private final int numParkingSpots;   // Nr. of parking spots (aka. the capacity)
    private AtomicInteger numParkedCars = new AtomicInteger(0);   // Thead-safe counter of the nr of parked cars
    private ConcurrentMap<String, Car> parkingLot = new ConcurrentHashMap<String, Car>();  // Concirrent map of parked cars

    // Constructor
    public ParkingLot(int numParkingSpots) {
        this.numParkingSpots = numParkingSpots;
    }


    /** ==== Methods from Task B1.1 ==== **/

    /**
     * Reserving spot before parking a car
     * Attempts to reserve a parking spot. Returns true if there was space and the spot is reserved,
     * false if the parking lot was full.
     * <p>
     * Operator op ensures: if not full → increment; if full → leave unchanged.
     * <p>
     * getAndUpdate(op) atomically:
     * Reads the current value of numParkedCars.
     * Applies the operator op.
     * Stores the result back.
     * Returns the old value.
     *
     * @return true/false whether we successfully reserved a parking spot.
     */
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
     * and the car is parked, false if the parking lot was full.
     *
     * @param car the car to park.
     * @return true/false wheter the car got parked.
     */
    public void parkCar(Car car) {
        car.park();
        parkingLot.put(car.getRegNr(), car); // ConcurrentHashMap ensures thread-safe insertion
    }

    /**
     * Checks if a car with the given registration number is parked in the parking lot.
     *
     * @param regNr the registration number of the car we want to check.
     * @return true/false whether the car is parked.
     */
    public boolean isParked(String regNr) {
        return parkingLot.containsKey(regNr); // Concurrent read, safe for multiple threads
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
            parkingLot.remove(regNr, car);   // Concurrent removal, thread-safe
            numParkedCars.decrementAndGet(); // Atomic decrement → acts like a semaphore release
            car.pickUp();
        }
        return car;
    }
}