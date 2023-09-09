package com.database;

import com.user.User;

import java.sql.*;

public class Database {
    Connection connection;
    PreparedStatement checkUser,addUser;
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trip_management_system","root","root");
            checkUser = connection.prepareStatement("SELECT user_id,name,email,usertype,phone_number,country FROM users WHERE BINARY email = ? AND password = SHA2(?,256)");
            addUser = connection.prepareStatement("INSERT INTO USERS(name,email,password,usertype,phone_number,country) VALUES(?,?, SHA2(?,256),?,?,?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User checkUser(String email ,String password) {
        try {
            checkUser.setString(1,email);
            checkUser.setString(2,password);
            ResultSet rs = checkUser.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("user_id"),rs.getString("name"),rs.getString("email"),rs.getString("usertype"),rs.getLong("phone_number"),rs.getString("country"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean addNewUser(String name, String email,String password, long phone_number,String country) {
        try {
            addUser.setString(1,name);
            addUser.setString(2,email);
            addUser.setString(3,password);
            addUser.setString(4,"Customer");
            addUser.setLong(5,phone_number);
            addUser.setString(6,country);
            return addUser.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
