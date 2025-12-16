package dialogs;

import javafx.scene.Node;
import Models.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;

import java.util.Optional;

public class TemplateColorSelectionDialog extends Dialog<TemplateColorSelection> {

    private CVTemplateStyle selectedTemplate;
    private CVColor selectedColor;
    private VBox previewBox;

    public TemplateColorSelectionDialog() {
        setTitle("Choose Your CV Style");
        setHeaderText("Select a template design and color scheme");

        DialogPane dialogPane = getDialogPane();
        dialogPane.setPrefSize(850, 650);

        // Main container
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Template Selection Section
        Label templateLabel = new Label("1. Choose Template Style");
        templateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        GridPane templateGrid = createTemplateGrid();

        // Color Selection Section
        Label colorLabel = new Label("2. Choose Color Scheme");
        colorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        FlowPane colorFlow = createColorFlow();

        // Live Preview Section
        Label previewLabel = new Label("3. Preview");
        previewLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        previewBox = new VBox(10);
        previewBox.setPadding(new Insets(20));
        previewBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-background-color: white;");
        previewBox.setPrefHeight(200);
        previewBox.setAlignment(Pos.CENTER);

        Label previewPlaceholder = new Label("Select a template and color to see preview");
        previewPlaceholder.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14px;");
        previewBox.getChildren().add(previewPlaceholder);

        // Add all sections
        mainContent.getChildren().addAll(
                templateLabel, templateGrid,
                new Separator(),
                colorLabel, colorFlow,
                new Separator(),
                previewLabel, previewBox
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);

        dialogPane.setContent(scrollPane);

        // Buttons
        ButtonType selectButton = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(selectButton, ButtonType.CANCEL);

        // Disable continue until both selected
        Node continueBtn = dialogPane.lookupButton(selectButton);
        continueBtn.setDisable(true);

        // Result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == selectButton && selectedTemplate != null && selectedColor != null) {
                return new TemplateColorSelection(selectedTemplate, selectedColor);
            }
            return null;
        });
    }

    private GridPane createTemplateGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        CVTemplateStyle[] styles = CVTemplateStyle.values();
        int col = 0;

        for (CVTemplateStyle style : styles) {
            VBox templateCard = createTemplateCard(style);
            grid.add(templateCard, col, 0);
            col++;
            if (col > 2) break; // Show only 3 main templates
        }

        return grid;
    }

    private VBox createTemplateCard(CVTemplateStyle style) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefSize(200, 180);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; " +
                "-fx-border-width: 2; -fx-cursor: hand; -fx-background-radius: 8;");

        // Template icon/preview
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(150, 100);
        iconBox.setStyle("-fx-background-color: " + style.getDefaultColor() + "; " +
                "-fx-background-radius: 5;");

        Label iconLabel = new Label(getTemplateIcon(style));
        iconLabel.setFont(Font.font(40));
        iconLabel.setTextFill(Color.WHITE);
        iconBox.getChildren().add(iconLabel);

        // Template name
        Label nameLabel = new Label(style.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Template description
        Label descLabel = new Label(style.getDescription());
        descLabel.setFont(Font.font("Arial", 11));
        descLabel.setStyle("-fx-text-fill: #7f8c8d;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(180);
        descLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconBox, nameLabel, descLabel);

        // Click to select
        card.setOnMouseClicked(e -> {
            selectedTemplate = style;
            highlightSelected(card);
            updatePreview();
            enableContinueButton();
        });

        // Hover effect
        card.setOnMouseEntered(e -> {
            if (selectedTemplate != style) {
                card.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: " +
                        style.getDefaultColor() + "; -fx-border-width: 2; -fx-cursor: hand; " +
                        "-fx-background-radius: 8;");
            }
        });

        card.setOnMouseExited(e -> {
            if (selectedTemplate != style) {
                card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; " +
                        "-fx-border-width: 2; -fx-cursor: hand; -fx-background-radius: 8;");
            }
        });

        return card;
    }

    private String getTemplateIcon(CVTemplateStyle style) {
        switch (style) {
            case SIMPLE: return "📄";
            case PROFESSIONAL: return "💼";
            case MODERN: return "✨";
//            case CREATIVE: return "🎨";
//            case ELEGANT: return "👔";
            default: return "📋";
        }
    }

    private FlowPane createColorFlow() {
        FlowPane flow = new FlowPane();
        flow.setHgap(10);
        flow.setVgap(10);
        flow.setAlignment(Pos.CENTER_LEFT);

        for (CVColor cvColor : CVColor.values()) {
            VBox colorBox = createColorBox(cvColor);
            flow.getChildren().add(colorBox);
        }

        return flow;
    }

    private VBox createColorBox(CVColor cvColor) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-cursor: hand;");

        // Color rectangle
        Rectangle colorRect = new Rectangle(80, 80);
        colorRect.setFill(Color.web(cvColor.getHexCode()));
        colorRect.setArcWidth(10);
        colorRect.setArcHeight(10);
        colorRect.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        // Color name
        Label nameLabel = new Label(cvColor.getDisplayName());
        nameLabel.setFont(Font.font("Arial", 11));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        box.getChildren().addAll(colorRect, nameLabel);

        // Click to select
        box.setOnMouseClicked(e -> {
            selectedColor = cvColor;
            highlightColorSelected(box, colorRect);
            updatePreview();
            enableContinueButton();
        });

        // Hover effect
        box.setOnMouseEntered(e -> {
            colorRect.setScaleX(1.1);
            colorRect.setScaleY(1.1);
        });

        box.setOnMouseExited(e -> {
            if (selectedColor != cvColor) {
                colorRect.setScaleX(1.0);
                colorRect.setScaleY(1.0);
            }
        });

        return box;
    }

    private void highlightSelected(VBox card) {
        // Remove highlight from all cards
        GridPane parent = (GridPane) card.getParent();
        parent.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                ((VBox) node).setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; " +
                        "-fx-border-width: 2; -fx-cursor: hand; -fx-background-radius: 8;");
            }
        });

        // Highlight selected
        card.setStyle("-fx-background-color: #e8f8f5; -fx-border-color: #27ae60; " +
                "-fx-border-width: 3; -fx-cursor: hand; -fx-background-radius: 8;");
    }

    private void highlightColorSelected(VBox box, Rectangle rect) {
        // Remove highlight from all colors
        FlowPane parent = (FlowPane) box.getParent();
        parent.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                VBox colorBox = (VBox) node;
                Rectangle r = (Rectangle) colorBox.getChildren().get(0);
                r.setScaleX(1.0);
                r.setScaleY(1.0);
                r.setStroke(null);
            }
        });

        // Highlight selected
        rect.setScaleX(1.15);
        rect.setScaleY(1.15);
        rect.setStroke(Color.web("#27ae60"));
        rect.setStrokeWidth(3);
    }

    private void updatePreview() {
        if (selectedTemplate == null || selectedColor == null) return;

        previewBox.getChildren().clear();

        // Create mini preview
        VBox preview = new VBox(5);
        preview.setAlignment(Pos.CENTER_LEFT);

        // Header bar with selected color
        HBox header = new HBox();
        header.setPrefHeight(40);
        header.setStyle("-fx-background-color: " + selectedColor.getHexCode() + ";");
        header.setAlignment(Pos.CENTER);

        Label namePreview = new Label("Your Name");
        namePreview.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        namePreview.setTextFill(Color.WHITE);
        header.getChildren().add(namePreview);

        // Content preview
        VBox content = new VBox(8);
        content.setPadding(new Insets(15));

        Label sectionLabel = new Label("● Experience");
        sectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        sectionLabel.setTextFill(Color.web(selectedColor.getHexCode()));

        Label detailLabel = new Label("Job Title at Company");
        detailLabel.setFont(Font.font("Arial", 11));

        content.getChildren().addAll(sectionLabel, detailLabel);

        preview.getChildren().addAll(header, content);

        // Info label
        Label infoLabel = new Label("Template: " + selectedTemplate.getName() +
                " | Color: " + selectedColor.getDisplayName());
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        infoLabel.setStyle("-fx-text-fill: #27ae60;");

        previewBox.getChildren().addAll(preview, infoLabel);
    }

    private void enableContinueButton() {
        if (selectedTemplate != null && selectedColor != null) {
            Node continueBtn = getDialogPane().lookupButton(
                    getDialogPane().getButtonTypes().get(0));
            continueBtn.setDisable(false);
        }
    }
}
