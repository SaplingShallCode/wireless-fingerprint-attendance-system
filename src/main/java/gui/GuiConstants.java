package gui;

/**
 * The GuiConstants class contains all the constant variables to be used
 * by the classes contained in the current package
 */
public class GuiConstants {

    public enum WindowSizes {
        MIN_HEIGHT(600),
        MIN_WIDTH(800);

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

}
