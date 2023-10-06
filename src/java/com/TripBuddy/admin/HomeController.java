package com.TripBuddy.admin;

import com.TripBuddy.TripBuddy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    public StackPane homePane;
    public BorderPane internalPane;

    public void onClickBookings(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/bookings.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickPackages(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/packages.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickHotel(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/hotels.fxml"));
        try {
            internalPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onClickCruise(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/cruises.fxml"));
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
