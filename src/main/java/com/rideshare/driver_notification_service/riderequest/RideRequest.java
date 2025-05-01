package com.rideshare.driver_notification_service.riderequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RideRequest {
    private Double pickUpLongitude;
    private Double pickUpLatitude;
    private String receiptHandle; // Add receiptHandle property

    // Default constructor for Jackson
    public RideRequest() {
    }

    @JsonCreator
    public RideRequest(
        @JsonProperty("pickUpLongitude") Double pickUpLongitude,
        @JsonProperty("pickUpLatitude") Double pickUpLatitude,
        @JsonProperty("receiptHandle") String receiptHandle // Include receiptHandle in constructor
    ) {
        this.pickUpLongitude = pickUpLongitude;
        this.pickUpLatitude = pickUpLatitude;
        this.receiptHandle = receiptHandle;
    }

    public Double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(Double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public Double getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(Double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }
}
