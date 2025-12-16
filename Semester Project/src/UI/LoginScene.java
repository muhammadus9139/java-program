package UI;

// LoginScene.java

import Services.AuthenticationManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;


public class LoginScene {
    private VBox view;
    private SceneManager sceneManager;
    private AuthenticationManager authManager;

    public LoginScene(SceneManager sceneManager, AuthenticationManager authManager) {
        this.sceneManager = sceneManager;
        this.authManager = authManager;
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(50));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");

        // Logo/Icon Area
        VBox logoBox = new VBox(5);
        logoBox.setAlignment(Pos.CENTER);
        Text logo = new Text("📄");
        logo.setStyle("-fx-font-size: 48px;");
        logoBox.getChildren().add(logo);

        Text title = new Text("Professional CV Maker");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2c3e50"));

        Text subtitle = new Text("Login to create stunning CVs");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setFill(Color.web("#7f8c8d"));

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(350);
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(350);
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 5;");

        Button loginButton = new Button("LOGIN");
        loginButton.setMaxWidth(350);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #229954; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        Label messageLabel = new Label();
        messageLabel.setMaxWidth(350);
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("⚠ Please fill in all fields");
                return;
            }

            if (authManager.login(email, password)) {
                messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                messageLabel.setText("✓ Login successful!");
                sceneManager.showDashboardScene();
            } else {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("✗ Invalid email or password");
            }
        });

        passwordField.setOnAction(e -> loginButton.fire());

        Separator separator = new Separator();
        separator.setMaxWidth(350);

        Hyperlink signupLink = new Hyperlink("Don't have an account? Sign Up");
        signupLink.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;");
        signupLink.setOnAction(e -> sceneManager.showSignupScene());

        view.getChildren().addAll(logoBox, title, subtitle, emailField, passwordField,
                loginButton, messageLabel, separator, signupLink);
    }

    public VBox getView() {
        return view;
    }
}

