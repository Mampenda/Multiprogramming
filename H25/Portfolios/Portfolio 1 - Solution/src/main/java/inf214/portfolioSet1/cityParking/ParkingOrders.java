package inf214.portfolioSet1.cityParking;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ParkingOrders {

  private final BlockingQueue<Request> parkingOrders = new LinkedBlockingQueue<>();

  /**
   * Add a new order to the list
   * 
   * @param order the order
   */
  public void addRequest(Request order) {
    parkingOrders.add(order);
  }

  /**
   * Remove a car from the list
   * 
   * @return A car, if available; null otherwise
   */
  public Request takeRequest() {
    try {
      return parkingOrders.take();
    } catch (InterruptedException e) {
      return null;
    }
  }

  /**
   * @return Number of request to fulfill
   */
  public int size() {
    return parkingOrders.size();
  }

  /**
   * Clear the list of any remaining requests
   */
  public void clear() {
    parkingOrders.clear();
  }
}
