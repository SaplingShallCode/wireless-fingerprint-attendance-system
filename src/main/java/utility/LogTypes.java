package utility;

/**
 * The LogTypes enum is used as a parameter to the LogHelper's log method.
 * It is used to classify the logged text.
 */
public enum LogTypes {
    CLIENT("CLIENT"),
    CONSOLE("CONSOLE"),
    DEBUG("DEBUG"),
    ERROR("ERROR"),
    INFO("INFO"),
    SERVER("SERVER"),
    WARNING("WARNING");

    private final String type;

    LogTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}