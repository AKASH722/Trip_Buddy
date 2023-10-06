package com.TripBuddy.customer;

import com.TripBuddy.Records.Hotel;
import com.TripBuddy.TripBuddy;
import com.TripBuddy.database.Database;
import com.TripBuddy.utilities.BookingNumber;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
            Label priceLabel = new Label("Price: " + hotel.price() * 1.3);
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
        Button book = (Button) actionEvent.getSource();
        int hotelId = Integer.parseInt(book.getId());
        Database database = new Database();
        try {
            Hotel hotel = database.getHotel(hotelId);
            database.close();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Booking");
            dialog.setHeaderText("Enter Booking Details");

            // Create UI elements for input
            Label noOfRoomsLabel = new Label("No of rooms");
            TextField noOfRoomsTextField = new TextField("0");
            Label checkInDateLabel = new Label("Check-in date");
            DatePicker checkInDatePicker = new DatePicker();
            Label checkOutDateLabel = new Label("Check-out date");
            DatePicker checkOutDatePicker = new DatePicker();

            GridPane gridPane = new GridPane();
            gridPane.add(noOfRoomsLabel, 0, 0);
            gridPane.add(noOfRoomsTextField, 1, 0);
            gridPane.add(checkInDateLabel, 0, 1);
            gridPane.add(checkInDatePicker, 1, 1);
            gridPane.add(checkOutDateLabel, 0, 2);
            gridPane.add(checkOutDatePicker, 1, 2);
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            dialog.getDialogPane().setContent(gridPane);

            ButtonType bookButton = new ButtonType("Book", ButtonBar.ButtonData.YES);
            dialog.getDialogPane().getButtonTypes().addAll(bookButton, ButtonType.NO);
            while (true) {
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == bookButton) {
                    try {
                        int noOfRooms = Integer.parseInt(noOfRoomsTextField.getText());
                        LocalDate checkInDate = checkInDatePicker.getValue();
                        LocalDate checkOutDate = checkOutDatePicker.getValue();

                        if (noOfRooms <= 0 || checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)) {
                            showWarningDialog("Warning", "Please enter valid booking details.");
                        } else {
                            double totalPrice = hotel.price() * 1.3 * noOfRooms * (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
                            Dialog<ButtonType> confirmationDialog = new Dialog<>();
                            confirmationDialog.setTitle("Confirm");
                            confirmationDialog.setHeaderText("Confirm booking");
                            Label totalPriceLabel = new Label("Total Price for " + noOfRooms + " rooms: $" + totalPrice);
                            confirmationDialog.getDialogPane().setContent(totalPriceLabel);
                            ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                            confirmationDialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);
                            Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
                            if (confirmationResult.isPresent() && confirmationResult.get() == confirmButton) {
                                Database database1 = new Database();
                                String bookingNumber = BookingNumber.generateBookingNumber("hotel");
                                boolean added = database1.addHotelPackage(bookingNumber, hotel.hotelId(), noOfRooms, totalPrice, checkInDate, checkOutDate, TripBuddy.user.user_id());
                                database1.close();
                                if (!added) {
                                    showWarningDialog("Warning", "Sorry we couldn't book your hotel. Please try again later");
                                } else {
                                    showInfoDialog("Success", "Booking successful. Your booking number is " + bookingNumber);
                                    break;
                                }
                            } else {
                                showWarningDialog("Warning", "Booking cancelled");
                            }
                        }
                    } catch (NumberFormatException e) {
                        showWarningDialog("Warning", "Please enter a valid number for no of rooms.");
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void showWarningDialog(String title, String contentText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle(title);
        dialog.setHeaderText(contentText);
        dialog.showAndWait();
    }

    private void showInfoDialog(String title, String contentText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle(title);
        dialog.setHeaderText(contentText);
        dialog.showAndWait();
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
