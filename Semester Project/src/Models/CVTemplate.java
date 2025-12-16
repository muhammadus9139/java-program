package Models;

import java.io.Serializable;

public class CVTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String category;
    private String description;
    private String colorScheme;

    public CVTemplate(String id, String name, String category, String description, String colorScheme) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.colorScheme = colorScheme;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getColorScheme() { return colorScheme; }

    public static CVTemplate[] getAllTemplates() {
        return new CVTemplate[] {
                new CVTemplate("simple_classic", "Classic Blue", "SIMPLE",
                        "Traditional professional design", "#2c3e50"),
                new CVTemplate("simple_minimal", "Minimal Gray", "SIMPLE",
                        "Clean minimalist layout", "#5d6d7e"),
                new CVTemplate("simple_elegant", "Elegant Navy", "SIMPLE",
                        "Simple yet elegant", "#34495e"),
                new CVTemplate("modern_creative", "Creative Teal", "MODERN",
                        "Bold and eye-catching", "#16a085"),
                new CVTemplate("modern_vibrant", "Vibrant Orange", "MODERN",
                        "Dynamic and colorful", "#e67e22"),
                new CVTemplate("modern_tech", "Tech Purple", "MODERN",
                        "Modern tech-style", "#8e44ad"),
                new CVTemplate("pro_executive", "Executive Black", "PROFESSIONAL",
                        "Sophisticated design", "#2c2c2c"),
                new CVTemplate("pro_corporate", "Corporate Green", "PROFESSIONAL",
                        "Professional corporate", "#27ae60"),
                new CVTemplate("pro_formal", "Formal Burgundy", "PROFESSIONAL",
                        "Formal and polished", "#922b21")
        };
    }

    public static CVTemplate getTemplateById(String id) {
        for (CVTemplate template : getAllTemplates()) {
            if (template.getId().equals(id)) {
                return template;
            }
        }
        return getAllTemplates()[0];
    }
}