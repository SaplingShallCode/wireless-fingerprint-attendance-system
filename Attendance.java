package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class AttendanceDAO {

    private Connection connection;
    private String url = "jdbc:postgresql://localhost:5432/attendance";
    private String username = "postgres";
    private String password = "password";

    public AttendanceDAO() {
        try {
            Class.forName("com.postgresql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database...");
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAttendance(Attendance attendance) {
        try {
            String query = "INSERT INTO attendance (user_id, date, time_in, time_out, event_name, event_location) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, attendance.getUserId());
            ps.setDate(2, attendance.getDate());
            ps.setTime(3, attendance.getTimeIn());
            ps.setTime(4, attendance.getTimeOut());
            ps.setString(6, attendance.getEventName());
            ps.setString(7, attendance.getEventLocation());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Attendance> getAllAttendances() {
        List<Attendance> attendances = new ArrayList<>();
        try {
            String query = "SELECT * FROM Attendance";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                Date date = rs.getDate("date");
                Time timeIn = rs.getTime("time_in");
                Time timeOut = rs.getTime("time_out");
                String eventName = rs.getString("event_name");
                String eventLocation = rs.getString("event_location");
                attendances.add(new Attendance (userId, date, timeIn, timeOut, eventName, eventLocation));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }
}

}
