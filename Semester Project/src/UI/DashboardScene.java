package UI;

// DashboardScene.java
import java.util.Optional;
import dialogs.TemplateColorSelectionDialog;
import dialogs.TemplateColorSelection;
import Models.CV;
import Services.AuthenticationManager;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;



public class DashboardScene {
    private BorderPane view;
    private SceneManager sceneManager;
    private AuthenticationManager authManager;
    private VBox cvListContainer;

    public DashboardScene(SceneManager sceneManager, AuthenticationManager authManager) {
        this.sceneManager = sceneManager;
        this.authManager = authManager;
        createView();
    }

    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #ecf0f1;");

        // Top Bar
        HBox topBar = createTopBar();
        view.setTop(topBar);

        // Center Content
        VBox centerContent = new VBox(30);
        centerContent.setPadding(new Insets(40));
        centerContent.setAlignment(Pos.TOP_CENTER);

        // Welcome Section
        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);

        Text welcomeText = new Text("Welcome back, " + authManager.getCurrentUser().getEmail());
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeText.setFill(Color.web("#2c3e50"));

        Text subtitleText = new Text("Manage your CVs and create new ones");
        subtitleText.setFont(Font.font("Arial", 16));
        subtitleText.setFill(Color.web("#7f8c8d"));

        welcomeBox.getChildren().addAll(welcomeText, subtitleText);

        // Create New CV Button
        Button createNewBtn = new Button("+ CREATE NEW CV");
        createNewBtn.setPrefWidth(300);
        createNewBtn.setPrefHeight(60);
        createNewBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;");
        createNewBtn.setOnMouseEntered(e -> createNewBtn.setStyle(
                "-fx-background-color: #229954; -fx-text-fill: white; " +
                        "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;"));
        createNewBtn.setOnMouseExited(e -> createNewBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; " +
                        "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;"));

        createNewBtn.setOnAction(e -> {
            // Show template and color selection dialog
            TemplateColorSelectionDialog dialog = new TemplateColorSelectionDialog();
            Optional<TemplateColorSelection> result = dialog.showAndWait();

            if (result.isPresent()) {
                TemplateColorSelection selection = result.get();

                CV newCV = new CV();
                newCV.setTemplateStyle(selection.getTemplateStyle());
                newCV.setSelectedColor(selection.getColor());

                authManager.getCurrentUser().addCV(newCV);
                authManager.saveUsers();
                sceneManager.showCVBuilderScene(newCV);
            }
        });

        // Saved CVs Section
        VBox savedCVsSection = new VBox(15);
        savedCVsSection.setAlignment(Pos.CENTER);
        savedCVsSection.setMaxWidth(800);

        Label savedCVsLabel = new Label("Your Saved CVs");
        savedCVsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        savedCVsLabel.setTextFill(Color.web("#2c3e50"));

        cvListContainer = new VBox(10);
        cvListContainer.setAlignment(Pos.CENTER);
        refreshCVList();

        ScrollPane scrollPane = new ScrollPane(cvListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(300);

        savedCVsSection.getChildren().addAll(savedCVsLabel, scrollPane);

        centerContent.getChildren().addAll(welcomeBox, createNewBtn, savedCVsSection);
        view.setCenter(centerContent);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setSpacing(20);

        Label appTitle = new Label("📄 CV Maker Pro");
        appTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        appTitle.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userEmail = new Label(authManager.getCurrentUser().getEmail());
        userEmail.setTextFill(Color.web("#ecf0f1"));
        userEmail.setFont(Font.font("Arial", 14));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 8 20 8 20; -fx-background-radius: 5;");
        logoutBtn.setOnAction(e -> {
            authManager.logout();
            sceneManager.showLoginScene();
        });

        topBar.getChildren().addAll(appTitle, spacer, userEmail, logoutBtn);
        return topBar;
    }

    private void refreshCVList() {
        cvListContainer.getChildren().clear();

        java.util.List<CV> cvs = authManager.getCurrentUser().getSavedCVs();

        if (cvs.isEmpty()) {
            Label emptyLabel = new Label("No CVs yet. Create your first one!");
            emptyLabel.setFont(Font.font("Arial", 16));
            emptyLabel.setTextFill(Color.web("#95a5a6"));
            cvListContainer.getChildren().add(emptyLabel);
        } else {
            for (int i = 0; i < cvs.size(); i++) {
                CV cv = cvs.get(i);
                cvListContainer.getChildren().add(createCVCard(cv, i));
            }
        }
    }

    private HBox createCVCard(CV cv, int index) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setMaxWidth(750);
        card.setPrefHeight(100);

        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(cv.getCvName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web("#2c3e50"));

        String personalName = cv.getPersonalInfo().getFullName();
        if (!personalName.isEmpty()) {
            Label personLabel = new Label("👤 " + personalName);
            personLabel.setFont(Font.font("Arial", 14));
            personLabel.setTextFill(Color.web("#7f8c8d"));
            infoBox.getChildren().add(personLabel);
        }

        Label dateLabel = new Label("Last modified: " + cv.getLastModified());
        dateLabel.setFont(Font.font("Arial", 12));
        dateLabel.setTextFill(Color.web("#95a5a6"));

        infoBox.getChildren().addAll(nameLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button editBtn = new Button("✏ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-padding: 8 20 8 20; -fx-background-radius: 5;");
        editBtn.setOnAction(e -> sceneManager.showCVBuilderScene(cv));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-padding: 8 20 8 20; -fx-background-radius: 5;");

        final int cvIndex = index;
        deleteBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete CV");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This will permanently delete the CV.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                authManager.getCurrentUser().removeCV(cvIndex);
                authManager.saveUsers();
                refreshCVList();
            }
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(infoBox, spacer, buttonBox);
        return card;
    }

    public BorderPane getView() {
        return view;
    }
}

