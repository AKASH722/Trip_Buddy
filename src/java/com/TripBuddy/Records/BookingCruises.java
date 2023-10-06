package com.TripBuddy.Records;

import java.sql.Date;

public record BookingCruises(long bookingCruiseId, String bookingNumber, Cruise cruise, int noOfTickets, Double price,
                             Date dateOfBooking, User user, String status) {
}
