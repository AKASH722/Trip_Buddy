package com.TripBuddy.Records;

import java.sql.Date;

public record BookingPackages(long bookingPackageId, String bookingNumber, Packages packages, int noOfPersons,
                              Double price, Date dateOfJourney, Date dateOfBooking, User user, String status) {
}
