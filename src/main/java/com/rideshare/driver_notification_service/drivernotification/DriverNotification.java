package com.rideshare.driver_notification_service.drivernotification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverNotification {
    public double pickUpLongitude;
    public double pickUpLatitude;
    public String driverId;

    public double getPickUpLongitude() {
        return pickUpLongitude;
    }
    public void setPickUpLongitude(double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }
    public double getPickUpLatitude() {
        return pickUpLatitude;
    }
    public void setPickUpLatitude(double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }
}
