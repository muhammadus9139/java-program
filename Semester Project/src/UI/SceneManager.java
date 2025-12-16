package UI;

// SceneManager.java

import Models.CV;
import Services.AuthenticationManager;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SceneManager {
    private Stage stage;
    private AuthenticationManager authManager;
    private CV currentCV;

    public SceneManager(Stage stage, AuthenticationManager authManager) {
        this.stage = stage;
        this.authManager = authManager;
    }

    public void showLoginScene() {
        LoginScene loginScene = new LoginScene(this, authManager);
        Scene scene = new Scene(loginScene.getView(), 450, 550);
        scene.getStylesheets().add("data:text/css," + getStyles());
        stage.setScene(scene);
    }

    public void showCVPreviewScene(CV cv) {
        CVPreviewScene previewScene = new CVPreviewScene(this, authManager, cv);
        Scene scene = new Scene(previewScene.getView(), 1200, 800);
        stage.setScene(scene);
        stage.setTitle("CV Preview - " + cv.getCvName());
    }

    public void showSignupScene() {
        SignupScene signupScene = new SignupScene(this, authManager);
        Scene scene = new Scene(signupScene.getView(), 450, 650);
        scene.getStylesheets().add("data:text/css," + getStyles());
        stage.setScene(scene);
    }

    public void showDashboardScene() {
        DashboardScene dashboard = new DashboardScene(this, authManager);
        Scene scene = new Scene(dashboard.getView(), 1000, 750);
        scene.getStylesheets().add("data:text/css," + getStyles());
        stage.setScene(scene);
    }

    public void showCVBuilderScene(CV cv) {
        CVBuilderScene builderScene = new CVBuilderScene(this, authManager, cv);
        Scene scene = new Scene(builderScene.getView(), 1200, 800);
        stage.setScene(scene);
        stage.setTitle("CV Builder - " + cv.getCvName());
    }

    public CV getCurrentCV() {
        return currentCV;
    }

    private String getStyles() {
        return ".button { -fx-cursor: hand; -fx-background-radius: 5; } " +
                ".text-field { -fx-background-radius: 5; } " +
                ".label { -fx-text-fill: #2c3e50; }";
    }
}