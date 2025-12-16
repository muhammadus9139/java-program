package UI;

import Services.AuthenticationManager;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class TemplateSelectionScene {

    private SceneManager sceneManager;
    private AuthenticationManager authManager;
    private String cvName;
    private Parent view;  // Only layout, not Scene

    public TemplateSelectionScene(SceneManager sceneManager, AuthenticationManager authManager, String cvName) {
        this.sceneManager = sceneManager;
        this.authManager = authManager;
        this.cvName = cvName;

        createUI(); // Initialize layout
    }

    private void createUI() {
        VBox root = new VBox(); // Layout container
        // TODO: Add template thumbnails, checkboxes, buttons etc.

        view = root;  // ✅ Assign Parent (VBox) to view
    }

    public Parent getView() {
        return view;
    }
}
