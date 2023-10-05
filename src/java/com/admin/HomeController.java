package com.admin;

import com.login.TripBuddy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    public StackPane homePane;
    public BorderPane internalPane;

    public void onClickBookings(ActionEvent actionEvent) {
    }

    public void onClickPackages(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/packages.fxml"));
        internalPane.setCenter(loader.load());
    }

    public void onClickHotel(ActionEvent actionEvent) {
    }

    public void onClickFlights(ActionEvent actionEvent) {
    }

    public void onClickCruise(ActionEvent actionEvent) {
    }

    public void onClickLogout(ActionEvent actionEvent) {
        TripBuddy.user = null;
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onClickUsers(ActionEvent actionEvent) {
    }
}
