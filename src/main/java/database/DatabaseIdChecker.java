package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseIdChecker {

    public static void main(String[] args) throws ClassNotFoundException {
        //TODO: USE Connection Object in DatabaseManager
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://localhost/personal_information";   //database specific url
        String user = "postgres";  //defaultuser
        String password = "secret"; //yourpassword

        String idToCheck = "2"; //sample id

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            //Check user_id from the attendance table if exist
            String query = "SELECT * FROM \"Attendance\" WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, idToCheck);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("ID found in the database.");
                //TODO: Create Attendance if matched
            } else {
                System.out.println("ID not found in the database.");
            }
}
        catch (SQLException e) {
            e.printStackTrace();
        }
    }}