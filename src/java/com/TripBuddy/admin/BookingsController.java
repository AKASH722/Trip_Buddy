package com.TripBuddy.admin;

import com.TripBuddy.Records.BookingCruises;
import com.TripBuddy.Records.BookingHotel;
import com.TripBuddy.Records.BookingPackages;
import com.TripBuddy.database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BookingsController implements Initializable {
    @FXML
    public VBox bookingsContainer;
    @FXML
    public ComboBox<String> bookingTypes;
    @FXML
    public TextField searchText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookingTypes.getItems().addAll("All", "Packages", "Hotels", "Cruises");
        bookingTypes.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            bookingsContainer.getChildren().clear();
            if (bookingTypes.getSelectionModel().getSelectedItem().equals("All")) {
                bookingsContainer.getChildren().clear();
                Database database = new Database();
                ArrayList<BookingPackages> packageBookings = new ArrayList<>();
                ArrayList<BookingHotel> hotelBookings = new ArrayList<>();
                ArrayList<BookingCruises> cruiseBookings = new ArrayList<>();
                database.getAllPackagesBookings(packageBookings);
                database.getAllHotelBookings(hotelBookings);
                database.getAllCruiseBookings(cruiseBookings);
                displayPackageBookings(packageBookings);
                displayHotelBookings(hotelBookings);
                displayCruiseBookings(cruiseBookings);
            } else if (bookingTypes.getSelectionModel().getSelectedItem().equals("Packages")) {
                bookingsContainer.getChildren().clear();
                Database database = new Database();
                ArrayList<BookingPackages> bookings = new ArrayList<>();
                database.getAllPackagesBookings(bookings);
                displayPackageBookings(bookings);
            } else if (bookingTypes.getSelectionModel().getSelectedItem().equals("Hotels")) {
                bookingsContainer.getChildren().clear();
                Database database = new Database();
                ArrayList<BookingHotel> bookings = new ArrayList<>();
                database.getAllHotelBookings(bookings);
                displayHotelBookings(bookings);
            } else if (bookingTypes.getSelectionModel().getSelectedItem().equals("Cruises")) {
                bookingsContainer.getChildren().clear();
                Database database = new Database();
                ArrayList<BookingCruises> bookings = new ArrayList<>();
                database.getAllCruiseBookings(bookings);
                displayCruiseBookings(bookings);
            }
        });
    }

    private void displayCruiseBookings(ArrayList<BookingCruises> bookings) {
        if (bookings.isEmpty()) {
            Label noBookings = new Label("No Bookings");
            bookingsContainer.getChildren().add(noBookings);
            return;
        }
        for (BookingCruises booking : bookings) {
            VBox bookingPane = new VBox();
            bookingPane.setStyle("-fx-border-color: #d66bef; -fx-border-width: 2px; -fx-padding: 10px;");
            bookingPane.getStyleClass().add("booking");
            bookingPane.setPadding(new Insets(10));
            Label bookingId = new Label("Booking Number: " + booking.bookingNumber());
            Label cruiseId = new Label("Cruise Name: " + booking.cruise().cruiseName());
            Label noOfPersons = new Label("No of Persons: " + booking.noOfTickets());
            Label price = new Label("Price: " + booking.price());
            Label dateOfBooking = new Label("Date of Booking: " + booking.dateOfBooking());
            Label user = new Label("User: " + booking.user().name());
            Label status = new Label("Status: " + booking.status());
            bookingId.setStyle("-fx-font-weight: bold;-fx-font-size: 13;");
            bookingPane.getChildren().addAll(bookingId, cruiseId, noOfPersons, price, dateOfBooking, user, status);
            bookingsContainer.getChildren().add(bookingPane);
        }
    }

    private void displayHotelBookings(ArrayList<BookingHotel> bookings) {
        if (bookings.isEmpty()) {
            Label noBookings = new Label("No Bookings");
            bookingsContainer.getChildren().add(noBookings);
            return;
        }
        for (BookingHotel booking : bookings) {
            VBox bookingPane = new VBox();
            bookingPane.setStyle("-fx-border-color: #d66bef; -fx-border-width: 2px; -fx-padding: 10px;");
            bookingPane.getStyleClass().add("booking");
            bookingPane.setPadding(new Insets(10));
            Label bookingId = new Label("Booking Number: " + booking.bookingNumber());
            Label hotelId = new Label("Hotel Name: " + booking.hotel().hotelName());
            Label noOfPersons = new Label("No of Persons: " + booking.noOfRooms());
            Label price = new Label("Price: " + booking.price());
            Label dateOfBooking = new Label("Date of Booking: " + booking.dateOfBooking());
            Label user = new Label("User: " + booking.user().name());
            Label status = new Label("Status: " + booking.status());
            bookingId.setStyle("-fx-font-weight: bold;-fx-font-size: 13;");
            bookingPane.getChildren().addAll(bookingId, hotelId, noOfPersons, price, dateOfBooking, user, status);
            bookingsContainer.getChildren().add(bookingPane);
        }
    }

    private void displayPackageBookings(ArrayList<BookingPackages> bookings) {
        if (bookings.isEmpty()) {
            Label noBookings = new Label("No Bookings");
            bookingsContainer.getChildren().add(noBookings);
            return;
        }
        for (BookingPackages booking : bookings) {
            VBox bookingPane = new VBox();
            bookingPane.setStyle("-fx-border-color: #d66bef; -fx-border-width: 2px; -fx-padding: 10px;");
            bookingPane.getStyleClass().add("booking");
            bookingPane.setPadding(new Insets(10));
            Label bookingId = new Label("Booking Number: " + booking.bookingNumber());
            Label packageId = new Label("Package Name: " + booking.packages().packageName());
            Label noOfPersons = new Label("No of Persons: " + booking.noOfPersons());
            Label price = new Label("Price: " + booking.price());
            Label dateOfJourney = new Label("Date of Journey: " + booking.dateOfJourney());
            Label dateOfBooking = new Label("Date of Booking: " + booking.dateOfBooking());
            Label user = new Label("User: " + booking.user().name());
            Label status = new Label("Status: " + booking.status());
            bookingId.setStyle("-fx-font-weight: bold;-fx-font-size: 13;");
            bookingPane.getChildren().addAll(bookingId, packageId, noOfPersons, price, dateOfJourney, dateOfBooking, user, status);
            bookingsContainer.getChildren().add(bookingPane);
        }
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
            ArrayList<BookingPackages> packageBookings = new ArrayList<>();
            ArrayList<BookingHotel> hotelBookings = new ArrayList<>();
            ArrayList<BookingCruises> cruiseBookings = new ArrayList<>();
            database.searchPackageBookings(text, packageBookings);
            database.searchHotelBookings(text, hotelBookings);
            database.searchCruiseBookings(text, cruiseBookings);
            if (packageBookings.isEmpty() && hotelBookings.isEmpty() && cruiseBookings.isEmpty()) {
                dialog.setTitle("Warning");
                dialog.getDialogPane().setContentText("No results found");
                dialog.show();
            } else {
                bookingsContainer.getChildren().clear();
                displayPackageBookings(packageBookings);
                displayHotelBookings(hotelBookings);
                displayCruiseBookings(cruiseBookings);
            }
        }
    }

    public void onClickExportSales(ActionEvent actionEvent) {

    }

    public void onClickViewSales(ActionEvent actionEvent) {
        Database database = new Database();
        database.getSalesReport();
    }
}
