package inf214.portfolioSet1;

public interface InterruptibleSupplier<T> {
  T get() throws InterruptedException;
}
