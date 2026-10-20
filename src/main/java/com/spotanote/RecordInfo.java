package com.spotanote;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RecordInfo {
    // database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";

    public String recordName;
    public int recordManagerId;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ability to grab record name from the database based on the recordId
    public static String getRecordName(int recordId) {
        String recordName = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT record_name FROM Record WHERE id = ?";
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

    // grabbing the record ID
    public static int getRecordId(String recordName) {
        int recordId = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id FROM Record WHERE record_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, recordName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        recordId = resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordId;
    }

public static int getRecordManagerId(int id) throws SQLException {
    int managerId = -1;

    try (Connection connection =
             DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

        String sql = "SELECT record_manager_id FROM Record WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    managerId = resultSet.getInt("record_manager_id");
                }
            }
        }
    }

    return managerId;
}

    
    // ability for user to follow a record with the UserFollowers table
    public static void followRecord(int userId, int recordId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO UserFollowers (user_Id, record_Id) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, recordId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unfollowRecord(int userId, int recordId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM UserFollowers WHERE user_Id = ? AND record_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, recordId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRecord(String recordName, int recordManagerId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Record (record_name, record_manager_id) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, recordName);
                statement.setInt(2, recordManagerId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static RecordInfo[] getAllRecords() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM Record";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                RecordInfo[] recordArray = new RecordInfo[100];
                int index = 0;
                while (resultSet.next()) {
                    RecordInfo record = new RecordInfo();
                    record.recordName = resultSet.getString("record_name");
                    record.recordManagerId = resultSet.getInt("record_manager_id");
                    recordArray[index++] = record;
                }
                return recordArray;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isRecordFollowedByUser(int recordId, int userId) throws SQLException {
        if (recordId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Record ID or User ID cannot be null or empty");
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM UserFollowers WHERE record_Id = ? AND user_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, recordId);
                statement.setInt(2, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

/// comment