# Driver Notification Service
The Driver Notification Service is responsible for delivering ride request notifications to nearby drivers in a ride-sharing platform. It processes messages from a queue, handles sequential driver offers, and ensures that only one driver is matched to a ride.

## Design Doc
https://docs.google.com/document/d/1BS_wAvXEq5U9X2N0A8F5IcN4wPj_l8pE512IKlzITnQ/edit?tab=t.0

## Feature Designs
https://app.diagrams.net/#G1IHnWwqZFYZpzGbaZWEVFWRco3uAW960T#%7B%22pageId%22%3A%22GxCQm2lJWlgfaDvyV_i_%22%7D

## Tech Stack
- **Java / Spring Boot**
- **AWS SQS** (Standard Queue with DelaySeconds)
- **Amazon DynamoDB** (RideRequest status tracking)
