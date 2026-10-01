package com.spotanote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RecordInfo {
            //database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    //ability to grab record name from the database based on the recordId
    public static String getRecordName(int recordId) {
        String recordName = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT record_name FROM Record WHERE record_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, recordId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        recordName = resultSet.getString("record_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordName;
    }


}