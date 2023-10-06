package com.TripBuddy.database;

import com.TripBuddy.Records.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Database {
    Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trip_management_system", "root", "root");
        } catch (SQLException ignored) {

        }
    }

    public User checkUser(String email, String password) {
        try {
            PreparedStatement checkUser = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = SHA2(?,256)");
            checkUser.setString(1, email);
            checkUser.setString(2, password);
            ResultSet rs = checkUser.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("usertype"), rs.getLong("phone_number"), rs.getString("country"));
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return null;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {

        }
    }

    public boolean addNewUser(String name, String email, String password, long phone_number, String country) {
        try {
            PreparedStatement addUser = connection.prepareStatement("INSERT INTO USERS(name,email,password,usertype,phone_number,country) VALUES(?,?, SHA2(?,256),?,?,?)");
            addUser.setString(1, name);
            addUser.setString(2, email);
            addUser.setString(3, password);
            addUser.setString(4, "Customer");
            addUser.setLong(5, phone_number);
            addUser.setString(6, country);
            return addUser.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            return false;
        }
    }

    public void getAllPackages(ArrayList<Packages> packages) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.* FROM packages p JOIN users u ON p.created_by = u.user_id WHERE p.is_deleted = false AND u.usertype = 'Admin'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long packageId = resultSet.getLong("package_id");
                String packageName = resultSet.getString("package_name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int duration = resultSet.getInt("duration");
                String destinationPlace = resultSet.getString("destination_place");
                String destinationCity = resultSet.getString("destination_city");
                String destinationState = resultSet.getString("destination_state");
                String destinationCountry = resultSet.getString("destination_country");
                String imagesFilename = resultSet.getString("images_filename");
                Packages packageItem = new Packages(packageId, packageName, description, price, duration, destinationPlace, destinationCity, destinationState, destinationCountry, imagesFilename);
                packages.add(packageItem);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ignored) {
        }
    }

    public boolean deletePackage(long id, int user_id) {
        try {
            PreparedStatement delete = connection.prepareStatement("UPDATE packages SET is_deleted =true WHERE package_id = ? AND created_by = ?");
            delete.setLong(1, id);
            delete.setInt(2, user_id);
            return delete.executeUpdate() == 1;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void searchPackages(ArrayList<Packages> packagesList, String text) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.* FROM packages p JOIN users u ON p.created_by = u.user_id WHERE p.is_deleted = false AND u.usertype = 'Admin' AND p.package_name LIKE ?");
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long packageId = resultSet.getLong("package_id");
                String packageName = resultSet.getString("package_name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int duration = resultSet.getInt("duration");
                String destinationPlace = resultSet.getString("destination_place");
                String destinationCity = resultSet.getString("destination_city");
                String destinationState = resultSet.getString("destination_state");
                String destinationCountry = resultSet.getString("destination_country");
                String imagesFilename = resultSet.getString("images_filename");
                Packages packageItem = new Packages(packageId, packageName, description, price, duration, destinationPlace, destinationCity, destinationState, destinationCountry, imagesFilename);
                packagesList.add(packageItem);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ignored) {
        }
    }

    public boolean addPackage(String packageName, String description, Double priceDouble, int durationInt, String place, String city, String state, String country, String image, int user_id) {
        if (image != null)
            image = getNewImageLoaction(image);
        try {
            System.out.println("addPackage: " + packageName + " " + description + " " + priceDouble + " " + durationInt + " " + place + " " + city + " " + state + " " + country + " " + image);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO packages(package_name,description,price,duration,destination_place,destination_city,destination_state,destination_country,images_filename,created_by) VALUES(?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, packageName);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, priceDouble);
            preparedStatement.setInt(4, durationInt);
            preparedStatement.setString(5, place);
            preparedStatement.setString(6, city);
            preparedStatement.setString(7, state);
            preparedStatement.setString(8, country);
            preparedStatement.setString(9, image);
            preparedStatement.setInt(10, user_id);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException ignored) {
            return false;
        }
    }

    private String getNewImageLoaction(String image) {
        File file = new File(image);
        Path sourcePath = Path.of(file.getAbsolutePath());
        Path destinationPath = Path.of("D:/Projects/Academic Projects/SEM II/Trip Buddy/src/resources/UserImages/" + file.getName());
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copied successfully.");
        } catch (IOException e) {
            System.err.println("Error copying the image: " + e.getMessage());
        }
        return destinationPath.toString();
    }

    public Packages getPackage(long id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.* FROM packages p JOIN users u ON p.created_by = u.user_id WHERE p.is_deleted = false AND u.usertype = 'Admin' AND p.package_id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long packageId = resultSet.getLong("package_id");
                String packageName = resultSet.getString("package_name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int duration = resultSet.getInt("duration");
                String destinationPlace = resultSet.getString("destination_place");
                String destinationCity = resultSet.getString("destination_city");
                String destinationState = resultSet.getString("destination_state");
                String destinationCountry = resultSet.getString("destination_country");
                String imagesFilename = resultSet.getString("images_filename");
                return new Packages(packageId, packageName, description, price, duration, destinationPlace, destinationCity, destinationState, destinationCountry, imagesFilename);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ignored) {

        }
        return null;
    }

    public boolean updatePackage(String packageName, String description, Double priceDouble, int durationInt, String place, String city, String state, String country, String image, long id, int user_id) {
        if (image != null)
            image = getNewImageLoaction(image);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE packages SET package_name = ?, description = ?, price = ?, duration = ?, destination_place = ?, destination_city = ?, destination_state = ?, destination_country = ?, images_filename = ? WHERE package_id = ? AND created_by = ?");
            preparedStatement.setString(1, packageName);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, priceDouble);
            preparedStatement.setInt(4, durationInt);
            preparedStatement.setString(5, place);
            preparedStatement.setString(6, city);
            preparedStatement.setString(7, state);
            preparedStatement.setString(8, country);
            preparedStatement.setString(9, image);
            preparedStatement.setLong(10, id);
            preparedStatement.setInt(11, user_id);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public void getAllHotels(ArrayList<Hotel> hotelsList) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hotels WHERE is_deleted = false");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long hotelId = resultSet.getLong("hotel_id");
                String hotelName = resultSet.getString("hotel_name");
                Double price = resultSet.getDouble("price");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double starRating = resultSet.getDouble("star_rating");
                String hotelAddress = resultSet.getString("hotel_address");
                String hotelCity = resultSet.getString("hotel_city");
                String hotelState = resultSet.getString("hotel_state");
                String hotelCountry = resultSet.getString("hotel_country");
                String imagesFilename = resultSet.getString("images_filename");
                Hotel hotel = new Hotel(hotelId, hotelName, price, noOfRooms, starRating, hotelAddress, hotelCity, hotelState, hotelCountry, imagesFilename);
                hotelsList.add(hotel);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception ignored) {

        }
    }

    public Hotel getHotel(long hotelId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hotels WHERE is_deleted = false AND hotel_id = ?");
            preparedStatement.setLong(1, hotelId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hotelName = resultSet.getString("hotel_name");
                Double price = resultSet.getDouble("price");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double starRating = resultSet.getDouble("star_rating");
                String hotelAddress = resultSet.getString("hotel_address");
                String hotelCity = resultSet.getString("hotel_city");
                String hotelState = resultSet.getString("hotel_state");
                String hotelCountry = resultSet.getString("hotel_country");
                String imagesFilename = resultSet.getString("images_filename");
                resultSet.close();
                preparedStatement.close();
                return new Hotel(hotelId, hotelName, price, noOfRooms, starRating, hotelAddress, hotelCity, hotelState, hotelCountry, imagesFilename);
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public boolean updateHotel(String updatedHotelName, Double updatedPrice, int updatedNoOfRooms, Double updatedStarRating, String updatedHotelAddress, String updatedHotelCity, String updatedHotelState, String updatedHotelCountry, String updatedImagesFilename, long hotelId, long user_id) {
        if (updatedImagesFilename != null)
            updatedImagesFilename = getNewImageLoaction(updatedImagesFilename);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE hotels SET hotel_name = ?, price = ?, no_of_rooms = ?, star_rating = ?, hotel_address = ?, hotel_city = ?, hotel_state = ?, hotel_country = ?, images_filename = ? WHERE hotel_id = ? AND added_by = ?");
            preparedStatement.setString(1, updatedHotelName);
            preparedStatement.setDouble(2, updatedPrice);
            preparedStatement.setInt(3, updatedNoOfRooms);
            preparedStatement.setDouble(4, updatedStarRating);
            preparedStatement.setString(5, updatedHotelAddress);
            preparedStatement.setString(6, updatedHotelCity);
            preparedStatement.setString(7, updatedHotelState);
            preparedStatement.setString(8, updatedHotelCountry);
            preparedStatement.setString(9, updatedImagesFilename);
            preparedStatement.setLong(10, hotelId);
            preparedStatement.setLong(11, user_id);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean addHotel(String hotelName, Double price, int noOfRooms, Double starRating, String hotelAddress, String hotelCity, String hotelState, String hotelCountry, String imagesFilename, long user_id) {
        if (imagesFilename != null)
            imagesFilename = getNewImageLoaction(imagesFilename);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hotels(hotel_name,price,no_of_rooms,star_rating,hotel_address,hotel_city,hotel_state,hotel_country,images_filename,added_by) VALUES(?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, hotelName);
            preparedStatement.setDouble(2, price);
            preparedStatement.setInt(3, noOfRooms);
            preparedStatement.setDouble(4, starRating);
            preparedStatement.setString(5, hotelAddress);
            preparedStatement.setString(6, hotelCity);
            preparedStatement.setString(7, hotelState);
            preparedStatement.setString(8, hotelCountry);
            preparedStatement.setString(9, imagesFilename);
            preparedStatement.setLong(10, user_id);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteHotel(long id, long user_id) {
        try {
            PreparedStatement delete = connection.prepareStatement("UPDATE hotels SET is_deleted =true WHERE hotel_id = ? AND added_by = ?");
            delete.setLong(1, id);
            delete.setLong(2, user_id);
            return delete.executeUpdate() == 1;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void searchHotels(ArrayList<Hotel> hotelsList, String text) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM hotels WHERE is_deleted = false AND hotel_name LIKE ?");
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long hotelId = resultSet.getLong("hotel_id");
                String hotelName = resultSet.getString("hotel_name");
                Double price = resultSet.getDouble("price");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double starRating = resultSet.getDouble("star_rating");
                String hotelAddress = resultSet.getString("hotel_address");
                String hotelCity = resultSet.getString("hotel_city");
                String hotelState = resultSet.getString("hotel_state");
                String hotelCountry = resultSet.getString("hotel_country");
                String imagesFilename = resultSet.getString("images_filename");
                Hotel hotel = new Hotel(hotelId, hotelName, price, noOfRooms, starRating, hotelAddress, hotelCity, hotelState, hotelCountry, imagesFilename);
                hotelsList.add(hotel);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception ignored) {

        }
    }

    public void getAllCruises(ArrayList<Cruise> cruisesList) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cruises WHERE is_deleted = false");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long cruiseId = resultSet.getLong("cruise_id");
                String cruiseName = resultSet.getString("cruise_name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int duration = resultSet.getInt("duration");
                int capacity = resultSet.getInt("capacity");
                String departurePort = resultSet.getString("departure_port");
                LocalDateTime departureDateTime = resultSet.getTimestamp("departure_date_time").toLocalDateTime();
                String arrivalPort = resultSet.getString("arrival_port");
                LocalDateTime arrivalDateTime = resultSet.getTimestamp("arrival_date_time").toLocalDateTime();
                String imagesFilename = resultSet.getString("images_filename");
                Cruise cruise = new Cruise(cruiseId, cruiseName, description, price, duration, capacity, departurePort, arrivalPort, departureDateTime, arrivalDateTime, imagesFilename);
                cruisesList.add(cruise);
            }
        } catch (Exception ignored) {

        }
    }

    public void searchCruises(ArrayList<Cruise> cruisesList, String text) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cruises WHERE is_deleted = false AND cruise_name LIKE ?");
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long cruiseId = resultSet.getLong("cruise_id");
                String cruiseName = resultSet.getString("cruise_name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int duration = resultSet.getInt("duration");
                int capacity = resultSet.getInt("capacity");
                String departurePort = resultSet.getString("departure_port");
                LocalDateTime departureDateTime = resultSet.getTimestamp("departure_date_time").toLocalDateTime();
                String arrivalPort = resultSet.getString("arrival_port");
                LocalDateTime arrivalDateTime = resultSet.getTimestamp("arrival_date_time").toLocalDateTime();
                String imagesFilename = resultSet.getString("images_filename");
                Cruise cruise = new Cruise(cruiseId, cruiseName, description, price, duration, capacity, departurePort, arrivalPort, departureDateTime, arrivalDateTime, imagesFilename);
                cruisesList.add(cruise);
            }
        } catch (Exception ignored) {

        }
    }

    public boolean addCruise(String cruiseName, String description, double priceDouble, int durationInt, int capacityInt, String departurePort, String arrivalPort, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, String image, int userID) {
        if (image != null)
            image = getNewImageLoaction(image);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO cruises(cruise_name,description,price,duration,capacity,departure_port,arrival_port,departure_date_time,arrival_date_time,images_filename,added_by) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, cruiseName);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, priceDouble);
            preparedStatement.setInt(4, durationInt);
            preparedStatement.setInt(5, capacityInt);
            preparedStatement.setString(6, departurePort);
            preparedStatement.setString(7, arrivalPort);
            preparedStatement.setTimestamp(8, Timestamp.valueOf(departureDateTime));
            preparedStatement.setTimestamp(9, Timestamp.valueOf(arrivalDateTime));
            preparedStatement.setString(10, image);
            preparedStatement.setInt(11, userID);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Cruise getCruise(long id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cruises WHERE is_deleted = false AND cruise_id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String cruiseName = resultSet.getString("cruise_name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int duration = resultSet.getInt("duration");
                int capacity = resultSet.getInt("capacity");
                String departurePort = resultSet.getString("departure_port");
                LocalDateTime departureDateTime = resultSet.getTimestamp("departure_date_time").toLocalDateTime();
                String arrivalPort = resultSet.getString("arrival_port");
                LocalDateTime arrivalDateTime = resultSet.getTimestamp("arrival_date_time").toLocalDateTime();
                String imagesFilename = resultSet.getString("images_filename");
                resultSet.close();
                preparedStatement.close();
                return new Cruise(id, cruiseName, description, price, duration, capacity, departurePort, arrivalPort, departureDateTime, arrivalDateTime, imagesFilename);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean updateCruise(long id, String cruiseName, String description, double priceDouble, int duration, int capacityInt, String departurePort, String arrivalPort, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, String image, int userID) {
        if (image != null)
            image = getNewImageLoaction(image);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE cruises SET cruise_name = ?, description = ?, price = ?, duration = ?, capacity = ?, departure_port = ?, arrival_port = ?, departure_date_time = ?, arrival_date_time = ?, images_filename = ? WHERE cruise_id = ? AND added_by = ?");
            preparedStatement.setString(1, cruiseName);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, priceDouble);
            preparedStatement.setInt(4, duration);
            preparedStatement.setInt(5, capacityInt);
            preparedStatement.setString(6, departurePort);
            preparedStatement.setString(7, arrivalPort);
            preparedStatement.setTimestamp(8, Timestamp.valueOf(departureDateTime));
            preparedStatement.setTimestamp(9, Timestamp.valueOf(arrivalDateTime));
            preparedStatement.setString(10, image);
            preparedStatement.setLong(11, id);
            preparedStatement.setInt(12, userID);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteCruise(long id, long user_id) {
        try {
            PreparedStatement delete = connection.prepareStatement("UPDATE cruises SET is_deleted =true WHERE cruise_id = ? AND added_by = ?");
            delete.setLong(1, id);
            delete.setLong(2, user_id);
            return delete.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void getAllPackagesBookings(ArrayList<BookingPackages> bookings) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_packages");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingPackageId = resultSet.getLong("booking_package_id");
                String bookingNumber = resultSet.getString("booking_number");
                long packageId = resultSet.getLong("package_id");
                int noOfPersons = resultSet.getInt("no_of_persons");
                Double price = resultSet.getDouble("total_price");
                Date dateOfJourney = resultSet.getDate("date_of_journey");
                Date dateOfBooking = resultSet.getDate("booked_at");
                int userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Packages packages = getPackage(packageId);
                User user = getUser(userId);
                BookingPackages booking = new BookingPackages(bookingPackageId, bookingNumber, packages, noOfPersons, price, dateOfJourney, dateOfBooking, user, status);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private User getUser(int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String usertype = resultSet.getString("usertype");
                long phoneNumber = resultSet.getLong("phone_number");
                String country = resultSet.getString("country");
                return new User(userId, name, email, usertype, phoneNumber, country);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int getBookingPackageCount() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM bookings_packages");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public int getBookingHotelCount() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM bookings_hotels");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public int getBookingCruiseCount() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM bookings_cruises");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean addBookingPackage(String bookingNumber, long packageId, int noOfPersons, double totalPrice, LocalDate date, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bookings_packages(booking_number,package_id,no_of_persons,total_price,date_of_journey,booked_by) VALUES(?,?,?,?,?,?)");
            preparedStatement.setString(1, bookingNumber);
            preparedStatement.setLong(2, packageId);
            preparedStatement.setInt(3, noOfPersons);
            preparedStatement.setDouble(4, totalPrice);
            preparedStatement.setDate(5, Date.valueOf(date));
            preparedStatement.setInt(6, userId);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void getAllCruiseBookings(ArrayList<BookingCruises> bookings) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_cruises");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingCruiseId = resultSet.getLong("booking_cruise_id");
                String bookingNumber = resultSet.getString("booking_number");
                long cruiseId = resultSet.getLong("cruise_id");
                int noOfPersons = resultSet.getInt("no_of_tickets");
                Double price = resultSet.getDouble("total_price");
                Date dateOfBooking = resultSet.getDate("booked_at");
                int userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Cruise cruise = getCruise(cruiseId);
                User user = getUser(userId);
                BookingCruises booking = new BookingCruises(bookingCruiseId, bookingNumber, cruise, noOfPersons, price, dateOfBooking, user, status);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void getAllHotelBookings(ArrayList<BookingHotel> bookings) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_hotels");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingHotelId = resultSet.getLong("booking_hotel_id");
                String bookingNumber = resultSet.getString("booking_number");
                long hotelId = resultSet.getLong("hotel_id");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double price = resultSet.getDouble("total_price");
                Date dateOfCheckIn = resultSet.getDate("check_in_date");
                Date dateOfCheckOut = resultSet.getDate("check_out_date");
                Date dateOfBooking = resultSet.getDate("booked_at");
                int userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Hotel hotel = getHotel(hotelId);
                User user = getUser(userId);
                BookingHotel booking = new BookingHotel(bookingHotelId, bookingNumber, hotel, noOfRooms, price, dateOfCheckIn, dateOfCheckOut, dateOfBooking, user, status);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public boolean addHotelPackage(String bookingNumber, long hotelId, int noOfRooms, double totalPrice, LocalDate checkInDate, LocalDate checkOutDate, int user) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bookings_hotels(booking_number,hotel_id,no_of_rooms,total_price,check_in_date,check_out_date,booked_by) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setString(1, bookingNumber);
            preparedStatement.setLong(2, hotelId);
            preparedStatement.setInt(3, noOfRooms);
            preparedStatement.setDouble(4, totalPrice);
            preparedStatement.setDate(5, Date.valueOf(checkInDate));
            preparedStatement.setDate(6, Date.valueOf(checkOutDate));
            preparedStatement.setInt(7, user);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void searchPackageBookings(String text, ArrayList<BookingPackages> packageBookings) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_packages INNER JOIN packages ON bookings_packages.package_id = packages.package_id WHERE package_name LIKE ?");
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingPackageId = resultSet.getLong("booking_package_id");
                String bookingNumber = resultSet.getString("booking_number");
                long packageId = resultSet.getLong("package_id");
                int noOfPersons = resultSet.getInt("no_of_persons");
                Double price = resultSet.getDouble("total_price");
                Date dateOfJourney = resultSet.getDate("date_of_journey");
                Date dateOfBooking = resultSet.getDate("booked_at");
                int userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Packages packages = getPackage(packageId);
                User user = getUser(userId);
                BookingPackages booking = new BookingPackages(bookingPackageId, bookingNumber, packages, noOfPersons, price, dateOfJourney, dateOfBooking, user, status);
                packageBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchHotelBookings(String text, ArrayList<BookingHotel> hotelBookings) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_hotels INNER JOIN hotels ON bookings_hotels.hotel_id = hotels.hotel_id WHERE hotel_name LIKE ?");
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingHotelId = resultSet.getLong("booking_hotel_id");
                String bookingNumber = resultSet.getString("booking_number");
                long hotelId = resultSet.getLong("hotel_id");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double price = resultSet.getDouble("total_price");
                Date dateOfCheckIn = resultSet.getDate("check_in_date");
                Date dateOfCheckOut = resultSet.getDate("check_out_date");
                Date dateOfBooking = resultSet.getDate("booked_at");
                int userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Hotel hotel = getHotel(hotelId);
                User user = getUser(userId);
                BookingHotel booking = new BookingHotel(bookingHotelId, bookingNumber, hotel, noOfRooms, price, dateOfCheckIn, dateOfCheckOut, dateOfBooking, user, status);
                hotelBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchCruiseBookings(String text, ArrayList<BookingCruises> cruiseBookings) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_cruises INNER JOIN cruises ON bookings_cruises.cruise_id = cruises.cruise_id WHERE cruise_name LIKE ?");
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingCruiseId = resultSet.getLong("booking_cruise_id");
                String bookingNumber = resultSet.getString("booking_number");
                long cruiseId = resultSet.getLong("cruise_id");
                int noOfPersons = resultSet.getInt("no_of_tickets");
                Double price = resultSet.getDouble("total_price");
                Date dateOfBooking = resultSet.getDate("booked_at");
                int userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Cruise cruise = getCruise(cruiseId);
                User user = getUser(userId);
                BookingCruises booking = new BookingCruises(bookingCruiseId, bookingNumber, cruise, noOfPersons, price, dateOfBooking, user, status);
                cruiseBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addBookingCruises(String bookingNumber, long cruiseId, int noOfTickets, double totalPrice, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bookings_cruises(booking_number,cruise_id,no_of_tickets,total_price,booked_by) VALUES(?,?,?,?,?)");
            preparedStatement.setString(1, bookingNumber);
            preparedStatement.setLong(2, cruiseId);
            preparedStatement.setInt(3, noOfTickets);
            preparedStatement.setDouble(4, totalPrice);
            preparedStatement.setInt(5, userId);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
            return i == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public void cancelCruiseBooking(long bookingId, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bookings_cruises SET booking_status = 'Cancelled' WHERE booking_cruise_id = ? AND booked_by = ?");
            preparedStatement.setLong(1, bookingId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchCruiseBookings(String text, ArrayList<BookingCruises> cruiseBookings, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_cruises INNER JOIN cruises ON bookings_cruises.cruise_id = cruises.cruise_id WHERE cruise_name LIKE ? AND booked_by = ?");
            preparedStatement.setString(1, "%" + text + "%");
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingCruiseId = resultSet.getLong("booking_cruise_id");
                String bookingNumber = resultSet.getString("booking_number");
                long cruiseId = resultSet.getLong("cruise_id");
                int noOfPersons = resultSet.getInt("no_of_tickets");
                Double price = resultSet.getDouble("total_price");
                Date dateOfBooking = resultSet.getDate("booked_at");
                userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Cruise cruise = getCruise(cruiseId);
                User user = getUser(userId);
                BookingCruises booking = new BookingCruises(bookingCruiseId, bookingNumber, cruise, noOfPersons, price, dateOfBooking, user, status);
                cruiseBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchHotelBookings(String text, ArrayList<BookingHotel> hotelBookings, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_hotels INNER JOIN hotels ON bookings_hotels.hotel_id = hotels.hotel_id WHERE hotel_name LIKE ? AND booked_by = ?");
            preparedStatement.setString(1, "%" + text + "%");
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingHotelId = resultSet.getLong("booking_hotel_id");
                String bookingNumber = resultSet.getString("booking_number");
                long hotelId = resultSet.getLong("hotel_id");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double price = resultSet.getDouble("total_price");
                Date dateOfCheckIn = resultSet.getDate("check_in_date");
                Date dateOfCheckOut = resultSet.getDate("check_out_date");
                Date dateOfBooking = resultSet.getDate("booked_at");
                userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Hotel hotel = getHotel(hotelId);
                User user = getUser(userId);
                BookingHotel booking = new BookingHotel(bookingHotelId, bookingNumber, hotel, noOfRooms, price, dateOfCheckIn, dateOfCheckOut, dateOfBooking, user, status);
                hotelBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchPackageBookings(String text, ArrayList<BookingPackages> packageBookings, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_packages INNER JOIN packages ON bookings_packages.package_id = packages.package_id WHERE package_name LIKE ? AND booked_by = ?");
            preparedStatement.setString(1, "%" + text + "%");
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingPackageId = resultSet.getLong("booking_package_id");
                String bookingNumber = resultSet.getString("booking_number");
                long packageId = resultSet.getLong("package_id");
                int noOfPersons = resultSet.getInt("no_of_persons");
                Double price = resultSet.getDouble("total_price");
                Date dateOfJourney = resultSet.getDate("date_of_journey");
                Date dateOfBooking = resultSet.getDate("booked_at");
                userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Packages packages = getPackage(packageId);
                User user = getUser(userId);
                BookingPackages booking = new BookingPackages(bookingPackageId, bookingNumber, packages, noOfPersons, price, dateOfJourney, dateOfBooking, user, status);
                packageBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getAllPackagesBookings(ArrayList<BookingPackages> packageBookings, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_packages WHERE booked_by = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingPackageId = resultSet.getLong("booking_package_id");
                String bookingNumber = resultSet.getString("booking_number");
                long packageId = resultSet.getLong("package_id");
                int noOfPersons = resultSet.getInt("no_of_persons");
                Double price = resultSet.getDouble("total_price");
                Date dateOfJourney = resultSet.getDate("date_of_journey");
                Date dateOfBooking = resultSet.getDate("booked_at");
                userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Packages packages = getPackage(packageId);
                User user = getUser(userId);
                BookingPackages booking = new BookingPackages(bookingPackageId, bookingNumber, packages, noOfPersons, price, dateOfJourney, dateOfBooking, user, status);
                packageBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getAllHotelBookings(ArrayList<BookingHotel> hotelBookings, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_hotels WHERE booked_by = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingHotelId = resultSet.getLong("booking_hotel_id");
                String bookingNumber = resultSet.getString("booking_number");
                long hotelId = resultSet.getLong("hotel_id");
                int noOfRooms = resultSet.getInt("no_of_rooms");
                Double price = resultSet.getDouble("total_price");
                Date dateOfCheckIn = resultSet.getDate("check_in_date");
                Date dateOfCheckOut = resultSet.getDate("check_out_date");
                Date dateOfBooking = resultSet.getDate("booked_at");
                userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Hotel hotel = getHotel(hotelId);
                User user = getUser(userId);
                BookingHotel booking = new BookingHotel(bookingHotelId, bookingNumber, hotel, noOfRooms, price, dateOfCheckIn, dateOfCheckOut, dateOfBooking, user, status);
                hotelBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getAllCruiseBookings(ArrayList<BookingCruises> cruiseBookings, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bookings_cruises WHERE booked_by = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long bookingCruiseId = resultSet.getLong("booking_cruise_id");
                String bookingNumber = resultSet.getString("booking_number");
                long cruiseId = resultSet.getLong("cruise_id");
                int noOfPersons = resultSet.getInt("no_of_tickets");
                Double price = resultSet.getDouble("total_price");
                Date dateOfBooking = resultSet.getDate("booked_at");
                userId = resultSet.getInt("booked_by");
                String status = resultSet.getString("booking_status");
                Cruise cruise = getCruise(cruiseId);
                User user = getUser(userId);
                BookingCruises booking = new BookingCruises(bookingCruiseId, bookingNumber, cruise, noOfPersons, price, dateOfBooking, user, status);
                cruiseBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cancelHotelBooking(long bookingId, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bookings_hotels SET booking_status = 'Cancelled' WHERE booking_hotel_id = ? AND booked_by = ?");
            preparedStatement.setLong(1, bookingId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cancelPackageBooking(long bookingId, int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bookings_packages SET booking_status = 'Cancelled' WHERE booking_package_id = ? AND booked_by = ?");
            preparedStatement.setLong(1, bookingId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getSalesReport(ArrayList<Sales> sales) {
        try {
            CallableStatement callableStatement = connection.prepareCall("{call create_sales_report()}");
            callableStatement.execute();
            callableStatement.close();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sales");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String saleType = resultSet.getString("sale_type");
                String itemName = resultSet.getString("item_name");
                double totalSales = resultSet.getDouble("total_sales");
                double profit = resultSet.getDouble("profit");
                Sales sale = new Sales(saleType, itemName, totalSales, profit);
                sales.add(sale);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getSalesReport(ArrayList<Sales> sales, LocalDate startDate, LocalDate endDate) {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call get_sales_report(?,?)}");
                callableStatement.setDate(1, Date.valueOf(startDate));
                callableStatement.setDate(2, Date.valueOf(endDate));
                ResultSet resultSet = callableStatement.executeQuery();
                while (resultSet.next()) {
                    String saleType = resultSet.getString("sale_type");
                    String itemName = resultSet.getString("item_name");
                    double totalSales = resultSet.getDouble("total_sales");
                    double profit = resultSet.getDouble("profit");
                    Sales sale = new Sales(saleType, itemName, totalSales, profit);
                    sales.add(sale);
                }
                resultSet.close();
                callableStatement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
    }
}