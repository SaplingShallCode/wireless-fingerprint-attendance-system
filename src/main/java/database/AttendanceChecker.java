// Sample and Temporary

package database;

import java.util.Scanner;
import java.sql.*;

public class AttendanceChecker {

    public static void main(String[] args) {
        try {
            // Connect to database
            Connection conn = DriverManager.getConnection("jdbc:postgresql//localhost:5432/Attendance", "postgres", "secret");
            Statement stmt = conn.createStatement();

            // Get user input
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter your ID: ");
            int user_id = scan.nextInt();

            // Query database for user ID
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + user_id);

            // Check if ID is valid
            if (rs.next()) {
                System.out.println("Attendance Recorded!");
            }
            else {
                System.out.println("Invalid ID");
            }

            //Close connection
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
