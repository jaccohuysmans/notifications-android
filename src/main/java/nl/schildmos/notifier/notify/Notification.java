package nl.schildmos.notifier.notify;


public class Notification {

  private String email;
  private Double latitude;
  private Double longitude;
  private String deviceId;
  private String description;

  public Notification(String email,Double latitude,Double longitude, String deviceId, String description){
    this.email = email;
    this.latitude = latitude;
    this.longitude = longitude;
    this.deviceId = deviceId;
    this.description = description;
  }
}
