package utility;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

/**
 * The Const class contains all the constant variables to be used
 * by the classes contained in the current package
 */
public class Const {

    /**
     * Disable instantiation of this object.
     */
    private Const() {}


    // for the server - client heartbeat mechanism
    public static final long HEARTBEAT_INTERVAL = 5000;
    public static final long DISCON_THRESHOLD = 2 * HEARTBEAT_INTERVAL;

    public static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final String ICON_PATH = "img/icondefault.png";
    public static final String ICON64_PATH = "img/icon64x64.png";
    public static final String ICON32_PATH = "img/icon32x32.png";
    public static final String ICON16_PATH = "img/icon16x16.png";

    public static final Image ENROLL_ICON = new Image("img/enroll24x24.png");
    public static final Image DISCONNECT_ICON = new Image("img/disconnect24x24.png");

    public static final Font CONSOLAS = new Font("Consolas", 13);

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
                "Register a fingerprint on a selected fingerprint client. Id must be greater than 0.",
                "enroll <client-name>",
                "enroll"
        ),
        DISCONNECT(
                4,
                "Disconnect a client from the server.",
                "disconnect <client-name>",
                "disconnect"
        ),
        CLIENTS_INFO(
                5,
                "Display the remote address and port of all the clients.",
                "clients info",
                "clients info"
        ),
        REBOOT_CLIENT(
                6,
                "Send a reboot command to a client.",
                "reboot <client-name>",
                "reboot"
        ),
        INIT_DB(
                7,
                "Initialize database tables. note: the postgres database must exist first.",
                "init tables",
                "init tables"
        ),
        TOCSV_DATE(
                8,
                "Export data from the database according to a specified date.",
                "export date <yyyy-mm-dd>",
                "export"
        ),
        TOCSV_EVENT(
                8,
                "Export data from the database according to a specified event name.",
                "export event <event-name>",
                "export"
        ),
        TOCSV_ALLUSER(
                8,
                "Export all users from the database.",
                "export all_users",
                "export"
        ),
        TOCSV_ALLATTENDANCE(
                8,
                "Export all attendance data from the database.",
                "export all_attendance",
                "export"
        ),
        DISPLAY_CURRENT_EVENT_DATA(
                9,
                "Displays the current event that will be recorded to the database.",
                "event see",
                "event see"
        ),
        SET_EVENT_DATA(
                10,
                "Set the new event name and location of the event. " +
                        "multi-word event name and event loc must be" +
                        "separated by spaces",
                "event new <name> <location>",
                "event new"
        ),
        DELETE_RECORDS(
                11,
                "Delete all records from a specific user from the database. Must use user id",
                "delete db <fingerprint-id> <client-name>",
                "delete db"
        ),
        DELETE_CLIENT_DB(
                12,
                "[for-debug-only] Empty the database of a client.",
                "deletealldatafromdatabase <client-name>",
                "deletealldatafromdatabase"
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


    public enum Icons {
        ICON_DEFAULT(new Image(ICON_PATH)),
        ICON_64(new Image(ICON64_PATH)),
        ICON_32(new Image(ICON32_PATH)),
        ICON_16(new Image(ICON16_PATH));

        private final Image img;
        Icons (Image img) {
            this.img = img;
        }


        public Image getIconImage() {
            return img;
        }
    }

}
