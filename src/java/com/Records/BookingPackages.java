package com.Records;

public record BookingPackages(long bookingId, long userId, long packageId, int noOfPersons, double totalPrice,
                              String bookingDate, String bookingStatus) {
}
