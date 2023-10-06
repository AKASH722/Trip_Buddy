package com.TripBuddy.admin;

import com.TripBuddy.Records.Hotel;
import com.TripBuddy.TripBuddy;
import com.TripBuddy.database.Database;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
            deleteButton.setOnAction(this::onDeleteHotelButtonAction);
            deleteButton.setStyle("-fx-text-fill: #d66bef; -fx-font-size: 13;");
            hotelInfoContainer.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            Button updateButton = new Button("Update");
            updateButton.setId("" + hotel.hotelId());
            updateButton.setOnAction(this::onUpdateHotelButtonAction);
            updateButton.setStyle("-fx-text-fill: #d66bef; -fx-font-size: 13;");
            buttonContainer.getChildren().addAll(deleteButton, updateButton);
            buttonContainer.setSpacing(10);
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

    private void onDeleteHotelButtonAction(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Warning");
        dialog.setContentText("Are you sure you want to delete this hotel?");
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        ButtonType yesButtonType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(yesButtonType, noButtonType);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == yesButtonType) {
            // Get the ID of the hotel to be deleted
            Button delete = (Button) event.getSource();
            long id = Long.parseLong(delete.getId());
            Database database = new Database();
            boolean deleted = database.deleteHotel(id, TripBuddy.user.user_id());
            database.close();
            // Show a confirmation dialog
            dialog.getDialogPane().getButtonTypes().clear();
            if (deleted) {
                dialog.setTitle("Successful");
                dialog.setContentText("Hotel deleted successfully");
                dialog.showAndWait();
            } else {
                dialog.setTitle("Failed");
                dialog.setContentText("Some error occurred");
                dialog.showAndWait();
            }

            // Optionally, refresh the hotel display after deletion
            onClickRefresh(null);
        }
    }


    private void onUpdateHotelButtonAction(ActionEvent event) {
        Button updateHotel = (Button) event.getSource();
        long hotelId = Long.parseLong(updateHotel.getId());

        // Fetch hotel information based on the hotelId
        Database database = new Database();
        Hotel hotel = database.getHotel(hotelId);
        database.close();

        // Create a GridPane to display and edit hotel information
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        // Create labels and text fields for hotel attributes
        Label hotelNameLabel = new Label("Name:");
        TextField hotelNameTextField = new TextField(hotel.hotelName());

        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField(String.valueOf(hotel.price()));

        Label noOfRoomsLabel = new Label("Number of Rooms:");
        TextField noOfRoomsTextField = new TextField(String.valueOf(hotel.noOfRooms()));

        Label starRatingLabel = new Label("Star Rating:");
        TextField starRatingTextField = new TextField(String.valueOf(hotel.starRating()));

        Label hotelAddressLabel = new Label("Address:");
        TextField hotelAddressTextField = new TextField(hotel.hotelAddress());

        Label hotelCityLabel = new Label("City:");
        TextField hotelCityTextField = new TextField(hotel.hotelCity());

        Label hotelStateLabel = new Label("State:");
        TextField hotelStateTextField = new TextField(hotel.hotelState());

        Label hotelCountryLabel = new Label("Country:");
        TextField hotelCountryTextField = new TextField(hotel.hotelCountry());

        Label imagesFilenameLabel = new Label("Image:");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        // Add labels and text fields to the grid
        grid.add(hotelNameLabel, 0, 0);
        grid.add(hotelNameTextField, 1, 0);

        grid.add(priceLabel, 0, 1);
        grid.add(priceTextField, 1, 1);

        grid.add(noOfRoomsLabel, 0, 2);
        grid.add(noOfRoomsTextField, 1, 2);

        grid.add(starRatingLabel, 0, 3);
        grid.add(starRatingTextField, 1, 3);

        grid.add(hotelAddressLabel, 0, 4);
        grid.add(hotelAddressTextField, 1, 4);

        grid.add(hotelCityLabel, 0, 5);
        grid.add(hotelCityTextField, 1, 5);

        grid.add(hotelStateLabel, 0, 6);
        grid.add(hotelStateTextField, 1, 6);

        grid.add(hotelCountryLabel, 0, 7);
        grid.add(hotelCountryTextField, 1, 7);

        grid.add(imagesFilenameLabel, 0, 8);
        Button imageButton = new Button("Choose Image");
        imageButton.setId(hotel.imagesFilename());
        imageButton.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
        grid.add(imageButton, 1, 8);
        imageButton.setOnAction(actionEvent1 -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageButton.setText(file.getName());
                imageButton.setId(file.getAbsolutePath());
            }
        });

        // Create a dialog for updating the hotel information
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle("Update Hotel");
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.YES);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        Dialog<String> dialogWarning = new Dialog<>();
        dialogWarning.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());

        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == saveButtonType) {
                // Handle updating hotel information here
                try {
                    String updatedHotelName = hotelNameTextField.getText();
                    Double updatedPrice = Double.parseDouble(priceTextField.getText());
                    int updatedNoOfRooms = Integer.parseInt(noOfRoomsTextField.getText());
                    Double updatedStarRating = Double.parseDouble(starRatingTextField.getText());
                    String updatedHotelAddress = hotelAddressTextField.getText();
                    String updatedHotelCity = hotelCityTextField.getText();
                    String updatedHotelState = hotelStateTextField.getText();
                    String updatedHotelCountry = hotelCountryTextField.getText();
                    String updatedImagesFilename = imageButton.getId();

                    if (updatedHotelName.isEmpty() || updatedHotelAddress.isEmpty() || updatedHotelCity.isEmpty()
                        || updatedHotelState.isEmpty() || updatedHotelCountry.isEmpty() || updatedImagesFilename.isEmpty()) {
                        dialogWarning.setTitle("Warning");
                        dialogWarning.getDialogPane().setContentText("Please fill all the required fields.");
                        dialogWarning.showAndWait();
                    } else {
                        Database database1 = new Database();
                        boolean updated = database1.updateHotel(updatedHotelName, updatedPrice, updatedNoOfRooms,
                            updatedStarRating, updatedHotelAddress, updatedHotelCity, updatedHotelState,
                            updatedHotelCountry, updatedImagesFilename, hotelId, TripBuddy.user.user_id());
                        database1.close();
                        if (updated) {
                            dialogWarning.setTitle("Successful");
                            dialogWarning.setContentText("Hotel updated successfully.");
                            dialogWarning.showAndWait();
                        } else {
                            dialogWarning.setTitle("Failed");
                            dialogWarning.setContentText("Some error occurred.");
                            dialogWarning.showAndWait();
                        }

                        break;
                    }
                } catch (NumberFormatException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter valid values for numeric fields.");
                    dialogWarning.showAndWait();
                }
            } else {
                break;
            }
        }
        onClickRefresh(null);
    }


    public void onClickViewHotels(ActionEvent actionEvent) {
        initialize(null, null);
    }

    public void onClickAddHotels(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label hotelNameLabel = new Label("Hotel Name:");
        TextField hotelNameTextField = new TextField();

        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField();

        Label noOfRoomsLabel = new Label("Number of Rooms:");
        TextField noOfRoomsTextField = new TextField();

        Label starRatingLabel = new Label("Star Rating:");
        TextField starRatingTextField = new TextField();

        Label hotelAddressLabel = new Label("Hotel Address:");
        TextField hotelAddressTextField = new TextField();

        Label hotelCityLabel = new Label("City:");
        TextField hotelCityTextField = new TextField();

        Label hotelStateLabel = new Label("State:");
        TextField hotelStateTextField = new TextField();

        Label hotelCountryLabel = new Label("Country:");
        TextField hotelCountryTextField = new TextField();

        Label imagesFilenameLabel = new Label("Image Filename:");

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        grid.add(hotelNameLabel, 0, 0);
        grid.add(hotelNameTextField, 1, 0);

        grid.add(priceLabel, 0, 1);
        grid.add(priceTextField, 1, 1);

        grid.add(noOfRoomsLabel, 0, 2);
        grid.add(noOfRoomsTextField, 1, 2);

        grid.add(starRatingLabel, 0, 3);
        grid.add(starRatingTextField, 1, 3);

        grid.add(hotelAddressLabel, 0, 4);
        grid.add(hotelAddressTextField, 1, 4);

        grid.add(hotelCityLabel, 0, 5);
        grid.add(hotelCityTextField, 1, 5);

        grid.add(hotelStateLabel, 0, 6);
        grid.add(hotelStateTextField, 1, 6);

        grid.add(hotelCountryLabel, 0, 7);
        grid.add(hotelCountryTextField, 1, 7);

        grid.add(imagesFilenameLabel, 0, 8);

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
        dialog.setTitle("Add Hotel");
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
                    String hotelName = hotelNameTextField.getText();
                    Double price = Double.parseDouble(priceTextField.getText());
                    int noOfRooms = Integer.parseInt(noOfRoomsTextField.getText());
                    Double starRating = Double.parseDouble(starRatingTextField.getText());
                    String hotelAddress = hotelAddressTextField.getText();
                    String hotelCity = hotelCityTextField.getText();
                    String hotelState = hotelStateTextField.getText();
                    String hotelCountry = hotelCountryTextField.getText();
                    String imagesFilename = imageButton.getId();

                    if (hotelName.isEmpty() || hotelAddress.isEmpty() || hotelCity.isEmpty() || hotelState.isEmpty() || hotelCountry.isEmpty()) {
                        dialogWarning.setTitle("Warning");
                        dialogWarning.getDialogPane().setContentText("Please fill all the required fields.");
                        dialogWarning.showAndWait();
                    } else {
                        Database database = new Database();
                        boolean added = database.addHotel(hotelName, price, noOfRooms, starRating, hotelAddress, hotelCity, hotelState, hotelCountry, imagesFilename, TripBuddy.user.user_id());
                        database.close();

                        if (added) {
                            dialogWarning.setTitle("Successful");
                            dialogWarning.setContentText("Hotel added successfully.");
                            dialogWarning.showAndWait();
                        } else {
                            dialogWarning.setTitle("Failed");
                            dialogWarning.setContentText("Some error occurred.");
                            dialogWarning.showAndWait();
                        }

                        break;
                    }
                } catch (NumberFormatException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter valid values for numeric fields.");
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
}
