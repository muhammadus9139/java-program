package UI;

//.
import Services.ValidationUtils;
import Services.AuthenticationManager;
import Services.TemplatePDFGenerator;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import Models.CVTemplate;
//import dialogs.ExportProgressWindow;
//import dialogs.BulkExportSettings;
//import dialogs.BulkExportDialog;
//import Services.CompletePDFGenerator;
import Services.PDFGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import java.io.File;
import Services.AuthenticationManager;
import Services.TemplatePDFGenerator;

import Models.CV;
import Models.PersonalInfo;
import Models.Experience;
import Models.Education;
import Models.Project;
import Models.Certification;




import java.util.Optional;

public class CVBuilderScene {
    private BorderPane view;
    private SceneManager sceneManager;
    private AuthenticationManager authManager;
    private CV currentCV;
    private VBox contentArea;
    private Label statusLabel;

    public CVBuilderScene(SceneManager sceneManager, AuthenticationManager authManager, CV cv) {
        this.sceneManager = sceneManager;
        this.authManager = authManager;
        this.currentCV = cv;
        createView();
    }

    private void createView() {
        view = new BorderPane();

        // Top Menu Bar
        MenuBar menuBar = createMenuBar();
        view.setTop(menuBar);

        // Left Sidebar
        VBox sidebar = createSidebar();
        view.setLeft(sidebar);

        // Center Content Area
        VBox centerWrapper = new VBox();
        centerWrapper.setStyle("-fx-background-color: #ecf0f1;");

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        centerWrapper.getChildren().add(scrollPane);

        view.setCenter(centerWrapper);

        // Bottom Status Bar
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(10, 20, 10, 20));
        statusBar.setStyle("-fx-background-color: #34495e;");
        statusLabel = new Label("");
        statusLabel.setTextFill(Color.WHITE);
        statusBar.getChildren().add(statusLabel);
        view.setBottom(statusBar);

        showPersonalInfoForm();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #7d8b98;");

        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("💾 Save CV");
        MenuItem exportItem = new MenuItem("📄 Export as PDF");
        MenuItem printItem = new MenuItem("🖨️ Print CV");
        MenuItem backItem = new MenuItem("← Back to Dashboard");

        saveItem.setOnAction(e -> saveCV());
        exportItem.setOnAction(e -> exportToPDF());
        printItem.setOnAction(e -> printCV());
        backItem.setOnAction(e -> {
            saveCV();
            sceneManager.showDashboardScene();
        });

        fileMenu.getItems().addAll(saveItem, exportItem, printItem, new SeparatorMenuItem(), backItem);

        Menu editMenu = new Menu("Edit");
        MenuItem renameItem = new MenuItem("✏ Rename CV");
        renameItem.setOnAction(e -> renameCVDialog());
        editMenu.getItems().add(renameItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        return menuBar;
    }


    private void printCV() {
        // First save the CV
        saveCV();

        // Show preview with print option
        showStatus("Opening preview for printing...");
        sceneManager.showCVPreviewScene(currentCV);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(220);

        Label cvTitle = new Label(currentCV.getCvName());
        cvTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cvTitle.setTextFill(Color.WHITE);
        cvTitle.setWrapText(true);
        cvTitle.setMaxWidth(200);
        cvTitle.setPadding(new Insets(0, 0, 20, 0));

        Button[] buttons = {
                createSidebarButton("👤 Personal Info", () -> showPersonalInfoForm()),
                createSidebarButton("📝 Summary", () -> showSummaryForm()),
                createSidebarButton("💼 Experience", () -> showExperienceForm()),
                createSidebarButton("🎓 Education", () -> showEducationForm()),
                createSidebarButton("🚀 Projects", () -> showProjectsForm()),
                createSidebarButton("🛠 Skills", () -> showSkillsForm()),
                createSidebarButton("🏆 Certifications", () -> showCertificationsForm()),
                createSidebarButton("🌐 Languages", () -> showLanguagesForm())
        };

        sidebar.getChildren().add(cvTitle);
        for (Button btn : buttons) {
            sidebar.getChildren().add(btn);
        }

        return sidebar;
    }

