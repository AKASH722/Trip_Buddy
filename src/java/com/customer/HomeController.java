package com.customer;

import com.login.TripBuddy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    BorderPane internalPane;

    public void onClickViewPackage(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/packages.fxml"));
        internalPane.setCenter(loader.load());
    }

    public void onClickCustomPackage(ActionEvent actionEvent) {

    }

    public void onClickViewHotel(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/hotels.fxml"));
        internalPane.setCenter(loader.load());
    }

    public void onClickViewCruise(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/cruises.fxml"));
        internalPane.setCenter(loader.load());
    }

    public void onClickAccount(ActionEvent actionEvent) {

    }

    public void onClickBookings(ActionEvent actionEvent) {

    }

    public void onClickLogout(ActionEvent actionEvent) {
        TripBuddy.user = null;
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
