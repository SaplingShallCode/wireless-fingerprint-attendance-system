package server;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The DatabaseManager class will handle the database communication and
 * execution of any SQL script.
 */
public class DatabaseHandler {
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;
    private Statement statement = null;

    static {
        Dotenv dotenv = Dotenv.load();
        DB_USERNAME = dotenv.get("DB_USERNAME");
        DB_PASSWORD = dotenv.get("DB_PASSWORD");
    }


    /**
     * Open a connection to the database.
     * This method is to be used before executing SQL commands.
     *
     * @return a connection object
     * @throws SQLException if a database access error or the url is null.
     */
    private Connection openConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost/attendance_logger",
                    DB_USERNAME,
                    DB_PASSWORD
            );
        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return connection;
    }
}
