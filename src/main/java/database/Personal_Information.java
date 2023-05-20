package database;

import java.sql.*;

public class Personal_Information {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://localhost/personal_information";   //database specific url
        String user = "yourusername";
        String password = "yourpassword";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            try (Statement statement = connection.createStatement()) {
                String sql = "select * from info";
                try (ResultSet result = statement.executeQuery(sql)) {
                    while (result.next()) {
                        String user_id = result.getString("user_id");
                        int age = result.getInt("age");
                        String gender = result.getString("gender");
                        String phone_number = result.getString("phone_number");
                        String address = result.getString("address");
                        String last_name = result.getString("last_name");
                        String first_name = result.getString("first_name");
                        String middle_name = result.getString("middle_name");

                        System.out.println(user_id + " "
                                + age + " "
                                + gender + " "
                                + phone_number + " "
                                + address + " "
                                + last_name + " "
                                + first_name + " "
                                + middle_name);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
