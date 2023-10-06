package com.TripBuddy.Records;

public record Hotel(long hotelId, String hotelName, Double price, int noOfRooms, Double starRating,
                    String hotelAddress, String hotelCity, String hotelState, String hotelCountry,
                    String imagesFilename) {
}