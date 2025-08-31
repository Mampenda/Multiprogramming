package inf214.portfolioSet1.cityParking;

public abstract class Named {
  protected final String name;

  public Named(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
