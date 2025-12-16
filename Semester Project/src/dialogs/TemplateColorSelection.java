package dialogs;

import Models.CVColor;
import Models.CVTemplateStyle;

public class TemplateColorSelection {
    private CVTemplateStyle templateStyle;
    private CVColor color;

    public TemplateColorSelection(CVTemplateStyle templateStyle, CVColor color) {
        this.templateStyle = templateStyle;
        this.color = color;
    }

    public CVTemplateStyle getTemplateStyle() { return templateStyle; }
    public CVColor getColor() { return color; }
}
