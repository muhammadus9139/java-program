package Services;

import javafx.scene.control.TextField;


// Add this validation utility class at the top or in a separate file
public class ValidationUtils {

    // Validate name (no numbers allowed)
    public static boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z\\s'-]+$");
    }

    // Validate email format
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Validate phone (only numbers, spaces, +, -, () allowed)
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9+\\-\\s()]+$");
    }

    // Validate postal code (only numbers and optional dash)
    public static boolean isValidPostalCode(String postal) {
        return postal != null && postal.matches("^[0-9-]+$");
    }

    // Validate city/country (no numbers)
    public static boolean isValidLocation(String location) {
        return location != null && location.matches("^[a-zA-Z\\s',.-]+$");
    }

    // Add red border style for invalid fields
    public static void markFieldInvalid(TextField field, String errorMessage) {
        field.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-color: red; -fx-border-width: 2px;");
        if (!field.getPromptText().contains("")) {
            field.setPromptText("" + errorMessage);
        }
    }

    // Remove red border for valid fields
    public static void markFieldValid(TextField field) {
        field.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-border-color: green; -fx-border-width: 2px;");
    }

    // Reset to default style
    public static void resetFieldStyle(TextField field) {
        field.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
    }
}
