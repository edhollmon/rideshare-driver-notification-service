package com.rideshare.driver_notification_service.drivernotification;

import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class DriverNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(DriverNotificationController.class);

    private final SqsClient sqsClient;

    @Value("${aws.sqs.ride-request-queue-url}")
    private String rideRequestQueueUrl;

    @Value("${aws.sqs.ride-match-queue-url}")
    private String rideMatchQueueUrl;

    public DriverNotificationController(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @GetMapping("/messages")
    public String getMessagesFromQueue() {
        List<Message> messages = null;
        ObjectMapper objectMapper = new ObjectMapper(); // For JSON deserialization
        // Receive messages from the queue
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(rideRequestQueueUrl)
                .build();
            messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            // log each message
            for (Message message : messages) {
                logger.info("Message: " + message.body());
            }

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        // Publish the message to the driver(s)
        for (Message message : messages) {
            try {
                RideRequest rideRequest = objectMapper.readValue(message.body(), RideRequest.class);

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
                        String messageBody = objectMapper.writeValueAsString(driverNotification);

                        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                            .queueUrl(rideMatchQueueUrl)
                            .messageBody(messageBody)
                            .build();
                        sqsClient.sendMessage(sendMessageRequest);
                        logger.info("Driver Match " + driverId + " sent to SQS queue successfully.");
                    } catch (Exception e) {
                        logger.error("Failed to send DriverNotificationMessage to SQS queue", e);
                    }

                }

                // Delete the message from the queue
                try {
                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                                .queueUrl(rideRequestQueueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build();
                        sqsClient.deleteMessage(deleteMessageRequest);
                        logger.info("Messages deleted successfully");
                } catch (SqsException e) {
                    System.err.println(e.awsErrorDetails().errorMessage());
                    System.exit(1);
                }
            } catch (Exception e) {
                logger.error("Failed to process message: " + message.body(), e);
            }

        }

        return messages != null ? messages.toString() : null;
    }
}
