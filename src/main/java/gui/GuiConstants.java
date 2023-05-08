package gui;

/**
 * The GuiConstants class contains all the constant variables to be used
 * by the classes contained in the current package
 */
public class GuiConstants {

    /**
     * Disable instantiation of this object.
     */
    private GuiConstants() {}

    public enum WindowSizes {
        MIN_HEIGHT(600),
        MIN_WIDTH(1000),
        BORDERPANE_MARGIN(10);

        private final int value;
        WindowSizes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum StringValues {
        WINDOW_TITLE("Wireless Fingerprint-based Attendance Logger Server by NameGroup"),
        LOGIN_WINDOW_TITLE("Bind server host and port to..."),
        STYLESHEET_PATH("css/styles.css");


        private final String value;
        StringValues(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum LoginWindowSizes {
        PRIMARY_WIDTH(300),
        PRIMARY_HEIGHT(150),
        TEXTFIELD_WIDTH(200),
        LABEL_WIDTH(75);

        private final int value;
        LoginWindowSizes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Commands {
        START(
                1,
                "Starts the server.",
                "start server",
                "start server"
        ),
        STOP(
                2,
                "Stops the server.",
                "stop server",
                "stop server"
        ),
        ENROLL(
                3,
                "Register a fingerprint on a selected fingerprint client.",
                "enroll <client-name>",
                "enroll"
        );

        private final int id;
        private final String description;
        private final String syntax;
        private final String reference;


        Commands(int id, String description, String syntax, String reference) {
            this.id = id;
            this.description = description;
            this.syntax = syntax;
            this.reference = reference;
        }


        public int getId() {
            return id;
        }
        public String getDescription() {
            return description;
        }
        public String getSyntax() {
            return syntax;
        }
        public String getReference() {
            return reference;
        }
    }

}
