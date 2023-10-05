package com.Records;

import java.sql.Date;

public record BookingHotels(long bookingId, long userId, long hotelId, int noOfRooms, double totalPrice,
                            Date checkInDate, Date checkOutDate,
                            String bookingDate, String bookingStatus) {
}
