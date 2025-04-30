package com.rideshare.driver_notification_service.riderequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RideRequest {
    private String pickUpLongitude;
    private String pickUpLatitude;
    private String receiptHandle; // Add receiptHandle property

    // Default constructor for Jackson
    public RideRequest() {
    }

    @JsonCreator
    public RideRequest(
        @JsonProperty("pickUpLongitude") String pickUpLongitude,
        @JsonProperty("pickUpLatitude") String pickUpLatitude,
        @JsonProperty("receiptHandle") String receiptHandle // Include receiptHandle in constructor
    ) {
        this.pickUpLongitude = pickUpLongitude;
        this.pickUpLatitude = pickUpLatitude;
        this.receiptHandle = receiptHandle;
    }

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

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }
}
