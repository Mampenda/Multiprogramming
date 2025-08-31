package inf214.portfolioSet1.cityParking;

public class Request {
  public enum RequestTypes {
    PARK, PICKUP, END_OF_DAY
  }

  private final RequestTypes type;
  private final String regNr;
  private final Car car;

  public Request(RequestTypes type, String regNr) {
    this.type = type;
    this.regNr = regNr;
    this.car = null;
  }

  public Request(RequestTypes type, Car car) {
    this.type = type;
    this.regNr = car.getRegNr();
    this.car = car;
  }

  public Request(RequestTypes type) {
    this.type = type;
    this.regNr = null;
    this.car = null;
  }

  public RequestTypes type() {
    return type;
  }

  public String regNr() {
    return regNr;
  }

  public Car car() {
    return car;
  }
}
