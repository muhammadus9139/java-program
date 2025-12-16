package UI;


//for printer working these two
import javafx.print.*;
import javafx.scene.transform.Scale;

import Models.*;
import Services.AuthenticationManager;
import Services.TemplatePDFGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;

public class CVPreviewScene {
    private BorderPane view;
    private SceneManager sceneManager;
    private AuthenticationManager authManager;
    private CV currentCV;
    private VBox contentArea;

    // Template color from CV
    private String templateColor;

    public CVPreviewScene(SceneManager sceneManager, AuthenticationManager authManager, CV cv) {
        this.sceneManager = sceneManager;
        this.authManager = authManager;
        this.currentCV = cv;

        // Get template color
        this.templateColor = cv.getTemplate().getColorScheme();

        createView();
    }

    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #ecf0f1;");

        // Top Bar with template color
        HBox topBar = createTopBar();
        view.setTop(topBar);

        // Center - CV Preview
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(40));
        contentArea.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        contentArea.setMaxWidth(800);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #ecf0f1;");

        HBox centerWrapper = new HBox(scrollPane);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(30));
        centerWrapper.setStyle("-fx-background-color: #ecf0f1;");

        view.setCenter(centerWrapper);

        buildCVPreview();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + templateColor + ";");

        Label titleLabel = new Label("📄 " + currentCV.getCvName());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        // Show template info
        Label templateInfo = new Label("Template: " + currentCV.getTemplate().getName());
        templateInfo.setFont(Font.font("Arial", 12));
        templateInfo.setTextFill(Color.web("#ecf0f1"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = createTopBarButton("✏️ Edit", "#3498db");
        editBtn.setOnAction(e -> sceneManager.showCVBuilderScene(currentCV));

        Button exportBtn = createTopBarButton("📥 Export PDF", "#27ae60");
        exportBtn.setOnAction(e -> exportToPDF());

        // ✅ NEW: Print Button
        Button printBtn = createTopBarButton("🖨️ Print", "#9b59b6");
        printBtn.setOnAction(e -> printCV());

        Button backBtn = createTopBarButton("← Back", "#95a5a6");
        backBtn.setOnAction(e -> sceneManager.showDashboardScene());

        topBar.getChildren().addAll(titleLabel, templateInfo, spacer, editBtn, exportBtn, printBtn, backBtn);
        return topBar;
    }

    private void printCV() {
        try {
            // Create printer job
            PrinterJob printerJob = PrinterJob.createPrinterJob();

            if (printerJob == null) {
                showErrorAlert("Print Error", "No printer available on your system.");
                return;
            }

            // Show print dialog with settings
            boolean proceed = printerJob.showPrintDialog(view.getScene().getWindow());

            if (proceed) {
                // Show page setup dialog (optional - user can configure pages, orientation, etc.)
                boolean pageSetup = printerJob.showPageSetupDialog(view.getScene().getWindow());

                // Get printer settings
                Printer printer = printerJob.getPrinter();
                PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();

                // Print progress alert
                Alert printingAlert = new Alert(Alert.AlertType.INFORMATION);
                printingAlert.setTitle("Printing");
                printingAlert.setHeaderText("Printing your CV...");
                printingAlert.setContentText("Please wait while we prepare the document.");
                printingAlert.show();

                // Scale content to fit page
                double scaleX = pageLayout.getPrintableWidth() / contentArea.getBoundsInParent().getWidth();
                double scaleY = pageLayout.getPrintableHeight() / contentArea.getBoundsInParent().getHeight();
                double scale = Math.min(scaleX, scaleY);

                Scale scaleTransform = new Scale(scale, scale);
                contentArea.getTransforms().add(scaleTransform);

                // Print the content
                boolean success = printerJob.printPage(contentArea);

                // Remove scale transform
                contentArea.getTransforms().remove(scaleTransform);

                if (success) {
                    // End the print job
                    printerJob.endJob();
                    printingAlert.close();

                    // Show success message
                    showSuccessAlert("Print Successful",
                            "Your CV has been sent to the printer!\n\n" +
                                    "Printer: " + printer.getName() + "\n" +
                                    "Pages: " + printerJob.getJobSettings().getPageRanges());
                } else {
                    printingAlert.close();
                    showErrorAlert("Print Failed", "Failed to print the document. Please try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("Print error: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Print Error", "An error occurred while printing: " + e.getMessage());
        }
    }


    private Button createTopBarButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void buildCVPreview() {
        contentArea.getChildren().clear();

        // Personal Info Header with template color
        addPersonalInfoSection();

        if (!currentCV.getSummary().isEmpty()) {
            addSummarySection();
        }

        if (!currentCV.getExperienceList().isEmpty()) {
            addExperienceSection();
        }

        if (!currentCV.getEducationList().isEmpty()) {
            addEducationSection();
        }

        if (!currentCV.getProjectList().isEmpty()) {
            addProjectsSection();
        }

        if (!currentCV.getSkills().isEmpty()) {
            addSkillsSection();
        }

        if (!currentCV.getCertifications().isEmpty()) {
            addCertificationsSection();
        }

        if (!currentCV.getLanguages().isEmpty()) {
            addLanguagesSection();
        }
    }

    private void addPersonalInfoSection() {
        PersonalInfo info = currentCV.getPersonalInfo();

        // Header with template color
        VBox headerBox = new VBox(10);
        headerBox.setPadding(new Insets(20));
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-background-color: " + templateColor + ";");

        Label nameLabel = new Label(info.getFullName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        nameLabel.setTextFill(Color.WHITE);

        VBox contactBox = new VBox(5);
        contactBox.setAlignment(Pos.CENTER);

        if (!info.getEmail().isEmpty()) {
            Label emailLabel = new Label("✉️ " + info.getEmail());
            emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
            contactBox.getChildren().add(emailLabel);
        }

        if (!info.getPhone().isEmpty()) {
            Label phoneLabel = new Label("📞 " + info.getPhone());
            phoneLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
            contactBox.getChildren().add(phoneLabel);
        }

        String location = "";
        if (!info.getCity().isEmpty()) location = info.getCity();
        if (!info.getCountry().isEmpty()) {
            location += (location.isEmpty() ? "" : ", ") + info.getCountry();
        }
        if (!location.isEmpty()) {
            Label locationLabel = new Label("📍 " + location);
            locationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
            contactBox.getChildren().add(locationLabel);
        }

        headerBox.getChildren().addAll(nameLabel, contactBox);
        contentArea.getChildren().add(headerBox);

        contentArea.getChildren().add(new Separator());
    }

    private void addSummarySection() {
        Label sectionTitle = createSectionTitle("Professional Summary");
        Label summaryText = new Label(currentCV.getSummary());
        summaryText.setWrapText(true);
        summaryText.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50; -fx-line-spacing: 3px;");
        summaryText.setPadding(new Insets(5, 0, 0, 0));
        contentArea.getChildren().addAll(sectionTitle, summaryText);
    }

    private void addExperienceSection() {
        Label sectionTitle = createSectionTitle("Work Experience");
        contentArea.getChildren().add(sectionTitle);

        for (Experience exp : currentCV.getExperienceList()) {
            VBox expBox = new VBox(5);
            expBox.setPadding(new Insets(10, 0, 15, 0));

            Label jobLabel = new Label(exp.getJobTitle());
            jobLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            jobLabel.setTextFill(Color.web(templateColor));

            Label companyLabel = new Label(exp.getCompany());
            companyLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
            companyLabel.setTextFill(Color.web("#34495e"));

            Label dateLocationLabel = new Label(exp.getStartDate() + " - " + exp.getEndDate() + " | " + exp.getLocation());
            dateLocationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            expBox.getChildren().addAll(jobLabel, companyLabel, dateLocationLabel);

            if (!exp.getResponsibilities().isEmpty()) {
                VBox respBox = new VBox(3);
                respBox.setPadding(new Insets(5, 0, 0, 15));
                for (String resp : exp.getResponsibilities()) {
                    Label respLabel = new Label("• " + resp);
                    respLabel.setWrapText(true);
                    respLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
                    respBox.getChildren().add(respLabel);
                }
                expBox.getChildren().add(respBox);
            }

            contentArea.getChildren().add(expBox);
        }
    }

    private void addEducationSection() {
        Label sectionTitle = createSectionTitle("Education");
        contentArea.getChildren().add(sectionTitle);

        for (Education edu : currentCV.getEducationList()) {
            VBox eduBox = new VBox(5);
            eduBox.setPadding(new Insets(10, 0, 15, 0));

            String degreeText = edu.getDegree();
            if (!edu.getFieldOfStudy().isEmpty()) {
                degreeText += " in " + edu.getFieldOfStudy();
            }
            Label degreeLabel = new Label(degreeText);
            degreeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            degreeLabel.setTextFill(Color.web(templateColor));

            Label institutionLabel = new Label(edu.getInstitution());
            institutionLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
            institutionLabel.setTextFill(Color.web("#34495e"));

            String details = edu.getStartDate() + " - " + edu.getEndDate();
            if (!edu.getLocation().isEmpty()) {
                details += " | " + edu.getLocation();
            }
            if (!edu.getGpa().isEmpty()) {
                details += " | GPA: " + edu.getGpa();
            }
            Label detailsLabel = new Label(details);
            detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            eduBox.getChildren().addAll(degreeLabel, institutionLabel, detailsLabel);

            if (!edu.getDescription().isEmpty()) {
                Label descLabel = new Label(edu.getDescription());
                descLabel.setWrapText(true);
                descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
                descLabel.setPadding(new Insets(5, 0, 0, 0));
                eduBox.getChildren().add(descLabel);
            }

            contentArea.getChildren().add(eduBox);
        }
    }

    private void addProjectsSection() {
        Label sectionTitle = createSectionTitle("Projects");
        contentArea.getChildren().add(sectionTitle);

        for (Project proj : currentCV.getProjectList()) {
            VBox projBox = new VBox(5);
            projBox.setPadding(new Insets(10, 0, 15, 0));

            Label nameLabel = new Label(proj.getProjectName());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            nameLabel.setTextFill(Color.web(templateColor));

            projBox.getChildren().add(nameLabel);

            HBox metaBox = new HBox(15);
            if (!proj.getProjectLink().isEmpty()) {
                Label linkLabel = new Label("🔗 " + proj.getProjectLink());
                linkLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");
                metaBox.getChildren().add(linkLabel);
            }
            if (!proj.getDuration().isEmpty()) {
                Label durationLabel = new Label("📅 " + proj.getDuration());
                durationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                metaBox.getChildren().add(durationLabel);
            }
            if (!metaBox.getChildren().isEmpty()) {
                projBox.getChildren().add(metaBox);
            }

            if (!proj.getTechnologies().isEmpty()) {
                Label techLabel = new Label("Technologies: " + String.join(", ", proj.getTechnologies()));
                techLabel.setWrapText(true);
                techLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + templateColor + "; -fx-font-weight: bold;");
                projBox.getChildren().add(techLabel);
            }

            Label descLabel = new Label(proj.getDescription());
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
            descLabel.setPadding(new Insets(5, 0, 0, 0));
            projBox.getChildren().add(descLabel);

            contentArea.getChildren().add(projBox);
        }
    }

    private void addSkillsSection() {
        Label sectionTitle = createSectionTitle("Skills");
        contentArea.getChildren().add(sectionTitle);

        FlowPane skillsFlow = new FlowPane();
        skillsFlow.setHgap(10);
        skillsFlow.setVgap(10);
        skillsFlow.setPadding(new Insets(5, 0, 0, 0));

        for (String skill : currentCV.getSkills()) {
            Label skillLabel = new Label(skill);
            skillLabel.setPadding(new Insets(8, 15, 8, 15));
            skillLabel.setStyle("-fx-background-color: " + templateColor + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 15; -fx-font-size: 12px;");
            skillsFlow.getChildren().add(skillLabel);
        }

        contentArea.getChildren().add(skillsFlow);
    }

    private void addCertificationsSection() {
        Label sectionTitle = createSectionTitle("Certifications");
        contentArea.getChildren().add(sectionTitle);

        for (Certification cert : currentCV.getCertifications()) {
            VBox certBox = new VBox(5);
            certBox.setPadding(new Insets(10, 0, 15, 0));

            Label nameLabel = new Label(cert.getCertificationName());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            nameLabel.setTextFill(Color.web(templateColor));

            Label orgLabel = new Label(cert.getIssuingOrganization());
            orgLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
            orgLabel.setTextFill(Color.web("#34495e"));

            String details = "Issued: " + cert.getIssueDate();
            if (!cert.getExpiryDate().isEmpty()) {
                details += " | Expires: " + cert.getExpiryDate();
            }
            if (!cert.getCredentialId().isEmpty()) {
                details += " | ID: " + cert.getCredentialId();
            }
            Label detailsLabel = new Label(details);
            detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            certBox.getChildren().addAll(nameLabel, orgLabel, detailsLabel);
            contentArea.getChildren().add(certBox);
        }
    }

    private void addLanguagesSection() {
        Label sectionTitle = createSectionTitle("Languages");
        contentArea.getChildren().add(sectionTitle);

        FlowPane langFlow = new FlowPane();
        langFlow.setHgap(10);
        langFlow.setVgap(10);
        langFlow.setPadding(new Insets(5, 0, 0, 0));

        for (String lang : currentCV.getLanguages()) {
            Label langLabel = new Label(lang);
            langLabel.setPadding(new Insets(8, 15, 8, 15));
            langLabel.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: " + templateColor + "; " +
                    "-fx-border-color: " + templateColor + "; -fx-border-width: 2; " +
                    "-fx-background-radius: 15; -fx-border-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;");
            langFlow.getChildren().add(langLabel);
        }

        contentArea.getChildren().add(langFlow);
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.web(templateColor));
        label.setPadding(new Insets(15, 0, 10, 0));

        VBox container = new VBox(label);
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + templateColor + "; -fx-pref-height: 2px;");
        container.getChildren().add(separator);

        contentArea.getChildren().add(container);

        return label;
    }

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
            // Use TemplatePDFGenerator with template colors
            if (TemplatePDFGenerator.generatePDF(currentCV, file.getAbsolutePath())) {
                showSuccessAlert("Export Successful",
                        "Your CV has been saved with " + currentCV.getTemplate().getName() + " template!\n\n" +
                                "Location: " + file.getAbsolutePath());
            } else {
                showErrorAlert("Export Failed",
                        "Failed to export CV. Please check console for errors.");
            }
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }
}