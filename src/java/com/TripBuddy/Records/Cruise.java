package com.TripBuddy.Records;

import java.time.LocalDateTime;

public record Cruise(long cruiseId, String cruiseName, String description, double price, int duration, int capacity,
                     String departurePort, String arrivalPort, LocalDateTime departureDateTime,
                     LocalDateTime arrivalDateTime, String imagesFilename) {
}
