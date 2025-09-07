package inf214.portfolioSet1.cityParking;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import inf214.portfolioSet1.ThreadUtils;
import inf214.portfolioSet1.cityParking.Request.RequestTypes;

public class Valet extends Thread {

    Camera camera;
    CityParking cps;
    ParkingLot parkingLot;
    ConcurrentMap<String, Integer> registrationChars;

    /**
     * Valet constructor.
     *
     * @param name              Thread name
     * @param cps               Reference to CityParking system
     * @param parkingLot        Reference to the parking lot
     * @param registrationChars Shared map counting cars by registration prefix
     */
    public Valet(String name, CityParking cps, ParkingLot parkingLot, ConcurrentMap<String, Integer> registrationChars) {
        super(name);
        this.cps = cps;
        this.parkingLot = parkingLot;
        this.registrationChars = registrationChars;
    }

    /**
     * Main loop: process requests from the parking queue until end-of-day.
     * - PARK: park a new car
     * - PICKUP: retrieve a parked car
     * - END_OF_DAY: stop processing
     */
    public void run() {
        while (true) {

            // Take a request from the parking queue
            Request order = cps.parkingQueue().takeRequest();

            // Process the request based on its type (if not null, then park, pickup, or end-of-day, otherwise spin-wait)
            if (order != null) {

                // PARK request
                if (order.type() == RequestTypes.PARK) {
                    parkCar(order.car());
                    ThreadUtils.delay(ThreadLocalRandom.current().nextInt(10) + 1); // Union mandated break before next attempt

                    // PICKUP request
                } else if (order.type() == RequestTypes.PICKUP) {
                    pickupCar(order.regNr());
                    ThreadUtils.delay(ThreadLocalRandom.current().nextInt(10) + 1); // Union mandated break before next attempt

                    // END_OF_DAY request
                } else if (order.type() == RequestTypes.END_OF_DAY) {
                    System.out.println(this.getName() + ": end of day");
                    break; // Exit the loop and end the thread
                }

            } else {
                Thread.onSpinWait(); // Union mandated break before next attempt
            }
        }
    }


    /**
     * ==== Methods from Task B1.2 ====
     **/


    private void parkCar(Car car) {

        // Wait until we can reserve a parking spot
        while (!parkingLot.reserveParking()) {
            Thread.onSpinWait();
        }

        // Accuire monitoring resources
        Camera camera = cps.grabCamera(this);
        Supervisor sup = cps.grabSupervisor(this);

        parkingLot.parkCar(car);

        // Supervisor oversees parking with camera assistance
        sup.superviseParking(this, camera, car);
        CityParking.log("Parked car: " + car + " with " + sup + " and " + camera);

        // Release monitoring resources
        cps.releaseSupervisor(sup, this);
        cps.releaseCamera(camera, this);

        // Update the registration character count
        String chars = car.getRegNr().substring(0, 2);
        registrationChars.putIfAbsent(chars, 0);
        registrationChars.computeIfPresent(chars, (k, v) -> v + 1);
    }

    private void pickupCar(String regNr) {

        // Wait until we can reserve a parking spot
        while (!parkingLot.isParked(regNr)) {
            Thread.onSpinWait();
        }

        // Acquire monitoring resources
        Camera camera = cps.grabCamera(this);
        Supervisor sup = cps.grabSupervisor(this);

        Car car = parkingLot.pickupCar(regNr);

        // Supervisor oversees pickup with camera assistance
        sup.supervisePickUp(this, camera, car);
        CityParking.log("Picked up car: " + car + " with " + sup + " and " + camera);

        // Release monitoring resources
        cps.releaseSupervisor(sup, this);
        cps.releaseCamera(camera, this);
    }
}
