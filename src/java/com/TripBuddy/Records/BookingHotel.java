package com.TripBuddy.Records;

import java.sql.Date;

public record BookingHotel(long bookingHotelId, String bookingNumber, Hotel hotel, int noOfRooms, Double price,
                           Date dateOfBooking, Date checkInDate, Date checkOutDate, User user, String status) {
}
