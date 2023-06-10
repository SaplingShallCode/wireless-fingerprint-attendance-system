package core;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The DatabaseManager class will handle the database communication and
 * execution of any SQL script.
 */
public class DatabaseManager {
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;

    static {
        Dotenv dotenv = Dotenv.load();
        DB_USERNAME = dotenv.get("DB_USERNAME");
        DB_PASSWORD = dotenv.get("DB_PASSWORD");
    }


    /**
     * Open a connection to the database.
     * This method is to be used before executing SQL commands.
     * @return a connection object
     * @throws SQLException if a database access error or the url is null.
     */
    public Connection openConnection() throws SQLException {
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


    /**
     * Create the necessary tables. If the table already exists then the
     * SQL command will not execute as stated in the script "IF NOT EXISTS".
     */
    private void initTables() {
        try {
            Connection connection = openConnection();
            Statement stmt = connection.createStatement();

            String create_users_t = "CREATE TABLE IF NOT EXISTS users (" +
                    "user_id serial PRIMARY KEY, " +
                    "full_name text NOT NULL, " +
                    "fingerprint_id integer NOT NULL, " +
                    "UNIQUE (full_name, fingerprint_id)" +
                    ")";

            String create_user_info_t = "CREATE TABLE IF NOT EXISTS user_info (" +
                    "user_id integer REFERENCES users (user_id), " +
                    "first_name text, " +
                    "middle_name text, " +
                    "last_name text, " +
                    "age smallint, " +
                    "gender text, " +
                    "phone_number text, " +
                    "address text" +
                    ")";

            String create_attendance_t = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "attendance_id serial PRIMARY KEY, " +
                    "user_id integer REFERENCES users (user_id), " +
                    "date_attended date NOT NULL, " +
                    "time_attended time NOT NULL, " +
                    "event_name text, " +
                    "event_location text" +
                    ")";

            stmt.addBatch(create_users_t);
            stmt.addBatch(create_user_info_t);
            stmt.addBatch(create_attendance_t);

            int[] update_counts = stmt.executeBatch();
            // TODO: send to console.
            for (int i : update_counts) System.out.println(i);

            stmt.close();
            connection.close();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
