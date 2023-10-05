package com.customer;

import com.Records.Hotel;
import com.database.Database;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HotelController implements Initializable {
    @FXML
    public VBox hotelsContainer;
    @FXML
    public TextField searchText;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotelsContainer.setAlignment(Pos.TOP_CENTER);
        Database database = new Database();
        ArrayList<Hotel> hotelsList = new ArrayList<>();
        database.getAllHotels(hotelsList);
        database.close();
        displayHotels(hotelsList);
    }

    private void displayHotels(ArrayList<Hotel> hotelsList) {
        hotelsContainer.getChildren().clear();
        for (Hotel hotel : hotelsList) {
            HBox hotelInfoContainer = new HBox();
            String fileName = hotel.imagesFilename();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "src/resources/images/icon.png";
            }

            Image image = new Image("file:" + fileName);
            double ratio = image.getWidth() / image.getHeight();
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(120);
            imageView.setFitWidth(imageView.getFitHeight() * ratio);
            imageView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    Stage imageStage = new Stage();
                    ImageView largeImageView = new ImageView(image);
                    StackPane imagePane = new StackPane(largeImageView);
                    Scene imageScene = new Scene(imagePane, 800, 600);
                    largeImageView.setFitHeight(550);
                    largeImageView.setFitWidth(largeImageView.getFitHeight() * ratio);
                    imageStage.setScene(imageScene);
                    imageStage.setTitle(hotel.hotelName());
                    imageStage.show();

                    Duration duration = Duration.seconds(20);
                    Timeline timeline = new Timeline(new KeyFrame(duration, e -> imageStage.close()));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            });
            VBox detailsContainer = new VBox();
            Label nameLabel = new Label(hotel.hotelName());
            Label locationLabel = new Label("Location: " + hotel.hotelAddress() + ", " + hotel.hotelCity() + ", " + hotel.hotelState() + ", " + hotel.hotelCountry());
            Label priceLabel = new Label("Price: " + hotel.price());
            Label ratingLabel = new Label("Rating: " + hotel.starRating());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            locationLabel.setStyle("-fx-font-size: 12px;");
            priceLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            ratingLabel.setStyle("-fx-font-size: 12px;");
            detailsContainer.getChildren().addAll(nameLabel, locationLabel, priceLabel, ratingLabel);
            HBox buttonContainer = new HBox();
            Button deleteButton = new Button("Delete");
            deleteButton.setId("" + hotel.hotelId());
            Button bookButton = new Button("Book");
            bookButton.setId("" + hotel.hotelId());
            bookButton.setOnAction(this::onBookButtonAction);
            hotelInfoContainer.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            bookButton.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            buttonContainer.getChildren().addAll(bookButton);
            detailsContainer.setSpacing(5);
            detailsContainer.getChildren().add(buttonContainer);
            hotelInfoContainer.getChildren().addAll(imageView, detailsContainer);
            hotelInfoContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(imageView, new Insets(10));
            HBox.setMargin(detailsContainer, new Insets(10));
            HBox.setMargin(buttonContainer, new Insets(10));
            hotelsContainer.getChildren().add(hotelInfoContainer);
        }
    }

    private void onBookButtonAction(ActionEvent actionEvent) {
    }

    public void onSearchAction(ActionEvent actionEvent) {
        String text = searchText.getText();
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        if (text.isEmpty()) {
            dialog.setTitle("Warning");
            dialog.getDialogPane().setContentText("Please enter a title to search");
            dialog.show();
        } else {
            Database database = new Database();
            ArrayList<Hotel> hotelsList = new ArrayList<>();
            database.searchHotels(hotelsList, text);
            database.close();
            if (hotelsList.isEmpty()) {
                dialog.setTitle("Warning");
                dialog.getDialogPane().setContentText("Sorry we couldn't find any hotels with the title " + text);
                dialog.show();
            } else {
                displayHotels(hotelsList);
            }
        }
    }

    public void onClickViewHotels(ActionEvent actionEvent) {
        initialize(null, null);
    }
}
