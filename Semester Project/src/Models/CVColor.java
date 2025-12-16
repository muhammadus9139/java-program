package Models;

import java.io.Serializable;

public enum CVColor implements Serializable {
    BLUE("Professional Blue", "#2980b9"),
    GREEN("Fresh Green", "#27ae60"),
    PURPLE("Creative Purple", "#8e44ad"),
    RED("Bold Red", "#e74c3c"),
    ORANGE("Vibrant Orange", "#e67e22"),
    TEAL("Modern Teal", "#16a085"),
    NAVY("Classic Navy", "#34495e"),
    BURGUNDY("Elegant Burgundy", "#922b21"),
    GRAY("Minimal Gray", "#5d6d7e"),
    BLACK("Executive Black", "#2c2c2c");

    private static final long serialVersionUID = 1L;
    private final String displayName;
    private final String hexCode;


    CVColor(String displayName, String hexCode) {
        this.displayName = displayName;
        this.hexCode = hexCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHexCode() {
        return hexCode;
    }
}