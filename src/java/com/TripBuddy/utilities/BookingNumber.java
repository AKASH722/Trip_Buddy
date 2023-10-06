package com.TripBuddy.utilities;

import com.TripBuddy.database.Database;

public class BookingNumber {
    public static String generateBookingNumber(String type) {
        Database database = new Database();
        StringBuilder bookingNumber = new StringBuilder("B");
        int count = 0;
        if (type.equals("package")) {
            count = database.getBookingPackageCount();
            bookingNumber.append("PK");
        } else if (type.equals("hotel")) {
            count = database.getBookingHotelCount();
            bookingNumber.append("HT");
        } else {
            count = database.getBookingCruiseCount();
            bookingNumber.append("CR");
        }
        count++;
        int number = count % 9999;
        char intial = 'A';
        char intial_2 = 'A';
        while (count > 10000) {
            count -= 10000;
            intial_2++;
            if (intial_2 > 'Z') {
                intial_2 = 'A';
                intial++;
            }
        }
        bookingNumber.append(intial);
        bookingNumber.append(intial_2);
        bookingNumber.append(String.format("%04d", number));
        database.close();
        return bookingNumber.toString();
    }
}
