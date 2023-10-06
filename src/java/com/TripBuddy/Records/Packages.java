package com.TripBuddy.Records;

public record Packages(long packageId, String packageName, String description, double price, int duration,
                       String destinationPlace, String destinationCity, String destinationState,
                       String destinationCountry, String imagesFilename) {
}