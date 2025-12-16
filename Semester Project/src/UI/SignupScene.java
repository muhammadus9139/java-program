package UI;

// SignupScene.java

import Services.AuthenticationManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;


public class SignupScene {
    private VBox view;
    private SceneManager sceneManager;
    private AuthenticationManager authManager;
    private String pendingEmail;
    private String pendingPassword;

    public SignupScene(SceneManager sceneManager, AuthenticationManager authManager) {
        this.sceneManager = sceneManager;
        this.authManager = authManager;
        createView();
    }

    private void createView() {
        view = new VBox(15);
        view.setPadding(new Insets(40));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");

        Text logo = new Text("📝");
        logo.setStyle("-fx-font-size: 48px;");

        Text title = new Text("Create Your Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2c3e50"));

        Text subtitle = new Text("Join us and create professional CVs");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setFill(Color.web("#7f8c8d"));

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(350);
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");
        passwordField.setMaxWidth(350);
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(350);
        confirmPasswordField.setPrefHeight(40);
        confirmPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        TextField otpField = new TextField();
        otpField.setPromptText("Enter 6-digit OTP");
        otpField.setMaxWidth(350);
        otpField.setPrefHeight(40);
        otpField.setVisible(false);
        otpField.setManaged(false);
        otpField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        Button sendOtpButton = new Button("SEND VERIFICATION CODE");
        sendOtpButton.setMaxWidth(350);
        sendOtpButton.setPrefHeight(40);
        sendOtpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold;");

        Button signupButton = new Button("CREATE ACCOUNT");
        signupButton.setMaxWidth(350);
        signupButton.setPrefHeight(40);
        signupButton.setVisible(false);
        signupButton.setManaged(false);
        signupButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold;");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(30, 30);
        progressIndicator.setVisible(false);

        sendOtpButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("⚠ Please fill in all fields");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("⚠ Please enter a valid email address");
                return;
            }

            if (password.length() < 6) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("⚠ Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("⚠ Passwords don't match");
                return;
            }

            pendingEmail = email;
            pendingPassword = password;

            messageLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
            messageLabel.setText("📧 Sending verification code...");
            progressIndicator.setVisible(true);
            sendOtpButton.setDisable(true);

            new Thread(() -> {
                boolean sent = authManager.sendOTP(email);
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    sendOtpButton.setDisable(false);

                    if (sent) {
                        messageLabel.setStyle("-fx-text-fill: #257648; -fx-font-weight: bold;");
                        messageLabel.setText("✓ OTP sent to your email! Check inbox/spam.");
                        otpField.setVisible(true);
                        otpField.setManaged(true);
                        signupButton.setVisible(true);
                        signupButton.setManaged(true);
                        sendOtpButton.setVisible(false);
                        sendOtpButton.setManaged(false);
                    } else {
                        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        messageLabel.setText("✗ Failed to send OTP. Check EmailService configuration.");
                    }
                });
            }).start();
        });

        signupButton.setOnAction(e -> {
            String otp = otpField.getText().trim();

            if (otp.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("⚠ Please enter the OTP");
                return;
            }

            if (authManager.verifyOTP(pendingEmail, otp)) {
                if (authManager.signup(pendingEmail, pendingPassword)) {
                    messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    messageLabel.setText("✓ Account created successfully! Redirecting...");

                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ex) {
                        }
                        javafx.application.Platform.runLater(() -> sceneManager.showLoginScene());
                    }).start();
                } else {
                    messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    messageLabel.setText("✗ Email already registered");
                }
            } else {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                messageLabel.setText("✗ Invalid or expired OTP");
            }
        });

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;");
        loginLink.setOnAction(e -> sceneManager.showLoginScene());

        view.getChildren().addAll(logo, title, subtitle, emailField, passwordField,
                confirmPasswordField, sendOtpButton, otpField, signupButton,
                progressIndicator, messageLabel, loginLink);
    }

    public VBox getView() {
        return view;
    }
}