package com.database;

import com.Records.Cruise;
import com.Records.Hotel;
import com.Records.Packages;
import com.Records.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Database {
    Connection connection;
    PreparedStatement checkUser, addUser;

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trip_management_system", "root", "root");
            checkUser = connection.prepareStatement("SELECT user_id,name,email,usertype,phone_number,country FROM users WHERE email = ? AND password = SHA2(?,256)");
            addUser = connection.prepareStatement("INSERT INTO USERS(name,email,password,usertype,phone_number,country) VALUES(?,?, SHA2(?,256),?,?,?)");
        } catch (SQLException ignored) {

        }
    }

    public User checkUser(String email, String password) {
        try {
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
}