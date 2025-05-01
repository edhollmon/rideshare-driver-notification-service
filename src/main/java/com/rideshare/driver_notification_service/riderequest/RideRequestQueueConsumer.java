package com.rideshare.driver_notification_service.riderequest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideshare.driver_notification_service.drivernotification.DriverNotification;

import io.awspring.cloud.sqs.annotation.SqsListener;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.resps.GeoRadiusResponse;

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
        logger.info("Received rider request: {}", rideRequest);

        try {
            List<GeoRadiusResponse> nearbyDrivers = getNearbyDrivers(
                rideRequest.getPickUpLongitude(),
                rideRequest.getPickUpLatitude(),
                50.0
            );

            if (nearbyDrivers.isEmpty()) {
                logger.info("No drivers found within {} miles.", 50.0);
                return;
            }

            logger.info("Found {} drivers within {} miles.", nearbyDrivers.size(), 50.0);

            List<String> driverIds = new ArrayList<>();
            for (GeoRadiusResponse response : nearbyDrivers) {
                driverIds.add(response.getMemberByString());
            }

            sendDriverNotifications(driverIds, rideRequest);
        } catch (Exception e) {
            logger.error("Failed to process message: {}", rideRequest, e);
        }
    }

    private List<GeoRadiusResponse> getNearbyDrivers(double longitude, double latitude, double radiusInMiles) {
        String geoKey = "drivers:locations";

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            return jedis.georadius(
                geoKey,
                longitude,
                latitude,
                radiusInMiles,
                GeoUnit.MI,
                GeoRadiusParam.geoRadiusParam().withCoord().withDist()
            );
        } catch (Exception e) {
            logger.error("Failed to fetch nearby drivers from Redis.", e);
            return new ArrayList<>();
        }
    }

    private void sendDriverNotifications(List<String> driverIds, RideRequest rideRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        int messageDelay = 0;

        for (String driverId : driverIds) {
            try {
                DriverNotification driverNotification = new DriverNotification(
                    rideRequest.getPickUpLongitude(),
                    rideRequest.getPickUpLatitude(),
                    driverId
                );

                String notificationMessageBody = objectMapper.writeValueAsString(driverNotification);

                SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(rideMatchQueueUrl)
                    .messageBody(notificationMessageBody)
                    .delaySeconds(messageDelay)
                    .build();

                sqsClient.sendMessage(sendMessageRequest);
                messageDelay += 10;

                logger.info("Driver Match {} sent to SQS queue successfully.", driverId);
            } catch (Exception e) {
                logger.error("Failed to send DriverNotificationMessage for driverId: {}", driverId, e);
            }
        }
    }
}
