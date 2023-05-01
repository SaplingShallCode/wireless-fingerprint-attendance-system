package server;

public enum Commands {
    HELP(
            "lists all commands.",
            "/help <command>"
            ),
    ENROLL(
            "enroll a finger on a selected fingerprint client.",
            "/enroll"
    );

    private final String description;
    private final String syntax;


    private Commands(String description, String syntax) {
        this.description = description;
        this.syntax = syntax;
    }


    public String getDescription() {
        return description;
    }
    public String getSyntax() {
        return syntax;
    }
}
