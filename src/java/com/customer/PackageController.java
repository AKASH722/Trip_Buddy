package com.customer;

import com.Records.Packages;
import com.database.Database;
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

public class PackageController implements Initializable {
    public VBox packagesContainer;
    @FXML
    public TextField searchText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        packagesContainer.setAlignment(Pos.TOP_CENTER);
        Database database = new Database();
        ArrayList<Packages> packagesList = new ArrayList<>();
        database.getAllPackages(packagesList);
        database.close();
        displayPackages(packagesList);
    }

    private void displayPackages(ArrayList<Packages> packagesList) {
        packagesContainer.getChildren().clear();
        for (Packages packages : packagesList) {
            HBox packageInfoContainer = new HBox();
            String fileName = packages.imagesFilename();
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
                    imageStage.setTitle(packages.packageName());
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
            Label nameLabel = new Label(packages.packageName());
            Label descriptionLabel = new Label("Description: " + packages.description());
            Label durationLabel = new Label("Duration: " + packages.duration() + " days");
            Label locationLabel = new Label("Location: " + packages.destinationPlace() + ", " + packages.destinationCity() + ", " + packages.destinationState() + ", " + packages.destinationCountry());
            Label priceLabel = new Label("Price: " + packages.price());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            descriptionLabel.setStyle("-fx-font-size: 12px;");
            priceLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            durationLabel.setStyle("-fx-font-size: 12px;");
            locationLabel.setStyle("-fx-font-size: 12px;");
            detailsContainer.getChildren().addAll(nameLabel, descriptionLabel, priceLabel, durationLabel, locationLabel);
            packageInfoContainer.getChildren().addAll(imageView, detailsContainer);
            packageInfoContainer.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            HBox buttonContainer = new HBox();
            Button bookButton = new Button("Book");
            bookButton.setId("" + packages.packageId());
            bookButton.setOnAction(this::onBookButtonAction);
            bookButton.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            buttonContainer.getChildren().addAll(bookButton);
            detailsContainer.setSpacing(5);
            detailsContainer.getChildren().add(buttonContainer);
            packageInfoContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(imageView, new Insets(10));
            HBox.setMargin(detailsContainer, new Insets(10));
            HBox.setMargin(buttonContainer, new Insets(10));
            packagesContainer.getChildren().add(packageInfoContainer);
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
            ArrayList<Packages> packagesList = new ArrayList<>();
            database.searchPackages(packagesList, text);
            database.close();
            if (packagesList.isEmpty()) {
                dialog.setTitle("Warning");
                dialog.getDialogPane().setContentText("Sorry we couldn't find any packages with the title " + text);
                dialog.show();
            } else {
                displayPackages(packagesList);
            }
        }
    }


    public void onClickViewPackages(ActionEvent actionEvent) {
        initialize(null, null);
    }
}
