package com.admin;

import com.Records.Packages;
import com.database.Database;
import com.login.TripBuddy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PackageController implements Initializable {
    @FXML
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

    private void onUpdateButtonAction(ActionEvent actionEvent) {
        Button update = (Button) actionEvent.getSource();
        long id = Long.parseLong(update.getId());
        Database database = new Database();
        Packages packages = database.getPackage(id);
        database.close();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        Label packageNameLabel = new Label("Name:");
        TextField packageNameTextField = new TextField(packages.packageName());
        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField(packages.description());
        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField("" + packages.price());
        Label durationLabel = new Label("Duration (in days):");
        TextField durationTextField = new TextField("" + packages.duration());
        Label placeLabel = new Label("Place:");
        TextField placeTextField = new TextField(packages.destinationPlace());
        Label cityLabel = new Label("City:");
        TextField cityTextField = new TextField(packages.destinationCity());
        Label stateLabel = new Label("State:");
        TextField stateTextField = new TextField(packages.destinationState());
        Label countryLabel = new Label("Country:");
        TextField countryTextField = new TextField(packages.destinationCountry());
        Label imageLabel = new Label("Image:");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        grid.add(packageNameLabel, 0, 0);
        grid.add(packageNameTextField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionTextField, 1, 1);
        grid.add(priceLabel, 0, 2);
        grid.add(priceTextField, 1, 2);
        grid.add(durationLabel, 0, 3);
        grid.add(durationTextField, 1, 3);
        grid.add(placeLabel, 0, 4);
        grid.add(placeTextField, 1, 4);
        grid.add(cityLabel, 0, 5);
        grid.add(cityTextField, 1, 5);
        grid.add(stateLabel, 0, 6);
        grid.add(stateTextField, 1, 6);
        grid.add(countryLabel, 0, 7);
        grid.add(countryTextField, 1, 7);
        grid.add(imageLabel, 0, 8);
        Button imageButton = new Button("Choose Image");
        imageButton.setId(packages.imagesFilename());
        imageButton.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
        grid.add(imageButton, 1, 8);
        imageButton.setOnAction(actionEvent1 -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageButton.setText(file.getName());
                imageButton.setId(file.getAbsolutePath());
            }
        });
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle("Update Package");
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.YES);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        Dialog<String> dialogWarning = new Dialog<>();
        dialogWarning.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == saveButtonType) {
                try {
                    String packageName = packageNameTextField.getText();
                    String description = descriptionTextField.getText();
                    String price = priceTextField.getText();
                    String duration = durationTextField.getText();
                    String place = placeTextField.getText();
                    String city = cityTextField.getText();
                    String state = stateTextField.getText();
                    String country = countryTextField.getText();
                    String image = imageButton.getId();
                    if (packageName.isEmpty() || description.isEmpty() || price.isEmpty() || duration.isEmpty() || place.isEmpty() || city.isEmpty() || state.isEmpty() || country.isEmpty()) {
                        dialogWarning.setTitle("Warning");
                        dialogWarning.getDialogPane().setContentText("Please fill all the fields");
                        dialogWarning.showAndWait();
                    } else {
                        Double priceDouble = Double.parseDouble(price);
                        int durationInt = Integer.parseInt(duration);
                        Database database1 = new Database();
                        database1.updatePackage(packageName, description, priceDouble, durationInt, place, city, state, country, image, id, TripBuddy.user.user_id());
                        database1.close();
                        break;
                    }
                } catch (NumberFormatException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter a valid number for price and duration");
                    dialogWarning.showAndWait();
                }
            } else {
                break;
            }
        }
        onClickRefresh(null);
    }

    public void onDeleteButtonAction(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Warning");
        dialog.setContentText("Are you sure you want to delete this package?");
        ButtonType yesButtonType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(yesButtonType, noButtonType);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == yesButtonType) {
            Button delete = (Button) actionEvent.getSource();
            long id = Long.parseLong(delete.getId());
            Database database = new Database();
            database.deletePackage(id, TripBuddy.user.user_id());
            database.close();
            dialog.setTitle("Successfully Deleted");
            dialog.setContentText("Are you sure you want to delete this package?");
        }
        onClickRefresh(null);
    }

    public void onClickViewPackages(ActionEvent actionEvent) {
        initialize(null, null);
    }

    public void onClickAddPackages(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        Label packageNameLabel = new Label("Package Name:");
        TextField packageNameTextField = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField();
        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField();
        Label durationLabel = new Label("Duration (in days):");
        TextField durationTextField = new TextField();
        Label placeLabel = new Label("Place:");
        TextField placeTextField = new TextField();
        Label cityLabel = new Label("City:");
        TextField cityTextField = new TextField();
        Label stateLabel = new Label("State:");
        TextField stateTextField = new TextField();
        Label countryLabel = new Label("Country:");
        TextField countryTextField = new TextField();
        Label imageLabel = new Label("Image:");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        grid.add(packageNameLabel, 0, 0);
        grid.add(packageNameTextField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionTextField, 1, 1);
        grid.add(priceLabel, 0, 2);
        grid.add(priceTextField, 1, 2);
        grid.add(durationLabel, 0, 3);
        grid.add(durationTextField, 1, 3);
        grid.add(placeLabel, 0, 4);
        grid.add(placeTextField, 1, 4);
        grid.add(cityLabel, 0, 5);
        grid.add(cityTextField, 1, 5);
        grid.add(stateLabel, 0, 6);
        grid.add(stateTextField, 1, 6);
        grid.add(countryLabel, 0, 7);
        grid.add(countryTextField, 1, 7);
        grid.add(imageLabel, 0, 8);
        Button imageButton = new Button("Choose Image");
        imageButton.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
        grid.add(imageButton, 1, 8);
        imageButton.setOnAction(actionEvent1 -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageButton.setText(file.getName());
                imageButton.setId(file.getAbsolutePath());
            }
        });
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle("Add Package");
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.YES);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        Dialog<String> dialogWarning = new Dialog<>();
        dialogWarning.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == saveButtonType) {
                try {
                    String packageName = packageNameTextField.getText();
                    String description = descriptionTextField.getText();
                    String price = priceTextField.getText();
                    String duration = durationTextField.getText();
                    String place = placeTextField.getText();
                    String city = cityTextField.getText();
                    String state = stateTextField.getText();
                    String country = countryTextField.getText();
                    String image = imageButton.getId();
                    if (packageName.isEmpty() || description.isEmpty() || price.isEmpty() || duration.isEmpty() || place.isEmpty() || city.isEmpty() || state.isEmpty() || country.isEmpty()) {
                        dialogWarning.setTitle("Warning");
                        dialogWarning.getDialogPane().setContentText("Please fill all the fields");
                        dialogWarning.showAndWait();
                    } else {
                        Double priceDouble = Double.parseDouble(price);
                        int durationInt = Integer.parseInt(duration);
                        Database database = new Database();
                        database.addPackage(packageName, description, priceDouble, durationInt, place, city, state, country, image, TripBuddy.user.user_id());
                        database.close();
                        break;
                    }
                } catch (NumberFormatException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter a valid number for price and duration");
                    dialogWarning.showAndWait();
                }
            } else {
                break;
            }
        }
        onClickRefresh(null);
    }

    public void onClickRefresh(ActionEvent actionEvent) {
        initialize(null, null);
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

    private void displayPackages(ArrayList<Packages> packagesList) {
        packagesContainer.getChildren().clear();
        for (Packages packages : packagesList) {
            HBox packageInfoContainer = new HBox();
            String fileName = packages.imagesFilename();
            if (fileName == null) {
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
                    Scene imageScene = new Scene(imagePane, 800,600);
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
            Button delete_button = new Button("Delete");
            delete_button.setId("" + packages.packageId());
            delete_button.setOnAction(this::onDeleteButtonAction);
            delete_button.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            Button update_button = new Button("Update");
            update_button.setId("" + packages.packageId());
            update_button.setOnAction(this::onUpdateButtonAction);
            update_button.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            buttonContainer.getChildren().addAll(delete_button, update_button);
            buttonContainer.setSpacing(10);
            detailsContainer.setSpacing(5);
            detailsContainer.getChildren().add(buttonContainer);
            packageInfoContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(imageView, new Insets(10));
            HBox.setMargin(detailsContainer, new Insets(10));
            HBox.setMargin(buttonContainer, new Insets(10));
            packagesContainer.getChildren().add(packageInfoContainer);
        }
    }
}
