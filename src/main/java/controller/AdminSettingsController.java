package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import util.NavigationManager;

public class AdminSettingsController {
    @FXML private Button backButton;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button savePasswordButton;
    @FXML private Button cancelPasswordButton;
    @FXML private CheckBox emailNotificationsCheckBox;
    @FXML private CheckBox smsNotificationsCheckBox;
    @FXML private Button savePreferencesButton;
    @FXML private Button cancelPreferencesButton;
    @FXML private Button backupButton;
    @FXML private Button logoutButton;
    
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = (User) NavigationManager.getInstance().getSharedData("currentUser");
        backButton.setOnAction(e -> handleBackToDashboard());
        cancelPasswordButton.setOnAction(e -> clearPasswordFields());
        savePasswordButton.setOnAction(e -> showAlert("Password change feature coming soon!"));
        savePreferencesButton.setOnAction(e -> showAlert("Preferences saved!", Alert.AlertType.INFORMATION));
        cancelPreferencesButton.setOnAction(e -> resetPreferences());
        backupButton.setOnAction(e -> showAlert("System backup feature coming soon!"));
        logoutButton.setOnAction(e -> NavigationManager.getInstance().showLogin());
    }

    private void handleBackToDashboard() {
        if (currentUser != null && currentUser.getRole().name().equals("ADMIN")) {
            NavigationManager.getInstance().showAdminDashboard();
        } else {
            NavigationManager.getInstance().navigateTo(NavigationManager.LOGIN_SCREEN);
        }
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void resetPreferences() {
        emailNotificationsCheckBox.setSelected(false);
        smsNotificationsCheckBox.setSelected(false);
    }

    private void showAlert(String message) {
        showAlert(message, Alert.AlertType.WARNING);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Admin Settings");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
