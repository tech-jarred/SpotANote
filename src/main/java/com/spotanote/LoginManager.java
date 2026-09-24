package com.spotanote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManager {
    //database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";

    /**
     * @return a Connection object, will now be connected to database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * The purpose of this function is to authenticate a user by validating their entered credentials against information in database.
     * 
     * @param username - a string of the username provided on login page
     * @param password - a string of the password entered on login page
     * 
     * @return a User object with user's information, or null if either field is empty/username does not exist.
     */
    public User authenticate(String username, String password){
        // Make sure both fields have been provided. If either is empty, return null.
        if (username == null || password == null){
            return null;
        }

        // If both fields are provided, attempt authentication.
        String sqlQuery = "SELECT id, username, name, role, password FROM users WHERE username = ?"; // preparing the string this way "sanitizes" user input, preventing an SQL injection attack.
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlQuery)){ // this attempts to connect to database
            stmt.setString(1, sqlQuery);
            
            // Attempt executing the query once connected to database
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    String storedPassword = rs.getString("password");

                    // Check to see if entered password matches that in database.
                    if (storedPassword.equals(password)){
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String role = rs.getString("role");

                        // Create instance of User class with user's information
                        return new User(id, username, name, role);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
