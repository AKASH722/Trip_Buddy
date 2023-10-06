package com.TripBuddy.admin;

import com.TripBuddy.Records.BookingCruises;
import com.TripBuddy.Records.BookingHotel;
import com.TripBuddy.Records.BookingPackages;
import com.TripBuddy.Records.Sales;
import com.TripBuddy.database.Database;
import com.TripBuddy.utilities.SalesReport;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
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
        // Create the main dialog
        Dialog<ButtonType> mainDialog = new Dialog<>();
        mainDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> mainDialog.close());
        mainDialog.setTitle("Export Sales");

        // Create a GridPane for the main dialog layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create labels and date pickers for start and end dates
        Label startDateLabel = new Label("Start Date");
        DatePicker startDate = new DatePicker();
        Label endDateLabel = new Label("End Date");
        DatePicker endDate = new DatePicker();

        // Create label and button for choosing a folder
        Label chooseFolderLabel = new Label("Choose Folder");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Button chooseFolder = new Button("Choose Folder");

        // Set the action for the "Choose Folder" button
        chooseFolder.setOnAction(actionEvent1 -> {
            directoryChooser.setTitle("Choose Folder");
            File selectedDirectory = directoryChooser.showDialog(mainDialog.getDialogPane().getScene().getWindow());

            // Update the initial directory for the directory chooser
            if (selectedDirectory != null) {
                directoryChooser.setInitialDirectory(selectedDirectory);
            }
        });

        // Add components to the GridPane
        gridPane.add(startDateLabel, 0, 0);
        gridPane.add(startDate, 1, 0);
        gridPane.add(endDateLabel, 0, 1);
        gridPane.add(endDate, 1, 1);
        gridPane.add(chooseFolderLabel, 0, 2);
        gridPane.add(chooseFolder, 1, 2);

        // Set the content of the main dialog to the GridPane
        mainDialog.getDialogPane().setContent(gridPane);

        // Define button types for the main dialog
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.YES);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        mainDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        // Create a dialog for displaying warnings or success messages
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());

        while (true) {
            // Wait for user action in the main dialog
            Optional<ButtonType> result = mainDialog.showAndWait();

            if (result.isPresent() && result.get() == saveButtonType) {
                LocalDate selectedStartDate = startDate.getValue();
                LocalDate selectedEndDate = endDate.getValue();

                // Check if start and end dates are valid
                if (selectedStartDate == null || selectedEndDate == null) {
                    displayWarning("Please select a start date and end date");
                } else if (selectedStartDate.isAfter(selectedEndDate)) {
                    displayWarning("Start date cannot be after end date");
                } else if (directoryChooser.getInitialDirectory() == null) {
                    displayWarning("Please select a folder");
                } else {
                    // Perform data export
                    Database database = new Database();
                    ArrayList<Sales> sales = new ArrayList<>();
                    database.getSalesReport(sales, selectedStartDate, selectedEndDate);

                    if (sales.isEmpty()) {
                        displayWarning("No sales found");
                    } else {
                        new SalesReport(sales, directoryChooser.getInitialDirectory().getAbsolutePath());
                        displaySuccess("Sales report exported successfully");
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void displayWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.show();
    }

    // Helper method to display a success message
    private void displaySuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.show();
    }

    public void onClickViewSales(ActionEvent actionEvent) {
        bookingsContainer.getChildren().clear();
        Database database = new Database();
        ArrayList<Sales> sales = new ArrayList<>();
        database.getSalesReport(sales);
        TableView<Sales> salesTableView = new TableView<>();
        salesTableView.setEditable(false);
        salesTableView.setPrefWidth(600);
        salesTableView.setPrefHeight(400);
        TableColumn<Sales, String> saleType = new TableColumn<>("Sale Type");
        TableColumn<Sales, String> itemName = new TableColumn<>("Item Name");
        TableColumn<Sales, String> totalSales = new TableColumn<>("Total Sales");
        TableColumn<Sales, String> profit = new TableColumn<>("Profit");
        saleType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sale_type()));
        itemName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().item_name()));
        totalSales.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().total_sales().toString()));
        profit.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().profit().toString()));
        salesTableView.getColumns().addAll(saleType, itemName, totalSales, profit);
        salesTableView.getItems().addAll(sales);
        bookingsContainer.getChildren().add(salesTableView);
    }
}
