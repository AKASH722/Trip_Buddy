package com.login;

import com.Records.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TripBuddy extends Application {
    public static User user;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
//        loader = new FXMLLoader(getClass().getResource("/fxml/admin/home.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Trip Buddy");
        stage.getIcons().add(new Image("/images/icon.png"));
        stage.show();
    }
}
