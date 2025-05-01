package com.rideshare.driver_notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.args.GeoUnit;


@SpringBootApplication
public class DriverNotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverNotificationServiceApplication.class, args);

		UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");
		// Add 3 drivers to the Redis geospatial index santa monica
		jedis.geoadd("drivers:locations", -118.4912, 34.0194, "driver1");
		jedis.geoadd("drivers:locations", -118.4912, 34.0194, "driver2");
		jedis.geoadd("drivers:locations", -118.4912, 34.0194, "driver3");
		String driverLocation = jedis.georadius("drivers:locations", -118.4912, 34.0194, 5, GeoUnit.M).toString();
		System.out.println("Seeded Driver locations Location: " + driverLocation);
		jedis.close();

	}

}
