package com.rideshare.driver_notification_service.drivernotification;

import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class DriverNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(DriverNotificationController.class);

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public DriverNotificationController(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @GetMapping("/messages")
    public String getMessagesFromQueue() {
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .build();
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            // log each message
            for (Message message : messages) {
                logger.info("Message: " + message.body());
            }
            return messages.toString();
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    
    
}
