package com.TripBuddy.customer;

import com.TripBuddy.TripBuddy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    BorderPane internalPane;

    public void onClickViewPackage(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/packages.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickViewHotel(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/hotels.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickViewCruise(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/cruises.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickBookings(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/bookings.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickLogout(ActionEvent actionEvent) {
        TripBuddy.user = null;
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
