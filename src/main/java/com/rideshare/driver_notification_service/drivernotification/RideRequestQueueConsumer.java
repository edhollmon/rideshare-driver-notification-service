package com.rideshare.driver_notification_service.drivernotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.sqs.annotation.SqsListener;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class RideRequestQueueConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RideRequestQueueConsumer.class);

    private final SqsClient sqsClient;

    @Value("${aws.sqs.ride-request-queue-url}")
    private String rideRequestQueueUrl;

    @Value("${aws.sqs.ride-match-queue-url}")
    private String rideMatchQueueUrl;

    public RideRequestQueueConsumer(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @SqsListener("${aws.sqs.ride-request-queue-url}")
    public void listen(Message rideRequestMessage, RideRequest rideRequest) {
        logger.info("Received rider request: " + rideRequest);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            int messageDelay = 0; // Delay in seconds
            for (String driverId : new String[] {"driver1", "driver2", "driver3"}) {
                // Construct a DriverNotificationMessage object
                DriverNotificationMessage driverNotification = new DriverNotificationMessage(
                    rideRequest.getPickUpLongitude(),
                    rideRequest.getPickUpLatitude(),
                    driverId
                );
                logger.info("DriverNotificationMessage: " + driverNotification);

                // Send the DriverNotificationMessage to the ride match queue
                try {
                    String notificationMessageBody = objectMapper.writeValueAsString(driverNotification);

                    SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                        .queueUrl(rideMatchQueueUrl)
                        .messageBody(notificationMessageBody)
                        .delaySeconds(messageDelay)
                        .build();
                    sqsClient.sendMessage(sendMessageRequest);
                    messageDelay += 10; // Increment delay for each driver
                    logger.info("Driver Match " + driverId + " sent to SQS queue successfully.");
                } catch (Exception e) {
                    logger.error("Failed to send DriverNotificationMessage to SQS queue", e);
                }
            }

            // Delete the message from the queue
            try {
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(rideRequestQueueUrl)
                    .receiptHandle(rideRequestMessage.receiptHandle()) // Use receiptHandle parameter
                    .build();
                sqsClient.deleteMessage(deleteMessageRequest);
                logger.info("Message deleted successfully with receiptHandle: " + rideRequestMessage.receiptHandle());
            } catch (SqsException e) {
                logger.error("Error deleting message: " + e.awsErrorDetails().errorMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to process message: " + rideRequest, e);
        }
    }
}
