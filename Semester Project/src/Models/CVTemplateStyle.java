package Models;

import java.io.Serializable;

public enum CVTemplateStyle implements Serializable {

    SIMPLE("Simple", "Clean and minimalist design", "#3498db"),
    PROFESSIONAL("Professional", "Corporate and formal layout", "#2c3e50"),
    MODERN("Modern", "Contemporary with sidebar design", "#9b59b6");
//    CREATIVE("Creative", "Bold and colorful design", "#e74c3c"),
//    ELEGANT("Elegant", "Sophisticated two-tone design", "#16a085");

    private static final long serialVersionUID = 1L;
    private final String name;
    private final String description;
    private final String defaultColor;

    CVTemplateStyle(String name, String description, String defaultColor) {
        this.name = name;
        this.description = description;
        this.defaultColor = defaultColor;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDefaultColor() { return defaultColor; }
}
