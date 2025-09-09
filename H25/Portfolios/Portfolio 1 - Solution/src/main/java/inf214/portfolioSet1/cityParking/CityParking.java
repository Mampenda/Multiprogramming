package inf214.portfolioSet1.cityParking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import inf214.portfolioSet1.ThreadUtils;

public class CityParking {

    public static boolean ignoreReservable = false;
    public static boolean logging = true;

    private ParkingOrders orderQueue;
    private BlockingQueue<Camera> camerabox;
    private BlockingQueue<Supervisor> supervisors;
    private List<Valet> valets;
    private ParkingLot parkingLot;

    private ConcurrentMap<String, Integer> registrationChars; // Simulated database of car plates
    private Map<String, String> parkLog = new HashMap<>();

    public CityParking(int numValets, int numSupervisors, int numCameras, int numParkingSpots) {
        this.orderQueue = new ParkingOrders();
        this.parkingLot = new ParkingLot(numParkingSpots);
        this.camerabox = new LinkedBlockingQueue<>();
        this.supervisors = new LinkedBlockingQueue<>();
        this.valets = new ArrayList<>();

        this.registrationChars = new ConcurrentHashMap<>(); // Initialize the simulated database

        createValets(numValets);
        createSupervisors(numSupervisors);
        createCameras(numCameras);
    }

    /**
     * Grab a Supervisor.
     * <p>
     * The exact semantics depend on the implementation. (Maybe it's "politely ask a
     * Supervisor for help?")
     * <p>
     * This method might wait for a Supervisor to become available before returning;
     * even if it normally waits, it might return null in some cases (e.g., if the
     * implementation uses a timeout).
     * <p>
     * The student argument may or may not affect the outcome. E.g., we might use it
     * to attempt some kind of fair queuing.
     *
     * @param valet   The valet who needs help
     * @param timeout You may want to add a timeout argument
     * @return A helpful Supervisor, or null if none are available
     */
    public Supervisor grabSupervisor(Valet valet/* , long timeout */) {
        randomDelay();

        /*
         * We can get stuff from a BlockingQueue in different ways:
         *
         * peek() – find first Supervisor or null, but don't remove her from the queue
         * take() – remove first Supervisor, waiting if necessary
         * poll() – remove first Supervisor, or null if none available
         * poll(timeout, TimeUnit.MILLISECONDS) – returns null if none available within timeout
         * remove() – remove first Supervisor, exception if none available
         */
        Supervisor sup = ThreadUtils.ignoreInterrupted(() -> supervisors.take());
        if (sup != null)
            sup.reserve(valet);
        return sup;
    }

    /**
     * Release Supervisor from their duties.
     * <p>
     * The exact semantics depend on the implementation.
     * <p>
     * The valet argument may or may not affect the outcome. E.g., we might use it
     * to attempt some kind of fair queuing.
     *
     * @param supervisor The supervisor who's ready for a break
     * @param valet      The valet who was helped by the supervisor
     */
    public void releaseSupervisor(Supervisor supervisor, Valet valet) {
        supervisor.release(valet);
        supervisors.add(supervisor);
    }

    /**
     * Grab camera from the camerabox.
     * <p>
     * The exact semantics depend on the implementation.
     * <p>
     * This method might wait for cameras to become available before returning; even
     * if it normally waits, it might return null in some cases (e.g., if the
     * implementation uses a timeout).
     * <p>
     * The valet argument may or may not affect the outcome. E.g., we might use it
     * attempt some kind of fair queuing.
     *
     * @param valet   The valet who wants to use the camera
     * @param timeout You may want to add a timeout argument
     * @return Camera, or null if none were found
     */
    public Camera grabCamera(Valet valet /* , long timeout */) {
        randomDelay();
        Camera c = ThreadUtils.ignoreInterrupted(() -> camerabox.take());
        if (c != null)
            c.reserve(valet);

        return c;
    }

    /**
     * Put camera back in camerabox.
     * <p>
     * The exact semantics depend on the implementation.
     * <p>
     * The valet argument may or may not affect the outcome. E.g., we might use it
     * attempt some kind of fair queuing.
     *
     * @param pliers The recording tool
     * @param valet  The valet who borrowed the camera
     */
    public void releaseCamera(Camera camera, Valet valet) {
        camera.release(valet);
        /*
         * By default our camerabox has infinite size – but we could use a BlockingQueue
         * with a fixed capacity. For example, we could say that the camerabox has room
         * for no more than five cameras, in which case `add` would throw
         * `IllegalStateException` if we attempted to add a sixth camera.
         *
         * In that case, we'd probably want to use the `offer()` method instead, and
         * we'd have to deal with what happens if we try to put our cameras back when
         * there's no room in the camerabox. (This would of course make little sense in
         * our simulation – where would we get the extra cameras from?)
         */
        camerabox.add(camera);
    }

    public ParkingOrders parkingQueue() {
        return orderQueue;
    }

    public static void log(String s) {
        if (logging)
            System.out.printf("[%s] %s%n", Thread.currentThread().getName(), s);
    }

    public synchronized void logParking(String request, Valet valet, Supervisor supervisor, Camera camera, Car car) {
        parkLog.put(car.getRegNr() + ":" + request, supervisor + ":" + camera + ":" + valet);
        if (logging)
            System.out.printf("%s: %s " + request + " %s with %s%n", supervisor, valet.getName(), car, camera);
    }

    public Integer queryDatabase(String regChars) {
        return registrationChars.getOrDefault(regChars, 0);
    }

    // ============================================================================================================== //

    public void addRequestsToQueue(Request order) {
        orderQueue.addRequest(order);
    }

    public void addRequestsToQueue(List<Request> orders) {
        for (int i = 0; i < orders.size(); i++) {
            orderQueue.addRequest(orders.get(i));
        }
    }

    public Map<String, String> getParkingLog() {
        return this.parkLog;
    }

    public List<Valet> getValets() {
        return this.valets;
    }

    private void randomDelay() {
        ThreadUtils.delay(2 + ThreadLocalRandom.current().nextInt(logging ? 2 : 10));
    }

    private void createValets(int numValets) {
        for (int i = 1; i <= numValets; i++) {
            valets.add(new Valet("valet-" + i, this, parkingLot, registrationChars));
        }
    }

    private void createSupervisors(int numSupervisors) {
        for (int i = 1; i <= numSupervisors; i++) {
            supervisors.add(new Supervisor("Supervisor-" + i, this));
        }
    }

    private void createCameras(int numCameras) {
        for (int i = 1; i <= numCameras; i++) {
            camerabox.add(new Camera("Camera-" + i));
        }
    }
}
