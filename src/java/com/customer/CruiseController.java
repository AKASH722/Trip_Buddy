package com.customer;

import com.Records.Cruise;
import com.database.Database;
import javafx.event.ActionEvent;
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

public class CruiseController implements Initializable {

    public VBox cruisesContainer;
    public TextField searchText;
    public Button search;

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
            ArrayList<Cruise> cruisesList = new ArrayList<>();
            database.searchCruises(cruisesList, text); // Assuming you have a method searchCruises in your database class
            database.close();
            if (cruisesList.isEmpty()) {
                dialog.setTitle("Warning");
                dialog.getDialogPane().setContentText("Sorry we couldn't find any cruises with the title " + text);
                dialog.show();
            } else {
                displayCruises(cruisesList); // Call the method to display cruises
            }
        }
    }


    public void onClickViewCruises(ActionEvent actionEvent) {
        initialize(null, null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cruisesContainer.setAlignment(Pos.TOP_CENTER);
        Database database = new Database();
        ArrayList<Cruise> cruisesList = new ArrayList<>();
        database.getAllCruises(cruisesList);
        database.close();
        displayCruises(cruisesList);
    }

    private void displayCruises(ArrayList<Cruise> cruisesList) {
        cruisesContainer.getChildren().clear();
        for (Cruise cruise : cruisesList) {
            HBox cruiseInfoContainer = new HBox();
            String fileName = cruise.imagesFilename();
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
                    imageStage.setTitle(cruise.cruiseName());
                    imageStage.show();
                    Duration duration = Duration.seconds(20);
                    javafx.animation.Timeline timeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(duration, e -> {
                        imageStage.close();
                    }));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            });
            VBox detailsContainer = new VBox();
            Label nameLabel = new Label(cruise.cruiseName());
            Label descriptionLabel = new Label("Description: " + cruise.description());
            Label durationLabel = new Label("Duration: " + cruise.duration() + " days");
            Label capacityLabel = new Label("Capacity: " + cruise.capacity() + " passengers");
            Label departureLabel = new Label("Departure: " + cruise.departurePort() + " at " + cruise.departureDateTime());
            Label arrivalLabel = new Label("Arrival: " + cruise.arrivalPort() + " at " + cruise.arrivalDateTime());
            Label priceLabel = new Label("Price: " + cruise.price());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            descriptionLabel.setStyle("-fx-font-size: 12px;");
            priceLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            durationLabel.setStyle("-fx-font-size: 12px;");
            capacityLabel.setStyle("-fx-font-size: 12px;");
            departureLabel.setStyle("-fx-font-size: 12px;");
            arrivalLabel.setStyle("-fx-font-size: 12px;");
            detailsContainer.getChildren().addAll(nameLabel, descriptionLabel, priceLabel, durationLabel, capacityLabel, departureLabel, arrivalLabel);
            cruiseInfoContainer.getChildren().addAll(imageView, detailsContainer);
            cruiseInfoContainer.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            HBox buttonContainer = new HBox();
            Button bookButton = new Button("Book");
            bookButton.setId("" + cruise.cruiseId());
            bookButton.setOnAction(this::onBookButtonAction);
            bookButton.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            cruiseInfoContainer.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            buttonContainer.getChildren().addAll(bookButton);
            detailsContainer.setSpacing(5);
            detailsContainer.getChildren().add(buttonContainer);
            cruiseInfoContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(imageView, new Insets(10));
            HBox.setMargin(detailsContainer, new Insets(10));
            HBox.setMargin(buttonContainer, new Insets(10));
            cruisesContainer.getChildren().add(cruiseInfoContainer);
        }
    }

    private void onBookButtonAction(ActionEvent actionEvent) {
    }
}
