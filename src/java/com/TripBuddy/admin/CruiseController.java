package com.TripBuddy.admin;

import com.TripBuddy.Records.Cruise;
import com.TripBuddy.TripBuddy;
import com.TripBuddy.database.Database;
import javafx.event.ActionEvent;
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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
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

    public void onClickAddCruise(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label cruiseNameLabel = new Label("Cruise Name:");
        TextField cruiseNameTextField = new TextField();

        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField();

        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField();

        Label capacityLabel = new Label("Capacity:");
        TextField capacityTextField = new TextField();

        Label departurePortLabel = new Label("Departure Port:");
        TextField departurePortTextField = new TextField();

        Label arrivalPortLabel = new Label("Arrival Port:");
        TextField arrivalPortTextField = new TextField();

        Label departureDateTimeLabel = new Label("Departure Date and Time:");

        Label arrivalDateTimeLabel = new Label("Arrival Date and Time:");
        DatePicker departureDatePicker = new DatePicker();
        ComboBox<String> departureHourComboBox = new ComboBox<>();
        ComboBox<String> departureMinuteComboBox = new ComboBox<>();
        DatePicker arrivalDatePicker = new DatePicker();
        ComboBox<String> arrivalHourComboBox = new ComboBox<>();
        ComboBox<String> arrivalMinuteComboBox = new ComboBox<>();
        for (int hour = 0; hour <= 23; hour++) {
            departureHourComboBox.getItems().add(String.format("%02d", hour)); // Format as two digits (e.g., "01", "02", ..., "23")
            arrivalHourComboBox.getItems().add(String.format("%02d", hour)); // Format as two digits (e.g., "01", "02", ..., "23")
        }

        for (int minute = 0; minute <= 59; minute++) {
            departureMinuteComboBox.getItems().add(String.format("%02d", minute)); // Format as two digits (e.g., "00", "01", ..., "59")
            arrivalMinuteComboBox.getItems().add(String.format("%02d", minute)); // Format as two digits (e.g., "00", "01", ..., "59")
        }
        HBox departureDateTimePicker = new HBox(5);
        departureDateTimePicker.getChildren().addAll(departureDatePicker, departureHourComboBox, departureMinuteComboBox);
        HBox arrivalDateTimePicker = new HBox(5);
        arrivalDateTimePicker.getChildren().addAll(arrivalDatePicker, arrivalHourComboBox, arrivalMinuteComboBox);
        Label imagesLabel = new Label("Images:");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        grid.add(cruiseNameLabel, 0, 0);
        grid.add(cruiseNameTextField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionTextField, 1, 1);
        grid.add(priceLabel, 0, 2);
        grid.add(priceTextField, 1, 2);
        grid.add(capacityLabel, 0, 3);
        grid.add(capacityTextField, 1, 3);
        grid.add(departurePortLabel, 0, 4);
        grid.add(departurePortTextField, 1, 4);
        grid.add(arrivalPortLabel, 0, 5);
        grid.add(arrivalPortTextField, 1, 5);
        grid.add(departureDateTimeLabel, 0, 6);
        grid.add(departureDateTimePicker, 1, 6);
        grid.add(arrivalDateTimeLabel, 0, 7);
        grid.add(arrivalDateTimePicker, 1, 7);
        grid.add(imagesLabel, 0, 8);

        Button imageButton = new Button("Choose Image");
        grid.add(imageButton, 1, 8);
        imageButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageButton.setId(file.getAbsolutePath());
                imageButton.setText(file.getName());
            }
        });

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle("Add Cruise");
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
                    String cruiseName = cruiseNameTextField.getText();
                    String description = descriptionTextField.getText();
                    String price = priceTextField.getText();
                    String capacity = capacityTextField.getText();
                    String departurePort = departurePortTextField.getText();
                    String arrivalPort = arrivalPortTextField.getText();
                    LocalDate departureDate = departureDatePicker.getValue();
                    String departureHour = departureHourComboBox.getValue();
                    String departureMinute = departureMinuteComboBox.getValue();
                    LocalDate arrivalDate = arrivalDatePicker.getValue();
                    String arrivalHour = arrivalHourComboBox.getValue();
                    String arrivalMinute = arrivalMinuteComboBox.getValue();
                    if (cruiseName.isEmpty() || description.isEmpty() || price.isEmpty() ||
                        capacity.isEmpty() || departurePort.isEmpty() || arrivalPort.isEmpty() || departureDate == null || departureHour == null || departureMinute == null || arrivalDate == null || arrivalHour == null || arrivalMinute == null) {
                        dialogWarning.setTitle("Warning");
                        dialogWarning.setContentText("Please fill all the fields");
                        dialogWarning.showAndWait();
                    } else {
                        double priceDouble = Double.parseDouble(price);
                        int duration = (int) ChronoUnit.DAYS.between(departureDate, arrivalDate);
                        int capacityInt = Integer.parseInt(capacity);
                        LocalDateTime departureDateTime = departureDate.atTime(Integer.parseInt(departureHour), Integer.parseInt(departureMinute));
                        LocalDateTime arrivalDateTime = arrivalDate.atTime(Integer.parseInt(arrivalHour), Integer.parseInt(arrivalMinute));
                        if (arrivalDateTime.isBefore(departureDateTime)) {
                            dialogWarning.setTitle("Warning");
                            dialogWarning.setContentText("Arrival date and time cannot be before departure date and time");
                            dialogWarning.showAndWait();
                        } else {
                            Database database1 = new Database();
                            boolean updated = database1.addCruise(cruiseName, description, priceDouble, duration, capacityInt, departurePort, arrivalPort, departureDateTime, arrivalDateTime, imageButton.getId(), TripBuddy.user.user_id());
                            database1.close();
                            if (updated) {
                                dialogWarning.setTitle("Successful");
                                dialogWarning.setContentText("Cruise added successfully");
                                dialogWarning.showAndWait();
                            } else {
                                dialogWarning.setTitle("Failed");
                                dialogWarning.setContentText("Some error occurred");
                                dialogWarning.showAndWait();
                            }
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter valid numbers for price and capacity");
                    dialogWarning.showAndWait();
                } catch (DateTimeException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter valid date and time");
                    dialogWarning.showAndWait();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                break;
            }
            initialize(null, null);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Label departureLabel = new Label("Departure: " + cruise.departurePort() + " at " + cruise.departureDateTime().format(formatter));
            Label arrivalLabel = new Label("Arrival: " + cruise.arrivalPort() + " at " + cruise.arrivalDateTime().format(formatter));
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
            Button delete_button = new Button("Delete");
            delete_button.setId("" + cruise.cruiseId());
            delete_button.setOnAction(this::onDeleteCruiseButtonAction);
            delete_button.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            Button update_button = new Button("Update");
            update_button.setId("" + cruise.cruiseId());
            update_button.setOnAction(this::onUpdateCruiseButtonAction);
            update_button.setStyle("-fx-text-fill: #d66bef;-fx-font-size: 13;");
            buttonContainer.getChildren().addAll(delete_button, update_button);
            buttonContainer.setSpacing(10);
            detailsContainer.setSpacing(5);
            detailsContainer.getChildren().add(buttonContainer);
            cruiseInfoContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(imageView, new Insets(10));
            HBox.setMargin(detailsContainer, new Insets(10));
            HBox.setMargin(buttonContainer, new Insets(10));
            cruisesContainer.getChildren().add(cruiseInfoContainer);
        }
    }

    private void onUpdateCruiseButtonAction(ActionEvent actionEvent) {
        Button update = (Button) actionEvent.getSource();
        long id = Long.parseLong(update.getId());
        Database database = new Database();
        Cruise cruise = database.getCruise(id);
        database.close();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        Label cruiseNameLabel = new Label("Cruise Name:");
        TextField cruiseNameTextField = new TextField(cruise.cruiseName());
        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField(cruise.description());
        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField("" + cruise.price());
        Label capacityLabel = new Label("Capacity:");
        TextField capacityTextField = new TextField("" + cruise.capacity());
        Label departurePortLabel = new Label("Departure Port:");
        TextField departurePortTextField = new TextField(cruise.departurePort());
        Label arrivalPortLabel = new Label("Arrival Port:");
        TextField arrivalPortTextField = new TextField(cruise.arrivalPort());
        Label departureDateTimeLabel = new Label("Departure Date and Time:");
        Label arrivalDateTimeLabel = new Label("Arrival Date and Time:");
        DatePicker departureDatePicker = new DatePicker(cruise.departureDateTime().toLocalDate());
        ComboBox<String> departureHourComboBox = new ComboBox<>();
        ComboBox<String> departureMinuteComboBox = new ComboBox<>();
        DatePicker arrivalDatePicker = new DatePicker(cruise.arrivalDateTime().toLocalDate());
        ComboBox<String> arrivalHourComboBox = new ComboBox<>();
        ComboBox<String> arrivalMinuteComboBox = new ComboBox<>();
        for (int hour = 0; hour <= 23; hour++) {
            departureHourComboBox.getItems().add(String.format("%02d", hour)); // Format as two digits (e.g., "01", "02", ..., "23")
            arrivalHourComboBox.getItems().add(String.format("%02d", hour)); // Format as two digits (e.g., "01", "02", ..., "23")
        }
        for (int minute = 0; minute <= 59; minute++) {
            departureMinuteComboBox.getItems().add(String.format("%02d", minute)); // Format as two digits (e.g., "00", "01", ..., "59")
            arrivalMinuteComboBox.getItems().add(String.format("%02d", minute)); // Format as two digits (e.g., "00", "01", ..., "59")
        }
        departureHourComboBox.setValue(String.format("%02d", cruise.departureDateTime().getHour()));
        departureMinuteComboBox.setValue(String.format("%02d", cruise.departureDateTime().getMinute()));
        arrivalHourComboBox.setValue(String.format("%02d", cruise.arrivalDateTime().getHour()));
        arrivalMinuteComboBox.setValue(String.format("%02d", cruise.arrivalDateTime().getMinute()));
        HBox departureDateTimePicker = new HBox(5);
        departureDateTimePicker.getChildren().addAll(departureDatePicker, departureHourComboBox, departureMinuteComboBox);
        HBox arrivalDateTimePicker = new HBox(5);
        arrivalDateTimePicker.getChildren().addAll(arrivalDatePicker, arrivalHourComboBox, arrivalMinuteComboBox);
        Label imagesLabel = new Label("Images:");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        grid.add(cruiseNameLabel, 0, 0);
        grid.add(cruiseNameTextField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionTextField, 1, 1);
        grid.add(priceLabel, 0, 2);
        grid.add(priceTextField, 1, 2);
        grid.add(capacityLabel, 0, 3);
        grid.add(capacityTextField, 1, 3);
        grid.add(departurePortLabel, 0, 4);
        grid.add(departurePortTextField, 1, 4);
        grid.add(arrivalPortLabel, 0, 5);
        grid.add(arrivalPortTextField, 1, 5);
        grid.add(departureDateTimeLabel, 0, 6);
        grid.add(departureDateTimePicker, 1, 6);
        grid.add(arrivalDateTimeLabel, 0, 7);
        grid.add(arrivalDateTimePicker, 1, 7);
        grid.add(imagesLabel, 0, 8);
        Button imageButton = new Button("Choose Image");
        grid.add(imageButton, 1, 8);
        imageButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageButton.setId(file.getAbsolutePath());
                imageButton.setText(file.getName());
            }
        });
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle("Update Cruise");
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
                    String cruiseName = cruiseNameTextField.getText();
                    String description = descriptionTextField.getText();
                    String price = priceTextField.getText();
                    String capacity = capacityTextField.getText();
                    String departurePort = departurePortTextField.getText();
                    String arrivalPort = arrivalPortTextField.getText();
                    LocalDate departureDate = departureDatePicker.getValue();
                    String departureHour = departureHourComboBox.getValue();
                    String departureMinute = departureMinuteComboBox.getValue();
                    LocalDate arrivalDate = arrivalDatePicker.getValue();
                    String arrivalHour = arrivalHourComboBox.getValue();
                    String arrivalMinute = arrivalMinuteComboBox.getValue();
                    if (cruiseName.isEmpty() || description.isEmpty() || price.isEmpty() ||
                        capacity.isEmpty() || departurePort.isEmpty() || arrivalPort.isEmpty() || departureDate == null || departureHour == null || departureMinute == null || arrivalDate == null || arrivalHour == null || arrivalMinute == null) {
                        dialogWarning.setTitle("Warning");
                        dialogWarning.setContentText("Please fill all the fields");
                        dialogWarning.showAndWait();
                    } else {
                        double priceDouble = Double.parseDouble(price);
                        int duration = (int) ChronoUnit.DAYS.between(departureDate, arrivalDate);
                        int capacityInt = Integer.parseInt(capacity);
                        LocalDateTime departureDateTime = departureDate.atTime(Integer.parseInt(departureHour), Integer.parseInt(departureMinute));
                        LocalDateTime arrivalDateTime = arrivalDate.atTime(Integer.parseInt(arrivalHour), Integer.parseInt(arrivalMinute));
                        if (arrivalDateTime.isBefore(departureDateTime)) {
                            dialogWarning.setTitle("Warning");
                            dialogWarning.setContentText("Arrival date and time cannot be before departure date and time");
                            dialogWarning.showAndWait();
                        } else {
                            Database database1 = new Database();
                            boolean updated = database1.updateCruise(id, cruiseName, description, priceDouble, duration, capacityInt, departurePort, arrivalPort, departureDateTime, arrivalDateTime, imageButton.getId(), TripBuddy.user.user_id());
                            database1.close();
                            if (updated) {
                                dialogWarning.setTitle("Successful");
                                dialogWarning.setContentText("Cruise updated successfully");
                                dialogWarning.showAndWait();
                            } else {
                                dialogWarning.setTitle("Failed");
                                dialogWarning.setContentText("Some error occurred");
                                dialogWarning.showAndWait();
                            }
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter valid numbers for price and capacity");
                    dialogWarning.showAndWait();
                } catch (DateTimeException e) {
                    dialogWarning.setTitle("Warning");
                    dialogWarning.setContentText("Please enter valid date and time");
                    dialogWarning.showAndWait();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                break;
            }
        }
        initialize(null, null);
    }

    private void onDeleteCruiseButtonAction(ActionEvent actionEvent) {
        Button delete = (Button) actionEvent.getSource();
        long id = Long.parseLong(delete.getId());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        dialog.setTitle("Delete Cruise");
        dialog.setContentText("Are you sure you want to delete this cruise?");
        ButtonType yesButtonType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(yesButtonType, noButtonType);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == yesButtonType) {
            Database database = new Database();
            boolean deleted = database.deleteCruise(id, TripBuddy.user.user_id());
            database.close();
            if (deleted) {
                dialog.setTitle("Successful");
                dialog.setContentText("Cruise deleted successfully");
                dialog.showAndWait();
            } else {
                dialog.setTitle("Failed");
                dialog.setContentText("Some error occurred");
                dialog.showAndWait();
            }
        }
        onClickViewCruises(null);
    }
}
