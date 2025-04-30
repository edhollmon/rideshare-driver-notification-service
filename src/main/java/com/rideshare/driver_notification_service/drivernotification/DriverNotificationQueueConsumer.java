package com.rideshare.driver_notification_service.drivernotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rideshare.driver_notification_service.riderequest.RideRequestQueueConsumer;

import io.awspring.cloud.sqs.annotation.SqsListener;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
public class DriverNotificationQueueConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RideRequestQueueConsumer.class);

    @SqsListener("${aws.sqs.ride-match-queue-url}")
    public void listen(Message message, DriverNotification driverNotification) {
        logger.info("Driver notification  : " + driverNotification);
       
    }
}
