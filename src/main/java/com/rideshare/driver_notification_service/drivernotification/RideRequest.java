package com.rideshare.driver_notification_service.drivernotification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RideRequest {
    private String pickUpLongitude;
    private String pickUpLatitude;



    // Default constructor for Jackson
    public RideRequest() {
    }

    @JsonCreator
    public RideRequest(
        @JsonProperty("pickUpLongitude") String pickUpLongitude,
        @JsonProperty("pickUpLatitude") String pickUpLatitude
    ) {
        this.pickUpLongitude = pickUpLongitude;
        this.pickUpLatitude = pickUpLatitude;
    }

    // Removed duplicate constructor to avoid compilation error

    public String getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(String pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public String getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(String pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }
}
