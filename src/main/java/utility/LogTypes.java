package utility;

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