// Main.java
import javafx.application.Application;
import javafx.stage.Stage;
import Services.AuthenticationManager;
import UI.SceneManager;



public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        AuthenticationManager authManager = new AuthenticationManager();
        SceneManager sceneManager = new SceneManager(primaryStage, authManager);
        sceneManager.showLoginScene();

        primaryStage.setTitle("Professional CV Maker");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(750);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}