    private Button createSidebarButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 12px; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px; -fx-cursor: hand;"));

        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void showPersonalInfoForm() {
        contentArea.getChildren().clear();

        Label titleLabel = createSectionTitle("Personal Information");
        contentArea.getChildren().add(titleLabel);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Create fields with validation
        TextField nameField = createStyledTextField("Full Name");
        nameField.setText(currentCV.getPersonalInfo().getFullName());
        Label nameError = new Label();
        nameError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextField emailField = createStyledTextField("Email");
        emailField.setText(currentCV.getPersonalInfo().getEmail());
        Label emailError = new Label();
        emailError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextField phoneField = createStyledTextField("Phone");
        phoneField.setText(currentCV.getPersonalInfo().getPhone());
        Label phoneError = new Label();
        phoneError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextField addressField = createStyledTextField("Address");
        addressField.setText(currentCV.getPersonalInfo().getAddress());

        TextField cityField = createStyledTextField("City");
        cityField.setText(currentCV.getPersonalInfo().getCity());
        Label cityError = new Label();
        cityError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextField countryField = createStyledTextField("Country");
        countryField.setText(currentCV.getPersonalInfo().getCountry());
        Label countryError = new Label();
        countryError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextField postalField = createStyledTextField("Postal Code");
        postalField.setText(currentCV.getPersonalInfo().getPostalCode());
        Label postalError = new Label();
        postalError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextField linkedInField = createStyledTextField("LinkedIn URL");
        linkedInField.setText(currentCV.getPersonalInfo().getLinkedIn());

        TextField githubField = createStyledTextField("GitHub URL");
        githubField.setText(currentCV.getPersonalInfo().getGithub());

        TextField websiteField = createStyledTextField("Website");
        websiteField.setText(currentCV.getPersonalInfo().getWebsite());

        // Real-time validation for Name
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    ValidationUtils.resetFieldStyle(nameField);
                    nameError.setText("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    ValidationUtils.markFieldInvalid(nameField, "Enter your name");
                    nameError.setText("❌Cannot enter numbers or special characters");
                } else {
                    ValidationUtils.markFieldValid(nameField);
                    nameError.setText("");
                }
            } catch (Exception e) {
                System.err.println("Error validating name: " + e.getMessage());
            }
        });

        // Real-time validation for Email
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    ValidationUtils.resetFieldStyle(emailField);
                    emailError.setText("");
                } else if (!ValidationUtils.isValidEmail(newVal)) {
                    ValidationUtils.markFieldInvalid(emailField, "Enter valid email");
                    emailError.setText("❌ Please enter a valid email address");
                } else {
                    ValidationUtils.markFieldValid(emailField);
                    emailError.setText("");
                }
            } catch (Exception e) {
                System.err.println("Error validating email: " + e.getMessage());
            }
        });

        // Real-time validation for Phone
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    ValidationUtils.resetFieldStyle(phoneField);
                    phoneError.setText("");
                } else if (!ValidationUtils.isValidPhone(newVal)) {
                    ValidationUtils.markFieldInvalid(phoneField, "Enter phone number");
                    phoneError.setText("❌ Phone can only contain numbers, +, -, (), spaces");
                } else {
                    ValidationUtils.markFieldValid(phoneField);
                    phoneError.setText("");
                }
            } catch (Exception e) {
                System.err.println("Error validating phone: " + e.getMessage());
            }
        });

        // Real-time validation for City
        cityField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    ValidationUtils.resetFieldStyle(cityField);
                    cityError.setText("");
                } else if (!ValidationUtils.isValidLocation(newVal)) {
                    ValidationUtils.markFieldInvalid(cityField, "City");
                    cityError.setText("❌ City cannot contain numbers");
                } else {
                    ValidationUtils.markFieldValid(cityField);
                    cityError.setText("");
                }
            } catch (Exception e) {
                System.err.println("Error validating city: " + e.getMessage());
            }
        });

        // Real-time validation for Country
        countryField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    ValidationUtils.resetFieldStyle(countryField);
                    countryError.setText("");
                } else if (!ValidationUtils.isValidLocation(newVal)) {
                    ValidationUtils.markFieldInvalid(countryField, "Country");
                    countryError.setText("❌ Country cannot contain numbers");
                } else {
                    ValidationUtils.markFieldValid(countryField);
                    countryError.setText("");
                }
            } catch (Exception e) {
                System.err.println("Error validating country: " + e.getMessage());
            }
        });

        // Real-time validation for Postal Code
        postalField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    ValidationUtils.resetFieldStyle(postalField);
                    postalError.setText("");
                } else if (!ValidationUtils.isValidPostalCode(newVal)) {
                    ValidationUtils.markFieldInvalid(postalField, "Only numbers allowed");
                    postalError.setText("❌ Postal code can only contain numbers and dashes");
                } else {
                    ValidationUtils.markFieldValid(postalField);
                    postalError.setText("");
                }
            } catch (Exception e) {
                System.err.println("Error validating postal code: " + e.getMessage());
            }
        });

        // Add fields to grid with error labels on the RIGHT side
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(nameError, 2, 0);

        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(emailError, 2, 1);

        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(phoneError, 2, 2);

        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);

        grid.add(new Label("City:"), 0, 4);
        grid.add(cityField, 1, 4);
        grid.add(cityError, 2, 4);

        grid.add(new Label("Country:"), 0, 5);
        grid.add(countryField, 1, 5);
        grid.add(countryError, 2, 5);

        grid.add(new Label("Postal Code:"), 0, 6);
        grid.add(postalField, 1, 6);
        grid.add(postalError, 2, 6);

        grid.add(new Label("LinkedIn:"), 0, 7);
        grid.add(linkedInField, 1, 7);

        grid.add(new Label("GitHub:"), 0, 8);
        grid.add(githubField, 1, 8);

        grid.add(new Label("Website:"), 0, 9);
        grid.add(websiteField, 1, 9);

        Button saveBtn = createActionButton("💾 Save Information", "#27ae60");
        saveBtn.setOnAction(e -> {
            try {
                // Validate all fields before saving
                boolean isValid = true;
                StringBuilder errorMsg = new StringBuilder("Please fix the following errors:\n\n");

                // Validate Name
                if (!nameField.getText().isEmpty() && !ValidationUtils.isValidName(nameField.getText())) {
                    isValid = false;
                    errorMsg.append("• Name cannot contain numbers\n");
                }

                // Validate Email
                if (!emailField.getText().isEmpty() && !ValidationUtils.isValidEmail(emailField.getText())) {
                    isValid = false;
                    errorMsg.append("• Invalid email format\n");
                }

                // Validate Phone
                if (!phoneField.getText().isEmpty() && !ValidationUtils.isValidPhone(phoneField.getText())) {
                    isValid = false;
                    errorMsg.append("• Phone can only contain numbers\n");
                }

                // Validate City
                if (!cityField.getText().isEmpty() && !ValidationUtils.isValidLocation(cityField.getText())) {
                    isValid = false;
                    errorMsg.append("• City cannot contain numbers\n");
                }

                // Validate Country
                if (!countryField.getText().isEmpty() && !ValidationUtils.isValidLocation(countryField.getText())) {
                    isValid = false;
                    errorMsg.append("• Country cannot contain numbers\n");
                }

                // Validate Postal Code
                if (!postalField.getText().isEmpty() && !ValidationUtils.isValidPostalCode(postalField.getText())) {
                    isValid = false;
                    errorMsg.append("• Postal code can only contain numbers\n");
                }

                if (!isValid) {
                    showErrorAlert("Validation Error", errorMsg.toString());
                    return;
                }

                // If validation passes, save the data
                PersonalInfo info = currentCV.getPersonalInfo();
                info.setFullName(nameField.getText());
                info.setEmail(emailField.getText());
                info.setPhone(phoneField.getText());
                info.setAddress(addressField.getText());
                info.setCity(cityField.getText());
                info.setCountry(countryField.getText());
                info.setPostalCode(postalField.getText());
                info.setLinkedIn(linkedInField.getText());
                info.setGithub(githubField.getText());
                info.setWebsite(websiteField.getText());

                currentCV.updateLastModified();
                saveCV();
                showStatus("✓ Personal information saved successfully!");
                showSummaryForm();

            } catch (Exception ex) {
                System.err.println("Error saving personal info: " + ex.getMessage());
                ex.printStackTrace();
                showErrorAlert("Save Error", "An error occurred while saving: " + ex.getMessage());
            }
        });

        contentArea.getChildren().addAll(grid, saveBtn);
    }

    private void showSummaryForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> showPersonalInfoForm());

        // Title Label
        Label titleLabel = createSectionTitle("Professional Summary");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        Label subtitleLabel = new Label("Write a compelling summary that highlights your key strengths and career goals");
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        TextArea summaryArea = new TextArea(currentCV.getSummary());
        summaryArea.setPromptText("Example: Results-driven software engineer with 5+ years of experience in full-stack development...");
        summaryArea.setPrefRowCount(8);
        summaryArea.setWrapText(true);
        summaryArea.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        Label charCount = new Label("Characters: " + summaryArea.getText().length());
        charCount.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");
        summaryArea.textProperty().addListener((obs, old, newVal) -> {
            charCount.setText("Characters: " + newVal.length());
        });

        Button saveBtn = createActionButton("💾 Save Summary", "#27ae60");
        saveBtn.setOnAction(e -> {
            currentCV.setSummary(summaryArea.getText());
            currentCV.updateLastModified();
            saveCV();
            showStatus("Professional summary saved!");
            showExperienceForm();
        });

        contentArea.getChildren().addAll(titleBox, subtitleLabel, summaryArea, charCount, saveBtn);
    }
    //Back icon here
    private void showExperienceForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button - SIMPLE < SYMBOL ✅
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Important!
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> {
            saveCV();
            showSummaryForm();
        });

        // Title Label
        Label titleLabel = createSectionTitle("Work Experience");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        // Experience List
        VBox experienceList = new VBox(15);
        experienceList.setPadding(new Insets(10));

        for (int i = 0; i < currentCV.getExperienceList().size(); i++) {
            Experience exp = currentCV.getExperienceList().get(i);
            experienceList.getChildren().add(createExperienceCard(exp, i));
        }

        Button addBtn = createActionButton("+ Add New Experience", "#3498db");
        addBtn.setOnAction(e -> showAddExperienceDialog());

        Button nextBtn = createActionButton("Next ➜", "#27ae60");
        nextBtn.setOnAction(e -> {
            saveCV();
            showEducationForm();
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(addBtn, nextBtn);

        contentArea.getChildren().addAll(titleBox, experienceList, buttonBox);
    }

    private VBox createExperienceCard(Experience exp, int index) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");

        Label jobLabel = new Label(exp.getJobTitle() + " at " + exp.getCompany());
        jobLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label dateLabel = new Label(exp.getStartDate() + " - " + exp.getEndDate() + " | " + exp.getLocation());
        dateLabel.setStyle("-fx-text-fill: #7f8c8d;");

        VBox respBox = new VBox(3);
        for (String resp : exp.getResponsibilities()) {
            Label respLabel = new Label("• " + resp);
            respLabel.setWrapText(true);
            respBox.getChildren().add(respLabel);
        }

        // Buttons in horizontal layout
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button editBtn = new Button("✏️ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        editBtn.setOnMouseEntered(e -> editBtn.setOpacity(0.8));
        editBtn.setOnMouseExited(e -> editBtn.setOpacity(1.0));
        editBtn.setOnAction(e -> showEditExperienceDialog(exp, index));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setOpacity(0.8));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setOpacity(1.0));
        deleteBtn.setOnAction(e -> {
            currentCV.getExperienceList().remove(index);
            currentCV.updateLastModified();
            saveCV();
            showExperienceForm();
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(jobLabel, dateLabel, respBox, buttonBox);
        return card;
    }

    private void showEditExperienceDialog(Experience exp, int index) {
        Dialog<Experience> dialog = new Dialog<>();
        dialog.setTitle("Edit Work Experience");
        dialog.setHeaderText("Update work experience details");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Pre-fill fields with existing data
        TextField jobTitleField = new TextField(exp.getJobTitle());
        jobTitleField.setPromptText("Software Engineer");

        TextField companyField = new TextField(exp.getCompany());
        companyField.setPromptText("Tech Corp");

        TextField locationField = new TextField(exp.getLocation());
        locationField.setPromptText("New York, NY");

        TextField startDateField = new TextField(exp.getStartDate());
        startDateField.setPromptText("Jan 2020");

        TextField endDateField = new TextField(exp.getEndDate());
        endDateField.setPromptText("Dec 2022");

        CheckBox currentlyWorkingBox = new CheckBox("Currently working here");
        currentlyWorkingBox.setSelected(exp.isCurrentlyWorking());

        // Responsibilities as text
        StringBuilder respText = new StringBuilder();
        for (String resp : exp.getResponsibilities()) {
            respText.append(resp).append("\n");
        }

        TextArea responsibilitiesArea = new TextArea(respText.toString());
        responsibilitiesArea.setPromptText("Enter each responsibility on a new line");
        responsibilitiesArea.setPrefRowCount(5);

        currentlyWorkingBox.selectedProperty().addListener((obs, old, newVal) -> {
            endDateField.setDisable(newVal);
            if (newVal) endDateField.setText("Present");
        });

        if (exp.isCurrentlyWorking()) {
            endDateField.setDisable(true);
        }

        grid.add(new Label("Job Title:"), 0, 0);
        grid.add(jobTitleField, 1, 0);
        grid.add(new Label("Company:"), 0, 1);
        grid.add(companyField, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDateField, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDateField, 1, 4);
        grid.add(currentlyWorkingBox, 1, 5);
        grid.add(new Label("Responsibilities:"), 0, 6);
        grid.add(responsibilitiesArea, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Experience updatedExp = new Experience(
                        jobTitleField.getText(),
                        companyField.getText(),
                        locationField.getText(),
                        startDateField.getText(),
                        endDateField.getText(),
                        currentlyWorkingBox.isSelected()
                );

                String[] responsibilities = responsibilitiesArea.getText().split("\n");
                for (String resp : responsibilities) {
                    if (!resp.trim().isEmpty()) {
                        updatedExp.addResponsibility(resp.trim());
                    }
                }
                return updatedExp;
            }
            return null;
        });

        Optional<Experience> result = dialog.showAndWait();
        result.ifPresent(updatedExp -> {
            // Replace the experience at the same index
            currentCV.getExperienceList().set(index, updatedExp);
            currentCV.updateLastModified();
            saveCV();
            showExperienceForm();
            showStatus("Work experience updated!");
        });
    }

    private void showAddExperienceDialog() {
        Dialog<Experience> dialog = new Dialog<>();
        dialog.setTitle("Add Work Experience");
        dialog.setHeaderText("Enter work experience details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Software Engineer");

        TextField companyField = new TextField();
        companyField.setPromptText("Tech Corp");

        TextField locationField = new TextField();
        locationField.setPromptText("New York, NY");

        TextField startDateField = new TextField();
        startDateField.setPromptText("Jan 2020");

        TextField endDateField = new TextField();
        endDateField.setPromptText("Dec 2022");

        CheckBox currentlyWorkingBox = new CheckBox("Currently working here");

        TextArea responsibilitiesArea = new TextArea();
        responsibilitiesArea.setPromptText("Enter each responsibility on a new line");
        responsibilitiesArea.setPrefRowCount(5);

        // Real-time validation for Job Title - Text INSIDE the box
        jobTitleField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    jobTitleField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    jobTitleField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    jobTitleField.setPromptText("❌ Numbers not allowed");
                } else {
                    jobTitleField.setStyle("");
                    jobTitleField.setPromptText("Software Engineer");
                }
            } catch (Exception e) {
                System.err.println("Error validating job title: " + e.getMessage());
            }
        });

        // Real-time validation for Company - Text INSIDE the box
        companyField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    companyField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    companyField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    companyField.setPromptText("❌ Numbers not allowed");
                } else {
                    companyField.setStyle("");
                    companyField.setPromptText("Tech Corp");
                }
            } catch (Exception e) {
                System.err.println("Error validating company: " + e.getMessage());
            }
        });

        currentlyWorkingBox.selectedProperty().addListener((obs, old, newVal) -> {
            endDateField.setDisable(newVal);
            if (newVal) endDateField.setText("Present");
            else endDateField.setText("");
        });

        // NO EXTRA ROWS - Same spacing as before
        grid.add(new Label("Job Title:"), 0, 0);
        grid.add(jobTitleField, 1, 0);
        grid.add(new Label("Company:"), 0, 1);
        grid.add(companyField, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDateField, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDateField, 1, 4);
        grid.add(currentlyWorkingBox, 1, 5);
        grid.add(new Label("Responsibilities:"), 0, 6);
        grid.add(responsibilitiesArea, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    // Validate before saving
                    boolean isValid = true;
                    StringBuilder errorMsg = new StringBuilder("Please fix the following errors:\n\n");

                    if (jobTitleField.getText().isEmpty()) {
                        isValid = false;
                        errorMsg.append("• Job title is required\n");
                    } else if (!ValidationUtils.isValidName(jobTitleField.getText())) {
                        isValid = false;
                        errorMsg.append("• Job title cannot contain numbers\n");
                    }

                    if (companyField.getText().isEmpty()) {
                        isValid = false;
                        errorMsg.append("• Company name is required\n");
                    } else if (!ValidationUtils.isValidName(companyField.getText())) {
                        isValid = false;
                        errorMsg.append("• Company name cannot contain numbers\n");
                    }

                    if (!isValid) {
                        showErrorAlert("Validation Error", errorMsg.toString());
                        return null;
                    }

                    Experience exp = new Experience(
                            jobTitleField.getText(),
                            companyField.getText(),
                            locationField.getText(),
                            startDateField.getText(),
                            endDateField.getText(),
                            currentlyWorkingBox.isSelected()
                    );

                    String[] responsibilities = responsibilitiesArea.getText().split("\n");
                    for (String resp : responsibilities) {
                        if (!resp.trim().isEmpty()) {
                            exp.addResponsibility(resp.trim());
                        }
                    }
                    return exp;
                } catch (Exception e) {
                    System.err.println("Error creating experience: " + e.getMessage());
                    showErrorAlert("Error", "An error occurred: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Experience> result = dialog.showAndWait();
        result.ifPresent(exp -> {
            currentCV.getExperienceList().add(exp);
            currentCV.updateLastModified();
            saveCV();
            showExperienceForm();
            showStatus("Work experience added!");
        });
    }
    // Helper Methods
    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(350);
        field.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        return field;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label.setTextFill(Color.web("#2c3e50"));
        label.setPadding(new Insets(0, 0, 15, 0));
        return label;
    }

    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 12px 30px; -fx-background-radius: 5; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void saveCV() {
        authManager.saveUsers();
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            javafx.application.Platform.runLater(() -> statusLabel.setText("Ready"));
        }).start();
    }

    public BorderPane getView() {
        return view;
    }

    // CVBuilderScene.java - Continuation (Add these methods to the CVBuilderScene class)

    private void showEducationForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> {
            saveCV();
            showExperienceForm();
        });

        // Title Label
        Label titleLabel = createSectionTitle("Education");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        VBox educationList = new VBox(15);
        educationList.setPadding(new Insets(10));

        for (int i = 0; i < currentCV.getEducationList().size(); i++) {
            Education edu = currentCV.getEducationList().get(i);
            educationList.getChildren().add(createEducationCard(edu, i));
        }

        Button addBtn = createActionButton("+ Add Education", "#3498db");
        addBtn.setOnAction(e -> showAddEducationDialog());

        Button nextBtn = createActionButton("Next ➜", "#27ae60");
        nextBtn.setOnAction(e -> {
            saveCV();
            showProjectsForm();
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(addBtn, nextBtn);

        contentArea.getChildren().addAll(titleBox, educationList, buttonBox);
    }
    private VBox createEducationCard(Education edu, int index) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");

        String degreeText = edu.getDegree();
        if (!edu.getFieldOfStudy().isEmpty()) {
            degreeText += " in " + edu.getFieldOfStudy();
        }
        Label degreeLabel = new Label(degreeText);
        degreeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label institutionLabel = new Label(edu.getInstitution());
        institutionLabel.setStyle("-fx-text-fill: #3498db;");

        String detailsText = edu.getStartDate() + " - " + edu.getEndDate();
        if (!edu.getLocation().isEmpty()) {
            detailsText += " | " + edu.getLocation();
        }
        if (!edu.getGpa().isEmpty()) {
            detailsText += " | GPA: " + edu.getGpa();
        }
        Label detailsLabel = new Label(detailsText);
        detailsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        card.getChildren().addAll(degreeLabel, institutionLabel, detailsLabel);

        if (!edu.getDescription().isEmpty()) {
            Label descLabel = new Label(edu.getDescription());
            descLabel.setWrapText(true);
            card.getChildren().add(descLabel);
        }

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button editBtn = new Button("✏️ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        editBtn.setOnMouseEntered(e -> editBtn.setOpacity(0.8));
        editBtn.setOnMouseExited(e -> editBtn.setOpacity(1.0));
        editBtn.setOnAction(e -> showEditEducationDialog(edu, index));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setOpacity(0.8));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setOpacity(1.0));
        deleteBtn.setOnAction(e -> {
            currentCV.getEducationList().remove(index);
            currentCV.updateLastModified();
            saveCV();
            showEducationForm();
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().add(buttonBox);

        return card;
    }

    private void showEditEducationDialog(Education edu, int index) {
        Dialog<Education> dialog = new Dialog<>();
        dialog.setTitle("Edit Education");
        dialog.setHeaderText("Update education details");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField degreeField = new TextField(edu.getDegree());
        degreeField.setPromptText("Bachelor of Science");

        TextField fieldField = new TextField(edu.getFieldOfStudy());
        fieldField.setPromptText("Computer Science");

        TextField institutionField = new TextField(edu.getInstitution());
        institutionField.setPromptText("University Name");

        TextField startDateField = new TextField(edu.getStartDate());
        startDateField.setPromptText("Sep 2016");

        TextField endDateField = new TextField(edu.getEndDate());
        endDateField.setPromptText("Jun 2020");

        TextField gpaField = new TextField(edu.getGpa());
        gpaField.setPromptText("3.8/4.0");

        TextField locationField = new TextField(edu.getLocation());
        locationField.setPromptText("City, Country");

        TextArea descArea = new TextArea(edu.getDescription());
        descArea.setPromptText("Additional details, honors, relevant coursework...");
        descArea.setPrefRowCount(3);

        // Real-time validation for Degree (no numbers)
        degreeField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    degreeField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    degreeField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    degreeField.setPromptText("❌ Numbers not allowed");
                } else {
                    degreeField.setStyle("");
                }
            } catch (Exception e) {
                System.err.println("Error validating degree: " + e.getMessage());
            }
        });

        // Real-time validation for Field of Study (no numbers)
        fieldField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    fieldField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    fieldField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    fieldField.setPromptText("❌ Numbers not allowed");
                } else {
                    fieldField.setStyle("");
                }
            } catch (Exception e) {
                System.err.println("Error validating field of study: " + e.getMessage());
            }
        });

        // Real-time validation for Institution (no numbers)
        institutionField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    institutionField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    institutionField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    institutionField.setPromptText("❌ Numbers not allowed");
                } else {
                    institutionField.setStyle("");
                }
            } catch (Exception e) {
                System.err.println("Error validating institution: " + e.getMessage());
            }
        });

        // Real-time validation for GPA (only numbers and decimal point)
        gpaField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    gpaField.setStyle("");
                } else if (!newVal.matches("^[0-9./]+$")) {
                    gpaField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    gpaField.setPromptText("❌ Only numbers allowed");
                } else {
                    gpaField.setStyle("");
                }
            } catch (Exception e) {
                System.err.println("Error validating GPA: " + e.getMessage());
            }
        });

        grid.add(new Label("Degree:"), 0, 0);
        grid.add(degreeField, 1, 0);
        grid.add(new Label("Study program:"), 0, 1);
        grid.add(fieldField, 1, 1);
        grid.add(new Label("Institution:"), 0, 2);
        grid.add(institutionField, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDateField, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDateField, 1, 4);
        grid.add(new Label("GPA:"), 0, 5);
        grid.add(gpaField, 1, 5);
        grid.add(new Label("Location:"), 0, 6);
        grid.add(locationField, 1, 6);
        grid.add(new Label("Description:"), 0, 7);
        grid.add(descArea, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate before saving
                    boolean isValid = true;
                    StringBuilder errorMsg = new StringBuilder("Please fix the following errors:\n\n");

                    if (!degreeField.getText().isEmpty() && !ValidationUtils.isValidName(degreeField.getText())) {
                        isValid = false;
                        errorMsg.append("• Degree cannot contain numbers\n");
                    }

                    if (!fieldField.getText().isEmpty() && !ValidationUtils.isValidName(fieldField.getText())) {
                        isValid = false;
                        errorMsg.append("• Field of study cannot contain numbers\n");
                    }

                    if (!institutionField.getText().isEmpty() && !ValidationUtils.isValidName(institutionField.getText())) {
                        isValid = false;
                        errorMsg.append("• Institution name cannot contain numbers\n");
                    }

                    if (!gpaField.getText().isEmpty() && !gpaField.getText().matches("^[0-9./]+$")) {
                        isValid = false;
                        errorMsg.append("• GPA can only contain numbers\n");
                    }

                    if (!isValid) {
                        showErrorAlert("Validation Error", errorMsg.toString());
                        return null;
                    }

                    return new Education(
                            degreeField.getText(),
                            fieldField.getText(),
                            institutionField.getText(),
                            startDateField.getText(),
                            endDateField.getText(),
                            gpaField.getText(),
                            locationField.getText(),
                            descArea.getText()
                    );
                } catch (Exception e) {
                    System.err.println("Error updating education: " + e.getMessage());
                    showErrorAlert("Error", "An error occurred: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Education> result = dialog.showAndWait();
        result.ifPresent(updatedEdu -> {
            currentCV.getEducationList().set(index, updatedEdu);
            currentCV.updateLastModified();
            saveCV();
            showEducationForm();
            showStatus("Education updated!");
        });
    }
    private void showAddEducationDialog() {
        Dialog<Education> dialog = new Dialog<>();
        dialog.setTitle("Add Education");
        dialog.setHeaderText("Enter education details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField degreeField = new TextField();
        degreeField.setPromptText("Bachelor of Science");

        TextField fieldField = new TextField();
        fieldField.setPromptText("Computer Science");

        TextField institutionField = new TextField();
        institutionField.setPromptText("University Name");

        TextField startDateField = new TextField();
        startDateField.setPromptText("Sep 2016");

        TextField endDateField = new TextField();
        endDateField.setPromptText("Jun 2020");

        TextField gpaField = new TextField();
        gpaField.setPromptText("3.8/4.0");

        TextField locationField = new TextField();
        locationField.setPromptText("City, Country");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Additional details, honors, relevant coursework...");
        descArea.setPrefRowCount(3);

        // Real-time validation for Degree (no numbers)
        degreeField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    degreeField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    degreeField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    degreeField.setPromptText("❌ Numbers not allowed");
                } else {
                    degreeField.setStyle("");
                    degreeField.setPromptText("Bachelor of Science");
                }
            } catch (Exception e) {
                System.err.println("Error validating degree: " + e.getMessage());
            }
        });

        // Real-time validation for Field of Study (no numbers)
        fieldField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    fieldField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    fieldField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    fieldField.setPromptText("❌ Numbers not allowed");
                } else {
                    fieldField.setStyle("");
                    fieldField.setPromptText("Computer Science");
                }
            } catch (Exception e) {
                System.err.println("Error validating field of study: " + e.getMessage());
            }
        });

        // Real-time validation for Institution (no numbers)
        institutionField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    institutionField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    institutionField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    institutionField.setPromptText("❌ Numbers not allowed");
                } else {
                    institutionField.setStyle("");
                    institutionField.setPromptText("University Name");
                }
            } catch (Exception e) {
                System.err.println("Error validating institution: " + e.getMessage());
            }
        });

        // Real-time validation for GPA (only numbers and decimal point)
        gpaField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    gpaField.setStyle("");
                } else if (!newVal.matches("^[0-9./]+$")) {
                    gpaField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    gpaField.setPromptText("❌ Only numbers allowed");
                } else {
                    gpaField.setStyle("");
                    gpaField.setPromptText("3.8/4.0");
                }
            } catch (Exception e) {
                System.err.println("Error validating GPA: " + e.getMessage());
            }
        });

        grid.add(new Label("Degree:"), 0, 0);
        grid.add(degreeField, 1, 0);
        grid.add(new Label("Field of Study:"), 0, 1);
        grid.add(fieldField, 1, 1);
        grid.add(new Label("Institution:"), 0, 2);
        grid.add(institutionField, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDateField, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDateField, 1, 4);
        grid.add(new Label("GPA:"), 0, 5);
        grid.add(gpaField, 1, 5);
        grid.add(new Label("Location:"), 0, 6);
        grid.add(locationField, 1, 6);
        grid.add(new Label("Description:"), 0, 7);
        grid.add(descArea, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    // Validate before saving
                    boolean isValid = true;
                    StringBuilder errorMsg = new StringBuilder("Please fix the following errors:\n\n");

                    if (!degreeField.getText().isEmpty() && !ValidationUtils.isValidName(degreeField.getText())) {
                        isValid = false;
                        errorMsg.append("• Degree cannot contain numbers\n");
                    }

                    if (!fieldField.getText().isEmpty() && !ValidationUtils.isValidName(fieldField.getText())) {
                        isValid = false;
                        errorMsg.append("• Field of study cannot contain numbers\n");
                    }

                    if (!institutionField.getText().isEmpty() && !ValidationUtils.isValidName(institutionField.getText())) {
                        isValid = false;
                        errorMsg.append("• Institution name cannot contain numbers\n");
                    }

                    if (!gpaField.getText().isEmpty() && !gpaField.getText().matches("^[0-9./]+$")) {
                        isValid = false;
                        errorMsg.append("• GPA can only contain numbers\n");
                    }

                    if (!isValid) {
                        showErrorAlert("Validation Error", errorMsg.toString());
                        return null;
                    }

                    return new Education(
                            degreeField.getText(),
                            fieldField.getText(),
                            institutionField.getText(),
                            startDateField.getText(),
                            endDateField.getText(),
                            gpaField.getText(),
                            locationField.getText(),
                            descArea.getText()
                    );
                } catch (Exception e) {
                    System.err.println("Error creating education: " + e.getMessage());
                    showErrorAlert("Error", "An error occurred: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Education> result = dialog.showAndWait();
        result.ifPresent(edu -> {
            currentCV.getEducationList().add(edu);
            currentCV.updateLastModified();
            saveCV();
            showEducationForm();
            showStatus("Education added!");
        });
    }
    private void showProjectsForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> {
            saveCV();
            showEducationForm();
        });

        // Title Label
        Label titleLabel = createSectionTitle("Projects");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        Label subtitleLabel = new Label("Showcase your best projects and technical achievements");
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        VBox projectList = new VBox(15);
        projectList.setPadding(new Insets(10));

        for (int i = 0; i < currentCV.getProjectList().size(); i++) {
            Project proj = currentCV.getProjectList().get(i);
            projectList.getChildren().add(createProjectCard(proj, i));
        }

        Button addBtn = createActionButton("+ Add Project", "#3498db");
        addBtn.setOnAction(e -> showAddProjectDialog());

        Button nextBtn = createActionButton("Next ➜", "#27ae60");
        nextBtn.setOnAction(e -> {
            saveCV();
            showSkillsForm();
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(addBtn, nextBtn);

        contentArea.getChildren().addAll(titleBox, subtitleLabel, projectList, buttonBox);
    }

    private VBox createProjectCard(Project proj, int index) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");

        Label nameLabel = new Label(proj.getProjectName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        if (!proj.getProjectLink().isEmpty()) {
            Label linkLabel = new Label("🔗 " + proj.getProjectLink());
            linkLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 12px;");
            card.getChildren().add(linkLabel);
        }

        if (!proj.getDuration().isEmpty()) {
            Label durationLabel = new Label("Duration: " + proj.getDuration());
            durationLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
            card.getChildren().add(durationLabel);
        }

        if (!proj.getTechnologies().isEmpty()) {
            Label techLabel = new Label("Tech: " + String.join(", ", proj.getTechnologies()));
            techLabel.setStyle("-fx-text-fill: #9b59b6; -fx-font-size: 12px;");
            techLabel.setWrapText(true);
            card.getChildren().add(techLabel);
        }

        Label descLabel = new Label(proj.getDescription());
        descLabel.setWrapText(true);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button editBtn = new Button("✏️ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        editBtn.setOnMouseEntered(e -> editBtn.setOpacity(0.8));
        editBtn.setOnMouseExited(e -> editBtn.setOpacity(1.0));
        editBtn.setOnAction(e -> showEditProjectDialog(proj, index));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setOpacity(0.8));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setOpacity(1.0));
        deleteBtn.setOnAction(e -> {
            currentCV.getProjectList().remove(index);
            currentCV.updateLastModified();
            saveCV();
            showProjectsForm();
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(nameLabel, descLabel, buttonBox);
        return card;
    }

    private void showEditProjectDialog(Project proj, int index) {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Edit Project");
        dialog.setHeaderText("Update project details");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(proj.getProjectName());
        nameField.setPromptText("E-commerce Website");

        TextField linkField = new TextField(proj.getProjectLink());
        linkField.setPromptText("https://github.com/username/project");

        TextField durationField = new TextField(proj.getDuration());
        durationField.setPromptText("Jan 2023 - Mar 2023");

        TextField techField = new TextField(String.join(", ", proj.getTechnologies()));
        techField.setPromptText("React, Node.js, MongoDB (comma-separated)");

        TextArea descArea = new TextArea(proj.getDescription());
        descArea.setPromptText("Describe the project, your role, and key achievements...");
        descArea.setPrefRowCount(5);

        grid.add(new Label("Project Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Project Link:"), 0, 1);
        grid.add(linkField, 1, 1);
        grid.add(new Label("Duration:"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Technologies:"), 0, 3);
        grid.add(techField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Project updatedProj = new Project(
                        nameField.getText(),
                        linkField.getText(),
                        durationField.getText(),
                        descArea.getText()
                );

                String[] techs = techField.getText().split(",");
                for (String tech : techs) {
                    if (!tech.trim().isEmpty()) {
                        updatedProj.addTechnology(tech.trim());
                    }
                }
                return updatedProj;
            }
            return null;
        });

        Optional<Project> result = dialog.showAndWait();
        result.ifPresent(updatedProj -> {
            currentCV.getProjectList().set(index, updatedProj);
            currentCV.updateLastModified();
            saveCV();
            showProjectsForm();
            showStatus("Project updated!");
        });
    }


    private void showAddProjectDialog() {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add Project");
        dialog.setHeaderText("Enter project details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("E-commerce Website");
        TextField linkField = new TextField();
        linkField.setPromptText("https://github.com/username/project");
        TextField durationField = new TextField();
        durationField.setPromptText("Jan 2023 - Mar 2023");
        TextField techField = new TextField();
        techField.setPromptText("React, Node.js, MongoDB (comma-separated)");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the project, your role, and key achievements...");
        descArea.setPrefRowCount(5);

        grid.add(new Label("Project Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Project Link:"), 0, 1);
        grid.add(linkField, 1, 1);
        grid.add(new Label("Duration:"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Technologies:"), 0, 3);
        grid.add(techField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Project proj = new Project(
                        nameField.getText(),
                        linkField.getText(),
                        durationField.getText(),
                        descArea.getText()
                );

                String[] techs = techField.getText().split(",");
                for (String tech : techs) {
                    if (!tech.trim().isEmpty()) {
                        proj.addTechnology(tech.trim());
                    }
                }
                return proj;
            }
            return null;
        });

        Optional<Project> result = dialog.showAndWait();
        result.ifPresent(proj -> {
            currentCV.getProjectList().add(proj);
            currentCV.updateLastModified();
            saveCV();
            showProjectsForm();
            showStatus("Project added!");
        });
    }

    private void showSkillsForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> {
            saveCV();
            showProjectsForm();
        });

        // Title Label
        Label titleLabel = createSectionTitle("Skills");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        Label subtitleLabel = new Label("List your technical and soft skills");
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        TextField skillField = new TextField();
        skillField.setPromptText("Enter a skill and press Enter");
        skillField.setPrefWidth(400);
        skillField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        FlowPane skillsFlow = new FlowPane();
        skillsFlow.setHgap(10);
        skillsFlow.setVgap(10);
        skillsFlow.setPadding(new Insets(15));
        skillsFlow.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");

        for (String skill : currentCV.getSkills()) {
            skillsFlow.getChildren().add(createSkillChip(skill));
        }

        skillField.setOnAction(e -> {
            String skill = skillField.getText().trim();
            if (!skill.isEmpty() && !currentCV.getSkills().contains(skill)) {
                currentCV.getSkills().add(skill);
                skillsFlow.getChildren().add(createSkillChip(skill));
                skillField.clear();
                currentCV.updateLastModified();
                saveCV();
                showStatus("Skill added!");
            }
        });

        Button saveBtn = createActionButton("💾 Save Skills", "#27ae60");
        saveBtn.setOnAction(e -> {
            saveCV();
            showStatus("Skills saved!");
            showCertificationsForm();
        });

        contentArea.getChildren().addAll(titleBox, subtitleLabel, skillField, skillsFlow, saveBtn);
    }

    private HBox createSkillChip(String skill) {
        HBox chip = new HBox(8);
        chip.setPadding(new Insets(8, 12, 8, 12));
        chip.setAlignment(Pos.CENTER);
        chip.setStyle("-fx-background-color: #3498db; -fx-background-radius: 15; -fx-cursor: hand;");

        Label skillLabel = new Label(skill);
        skillLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        Label closeLabel = new Label("×");
        closeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        closeLabel.setOnMouseClicked(e -> {
            currentCV.getSkills().remove(skill);
            currentCV.updateLastModified();
            saveCV();
            showSkillsForm();
        });

        chip.getChildren().addAll(skillLabel, closeLabel);
        return chip;
    }


    private void showCertificationsForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> {
            saveCV();
            showSkillsForm();
        });

        // Title Label
        Label titleLabel = createSectionTitle("Certifications");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        VBox certList = new VBox(15);
        certList.setPadding(new Insets(10));

        for (int i = 0; i < currentCV.getCertifications().size(); i++) {
            Certification cert = currentCV.getCertifications().get(i);
            certList.getChildren().add(createCertificationCard(cert, i));
        }

        Button addBtn = createActionButton("+ Add Certification", "#3498db");
        addBtn.setOnAction(e -> showAddCertificationDialog());

        Button nextBtn = createActionButton("Next ➜", "#27ae60");
        nextBtn.setOnAction(e -> {
            saveCV();
            showLanguagesForm();
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(addBtn, nextBtn);

        contentArea.getChildren().addAll(titleBox, certList, buttonBox);
    }
    private VBox createCertificationCard(Certification cert, int index) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");

        Label nameLabel = new Label(cert.getCertificationName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label orgLabel = new Label(cert.getIssuingOrganization());
        orgLabel.setStyle("-fx-text-fill: #3498db;");

        String details = "Issued: " + cert.getIssueDate();
        if (!cert.getExpiryDate().isEmpty()) {
            details += " | Expires: " + cert.getExpiryDate();
        }
        if (!cert.getCredentialId().isEmpty()) {
            details += " | ID: " + cert.getCredentialId();
        }
        Label detailsLabel = new Label(details);
        detailsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button editBtn = new Button("✏️ Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        editBtn.setOnMouseEntered(e -> editBtn.setOpacity(0.8));
        editBtn.setOnMouseExited(e -> editBtn.setOpacity(1.0));
        editBtn.setOnAction(e -> showEditCertificationDialog(cert, index));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setOpacity(0.8));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setOpacity(1.0));
        deleteBtn.setOnAction(e -> {
            currentCV.getCertifications().remove(index);
            currentCV.updateLastModified();
            saveCV();
            showCertificationsForm();
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(nameLabel, orgLabel, detailsLabel, buttonBox);
        return card;
    }

    private void showEditCertificationDialog(Certification cert, int index) {
        Dialog<Certification> dialog = new Dialog<>();
        dialog.setTitle("Edit Certification");
        dialog.setHeaderText("Update certification details");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(cert.getCertificationName());
        nameField.setPromptText("AWS Certified Solutions Architect");

        TextField orgField = new TextField(cert.getIssuingOrganization());
        orgField.setPromptText("Amazon Web Services");

        TextField issueDateField = new TextField(cert.getIssueDate());
        issueDateField.setPromptText("Jan 2023");

        TextField expiryField = new TextField(cert.getExpiryDate());
        expiryField.setPromptText("Jan 2026 (optional)");

        TextField credentialField = new TextField(cert.getCredentialId());
        credentialField.setPromptText("ABC123XYZ (optional)");

        // Real-time validation for Certification Name (no numbers)
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    nameField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    nameField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    nameField.setPromptText("❌ Numbers not allowed");
                } else {
                    nameField.setStyle("");
                }
            } catch (Exception e) {
                System.err.println("Error validating certification name: " + e.getMessage());
            }
        });

        // Real-time validation for Issuing Organization (no numbers)
        orgField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    orgField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    orgField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    orgField.setPromptText("❌ Numbers not allowed");
                } else {
                    orgField.setStyle("");
                }
            } catch (Exception e) {
                System.err.println("Error validating organization: " + e.getMessage());
            }
        });

        grid.add(new Label("Certification Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Issuing Organization:"), 0, 1);
        grid.add(orgField, 1, 1);
        grid.add(new Label("Issue Date:"), 0, 2);
        grid.add(issueDateField, 1, 2);
        grid.add(new Label("Expiry Date:"), 0, 3);
        grid.add(expiryField, 1, 3);
        grid.add(new Label("Credential ID:"), 0, 4);
        grid.add(credentialField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate before saving
                    boolean isValid = true;
                    StringBuilder errorMsg = new StringBuilder("Please fix the following errors:\n\n");

                    if (!nameField.getText().isEmpty() && !ValidationUtils.isValidName(nameField.getText())) {
                        isValid = false;
                        errorMsg.append("• Certification name cannot contain numbers\n");
                    }

                    if (!orgField.getText().isEmpty() && !ValidationUtils.isValidName(orgField.getText())) {
                        isValid = false;
                        errorMsg.append("• Organization name cannot contain numbers\n");
                    }

                    if (!isValid) {
                        showErrorAlert("Validation Error", errorMsg.toString());
                        return null;
                    }

                    return new Certification(
                            nameField.getText(),
                            orgField.getText(),
                            issueDateField.getText(),
                            expiryField.getText(),
                            credentialField.getText()
                    );
                } catch (Exception e) {
                    System.err.println("Error updating certification: " + e.getMessage());
                    showErrorAlert("Error", "An error occurred: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Certification> result = dialog.showAndWait();
        result.ifPresent(updatedCert -> {
            currentCV.getCertifications().set(index, updatedCert);
            currentCV.updateLastModified();
            saveCV();
            showCertificationsForm();
            showStatus("Certification updated!");
        });
    }
    private void showAddCertificationDialog() {
        Dialog<Certification> dialog = new Dialog<>();
        dialog.setTitle("Add Certification");
        dialog.setHeaderText("Enter certification details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("AWS Certified Solutions Architect");

        TextField orgField = new TextField();
        orgField.setPromptText("Amazon Web Services");

        TextField issueDateField = new TextField();
        issueDateField.setPromptText("Jan 2023");

        TextField expiryField = new TextField();
        expiryField.setPromptText("Jan 2026 (optional)");

        TextField credentialField = new TextField();
        credentialField.setPromptText("ABC123XYZ (optional)");

        // Real-time validation for Certification Name (no numbers)
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    nameField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    nameField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    nameField.setPromptText("❌ Numbers not allowed");
                } else {
                    nameField.setStyle("");
                    nameField.setPromptText("AWS Certified Solutions Architect");
                }
            } catch (Exception e) {
                System.err.println("Error validating certification name: " + e.getMessage());
            }
        });

        // Real-time validation for Issuing Organization (no numbers)
        orgField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    orgField.setStyle("");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    orgField.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    orgField.setPromptText("❌ Numbers not allowed");
                } else {
                    orgField.setStyle("");
                    orgField.setPromptText("Amazon Web Services");
                }
            } catch (Exception e) {
                System.err.println("Error validating organization: " + e.getMessage());
            }
        });

        grid.add(new Label("Certification Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Issuing Organization:"), 0, 1);
        grid.add(orgField, 1, 1);
        grid.add(new Label("Issue Date:"), 0, 2);
        grid.add(issueDateField, 1, 2);
        grid.add(new Label("Expiry Date:"), 0, 3);
        grid.add(expiryField, 1, 3);
        grid.add(new Label("Credential ID:"), 0, 4);
        grid.add(credentialField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    // Validate before saving
                    boolean isValid = true;
                    StringBuilder errorMsg = new StringBuilder("Please fix the following errors:\n\n");

                    if (!nameField.getText().isEmpty() && !ValidationUtils.isValidName(nameField.getText())) {
                        isValid = false;
                        errorMsg.append("• Certification name cannot contain numbers\n");
                    }

                    if (!orgField.getText().isEmpty() && !ValidationUtils.isValidName(orgField.getText())) {
                        isValid = false;
                        errorMsg.append("• Organization name cannot contain numbers\n");
                    }

                    if (!isValid) {
                        showErrorAlert("Validation Error", errorMsg.toString());
                        return null;
                    }

                    return new Certification(
                            nameField.getText(),
                            orgField.getText(),
                            issueDateField.getText(),
                            expiryField.getText(),
                            credentialField.getText()
                    );
                } catch (Exception e) {
                    System.err.println("Error creating certification: " + e.getMessage());
                    showErrorAlert("Error", "An error occurred: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Certification> result = dialog.showAndWait();
        result.ifPresent(cert -> {
            currentCV.getCertifications().add(cert);
            currentCV.updateLastModified();
            saveCV();
            showCertificationsForm();
            showStatus("Certification added!");
        });
    }
    private void showLanguagesForm() {
        contentArea.getChildren().clear();

        // Title with Back Button
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Back Icon Button
        Button backIcon = new Button("<");
        backIcon.setPrefSize(40, 40);
        backIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                "-fx-background-radius: 50%; -fx-cursor: hand;");

        backIcon.setOnMouseEntered(e -> {
            backIcon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnMouseExited(e -> {
            backIcon.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-background-radius: 50%; -fx-cursor: hand;");
        });

        backIcon.setOnAction(e -> {
            saveCV();
            showCertificationsForm();
        });

        Label titleLabel = createSectionTitle("Languages");
        titleLabel.setPadding(new Insets(0));

        titleBox.getChildren().addAll(backIcon, titleLabel);

        Label subtitleLabel = new Label("List languages you can speak and your proficiency level");
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        TextField langField = new TextField();
        langField.setPromptText("e.g., English Native, Spanish Fluent");
        langField.setPrefWidth(400);
        langField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        // Real-time validation for Language field (only letters and spaces)
        langField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    langField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
                    langField.setPromptText("e.g., English Native, Spanish Fluent");
                } else if (!ValidationUtils.isValidName(newVal)) {
                    langField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #ffcccc; -fx-border-color: red; -fx-border-width: 2px; -fx-text-fill: red;");
                    langField.setPromptText("❌ Only letters allowed");
                } else {
                    langField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
                }
            } catch (Exception e) {
                System.err.println("Error validating language: " + e.getMessage());
            }
        });

        VBox langList = new VBox(10);
        langList.setPadding(new Insets(15));
        langList.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");

        // Show already added languages
        for (String lang : currentCV.getLanguages()) {
            HBox langItem = new HBox(15);
            langItem.setAlignment(Pos.CENTER_LEFT);
            Label langLabel = new Label("• " + lang);
            langLabel.setStyle("-fx-font-size: 14px;");

            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                    "-fx-font-size: 12px; -fx-padding: 3 8;");
            removeBtn.setOnAction(e -> {
                try {
                    currentCV.getLanguages().remove(lang);
                    currentCV.updateLastModified();
                    saveCV();
                    showLanguagesForm();
                } catch (Exception ex) {
                    System.err.println("Error removing language: " + ex.getMessage());
                }
            });

            langItem.getChildren().addAll(langLabel, removeBtn);
            langList.getChildren().add(langItem);
        }

        // Add language on Enter with validation
        langField.setOnAction(e -> {
            try {
                String lang = langField.getText().trim();

                // Validate: only letters and spaces
                if (!lang.isEmpty()) {
                    if (!ValidationUtils.isValidName(lang)) {
                        showErrorAlert("Invalid Input", "Language name can only contain letters and spaces.\nNumbers and special characters are not allowed.");
                        return;
                    }

                    if (!currentCV.getLanguages().contains(lang)) {
                        currentCV.getLanguages().add(lang);
                        langField.clear();
                        currentCV.updateLastModified();
                        saveCV();
                        showLanguagesForm();
                        showStatus("Language added!");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error adding language: " + ex.getMessage());
                showErrorAlert("Error", "An error occurred while adding language: " + ex.getMessage());
            }
        });

        // Save & Preview Button with validation
        Button saveBtn = createActionButton("💾 Save & Preview CV", "#27ae60");
        saveBtn.setOnAction(e -> {
            try {
                // Get all languages from the input field (comma separated)
                String input = langField.getText().trim();
                if (!input.isEmpty()) {
                    // Validate input first
                    if (!ValidationUtils.isValidName(input)) {
                        showErrorAlert("Invalid Input", "Language names can only contain letters and spaces.\nNumbers and special characters are not allowed.");
                        return;
                    }

                    String[] langs = input.split("[,\\n]");
                    for (String l : langs) {
                        String trimmed = l.trim();
                        if (!trimmed.isEmpty()) {
                            // Validate each language
                            if (!ValidationUtils.isValidName(trimmed)) {
                                showErrorAlert("Invalid Input", "Language '" + trimmed + "' contains invalid characters.\nOnly letters and spaces are allowed.");
                                return;
                            }
                            if (!currentCV.getLanguages().contains(trimmed)) {
                                currentCV.getLanguages().add(trimmed);
                            }
                        }
                    }
                }

                currentCV.updateLastModified();
                saveCV();

                // Debug
                System.out.println("Saved Languages: " + currentCV.getLanguages());

                // Show CV Preview
                showStatus("Languages saved! Opening preview...");
                sceneManager.showCVPreviewScene(currentCV);
            } catch (Exception ex) {
                System.err.println("Error saving languages: " + ex.getMessage());
                showErrorAlert("Error", "An error occurred while saving: " + ex.getMessage());
            }
        });

        contentArea.getChildren().addAll(titleBox, subtitleLabel, langField, langList, saveBtn);
    }
    // Replace the exportToPDF() method in both CVBuilderScene.java and CVPreviewScene.java

    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CV as PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        String fileName = currentCV.getPersonalInfo().getFullName().isEmpty() ?
                "MyCV" : currentCV.getPersonalInfo().getFullName().replaceAll(" ", "_");
        fileChooser.setInitialFileName(fileName + "_CV.pdf");

        File file = fileChooser.showSaveDialog(view.getScene().getWindow());
        if (file != null) {
            // Use template-based PDF generation
            if (TemplatePDFGenerator.generatePDF(currentCV, file.getAbsolutePath())) {
                showStatus("✓ CV exported successfully to: " + file.getName());
                showSuccessAlert("Export Successful",
                        "Your CV has been saved with " + currentCV.getTemplate().getName() + " template!\n\n" +
                                "Location: " + file.getAbsolutePath());
            } else {
                showErrorAlert("Export Failed",
                        "Failed to export CV. Please check console for errors.");
            }
        }
    }

    private void renameCVDialog() {
        TextInputDialog dialog = new TextInputDialog(currentCV.getCvName());
        dialog.setTitle("Rename CV");
        dialog.setHeaderText("Enter new name for CV");
        dialog.setContentText("CV Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                currentCV.setCvName(name.trim());
                currentCV.updateLastModified();
                saveCV();
                showStatus("CV renamed to: " + name);
                createView(); // Refresh view
            }
        });
    }

    public void openFileLocation(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file.getParentFile()); // opens the folder containing the file
            } else {
                System.out.println("File does not exist: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAboutDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About CV Maker");
        alert.setHeaderText("Professional CV Maker v1.0");
        alert.setContentText(
                "Create professional CVs with ease!\n\n" +
                        "Features:\n" +
                        "• Email authentication with OTP\n" +
                        "• Multiple CV sections\n" +
                        "• Professional PDF export\n" +
                        "• Save and manage multiple CVs\n\n" +
                        "Developed with JavaFX & iText"
        );
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ADD TO CVBuilderScene.java - Enhanced bulk export method
//    private void showBulkExport() {
//        if (!CompletePDFGenerator.validateCV(currentCV)) {
//            showErrorAlert("Incomplete CV",
//                    "Please fill in at least your name and email before bulk export.");
//            return;
//        }
//
//        BulkExportDialog dialog = new BulkExportDialog();
//        Optional<BulkExportSettings> result = dialog.showAndWait();
//
//        if (result.isPresent()) {
//            BulkExportSettings settings = result.get();
//
//            if (settings.getTemplates().isEmpty()) {
//                showErrorAlert("No Templates Selected",
//                        "Please select at least one template to export.");
//                return;
//            }
//
//            ExportProgressWindow progressWindow = new ExportProgressWindow();
//            progressWindow.show();
//
//            new Thread(() -> {
//                List<CVTemplate> templates = settings.getTemplates();
//                int total = templates.size();
//                int completed = 0;
//                int failed = 0;
//
//                for (int i = 0; i < templates.size(); i++) {
//                    if (progressWindow.isCancelled()) {
//                        break;
//                    }
//
//                    CVTemplate template = templates.get(i);
//
//                    double progress = (double) i / total;
//                    String status = "Exporting " + template.getName() + "...";
//                    String details = "(" + (i + 1) + " of " + total + ")";
//
//                    progressWindow.updateProgress(progress, status, details);
//
//                    String fileName = settings.getFilePrefix() + "_" +
//                            template.getName().replace(" ", "_") + ".pdf";
//                    String filePath = settings.getOutputFolder() + File.separator + fileName;
//
//                    boolean success = CompletePDFGenerator.generatePDF(currentCV, filePath, template);
//
//                    if (success) {
//                        completed++;
//                    } else {
//                        failed++;
//                    }
//
//                    // Small delay for UI update
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                final int finalCompleted = completed;
//                final int finalFailed = failed;
//
//                javafx.application.Platform.runLater(() -> {
//                    progressWindow.updateProgress(1.0, "Export Complete!",
//                            finalCompleted + " successful, " + finalFailed + " failed");
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {}
//
//                    progressWindow.close();
//
//                    // Show completion dialog
//                    Alert completionAlert = new Alert(Alert.AlertType.INFORMATION);
//                    completionAlert.setTitle("Bulk Export Complete");
//                    completionAlert.setHeaderText("Export finished!");
//                    completionAlert.setContentText(
//                            "Successfully exported: " + finalCompleted + "\n" +
//                                    "Failed: " + finalFailed + "\n" +
//                                    "Location: " + settings.getOutputFolder()
//                    );
//
//                    if (settings.isOpenFolderAfter()) {
//                        ButtonType openFolder = new ButtonType("Open Folder");
//                        ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
//                        completionAlert.getButtonTypes().setAll(openFolder, close);
//
//                        Optional<ButtonType> action = completionAlert.showAndWait();
//                        if (action.isPresent() && action.get() == openFolder) {
//                            openFileLocation(settings.getOutputFolder());
//                        }
//                    } else {
//                        completionAlert.showAndWait();
//                    }
//                });
//            }).start();
//        }
//    }
}
