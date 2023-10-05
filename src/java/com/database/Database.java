package com.database;

import com.Records.Packages;
import com.Records.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
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

    public void deletePackage(long id, int user_id) {
        try {
            PreparedStatement delete = connection.prepareStatement("UPDATE packages SET is_deleted =true WHERE package_id = ? AND created_by = ?");
            delete.setLong(1, id);
            delete.setInt(2, user_id);
            delete.executeUpdate();
        } catch (Exception ignored) {

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

    public void addPackage(String packageName, String description, Double priceDouble, int durationInt, String place, String city, String state, String country, String image, int user_id) {
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
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ignored) {

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

    public void updatePackage(String packageName, String description, Double priceDouble, int durationInt, String place, String city, String state, String country, String image, long id, int user_id) {
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
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ignored) {

        }
    }
}