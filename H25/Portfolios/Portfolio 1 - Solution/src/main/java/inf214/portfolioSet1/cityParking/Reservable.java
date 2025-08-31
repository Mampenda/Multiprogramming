package inf214.portfolioSet1.cityParking;

/**
 * Abstract superclass for objects that can be "reserved" and "released" by
 * other objects.
 * 
 * We use this to double-check proper use of Professor and Pliers.
 * 
 * @param <T> Type of object that can reserve this object
 */
public abstract class Reservable<T, U extends Reservable<T, U>> extends Named {

  protected T owner;

  public Reservable(String name) {
    super(name);
  }

  /**
   * Reserve the object.
   * 
   * @param obj Calling object
   * @throws IllegalStateException if object is already reserved
   */
  @SuppressWarnings("unchecked")
  public synchronized U reserve(T obj) {
    if (owner != null && !CityParking.ignoreReservable)
      throw new IllegalStateException();
    owner = obj;
    return (U) this;
  }

  /**
   * Release a reserved object.
   * 
   * 
   * @param obj Calling object
   * @throws IllegalStateException if object is not already reserved by
   *                               <code>obj</code>
   */
  @SuppressWarnings("unchecked")
  public synchronized U release(T obj) {
    if (owner != obj && !CityParking.ignoreReservable)
      throw new IllegalStateException();
    owner = null;
    return (U) this;
  }

  public synchronized boolean isReservedBy(T obj) {
    return owner == obj;
  }

}
