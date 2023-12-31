package com.TripBuddy.login;

import com.TripBuddy.Records.User;
import com.TripBuddy.TripBuddy;
import com.TripBuddy.database.Database;
import com.TripBuddy.utilities.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    public void onClickLogin(ActionEvent actionEvent) {
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> dialog.close());
        if (email.getText().isEmpty() || password.getText().isEmpty()) {
            dialog.setTitle("Warning");
            dialog.getDialogPane().setContentText("Please enter all the fields");
            dialog.show();
        } else if (Validator.email(email.getText())) {
            dialog.setTitle("Warning");
            dialog.getDialogPane().setContentText("Please enter a valid email");
            dialog.show();
        } else {
            Database database = new Database();
            User user = database.checkUser(email.getText(), password.getText());
            database.close();
            if (user == null) {
                dialog.setTitle("Login Failed");
                dialog.getDialogPane().setContentText("Incorrect email or password\nPlease try again");
                email.setText("");
                password.setText("");
                dialog.show();
            } else {
                TripBuddy.user = user;
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                FXMLLoader loader;
                if (user.type().equals("Customer")) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/customer/home.fxml"));
                    try {
                        stage.setScene(new Scene(loader.load()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    loader = new FXMLLoader(getClass().getResource("/fxml/admin/home.fxml"));
                    try {
                        stage.setScene(new Scene(loader.load()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                stage.show();
            }
        }
    }

    public void onClickSignUp(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer/SignUp.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
