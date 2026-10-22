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
    /**
     * 
     * @param recordId - the id of the record that we want to get the name from
     * @return string - retuns the name of the record we selected or null if something goes wrong
     */
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
    /**
     * 
     * @param recordName - the name of the record we want to get the id from (the names have to be unique)
     * @return int - returns the id of the record with the same name  or -1 if it doesnt exist
     */
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

    /**
     * 
     * @param id - the int id of the record we want to get the manager id from
     * @return - returns the id of the connected manager id or -1 if something goes wrong
     * @throws SQLException
     */
    public static int getRecordManagerId(int id) throws SQLException {
        int managerId = -1;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

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
    /**
     * 
     * @param userId - the int id of the current user who is currently logged in
     * @param recordId - the int id of the current record that is currnetly selected
     * @return returns nothing
     */
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

    /**
     * 
     * @param userId - the int id of the current user who is currently logged in
     * @param recordId - the int id of the current record that is currnetly selected
     * @return returns nothing
     */
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

    /**
     * 
     * @param recordName - the recordName that we want to add
     * @param recordManagerId - the associated manager id we want to connect to
     * @return returns nothing
     */
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

    /**
     * 
     * @param - needs no parameters
     * @return - returns nothing
     * @throws SQLException
     */
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

    /**
     * 
     * @param recordId - the id of the record that we want to check
     * @param userId - the id of the user who is currently logged in
     * @return - returns nothing
     * @throws SQLException
     */
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

    //getting all records followed by the given userId
    /**
     * 
     * @param userId - the id of the user who is currently logged in
     * @return RecordInfo array - returns an array of all records that the current user has followed
     * @throws SQLException
     */
    public static RecordInfo[] allFollowedRecords(int userId) throws SQLException
    {
        RecordInfo[] recordArray = new RecordInfo[100];
        if(userId <= 0)
        {
            return null;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "SELECT * FROM UserFollowers uf JOIN Record r ON uf.record_Id = r.id WHERE uf.user_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery())
                {
                    int index = 0;
                    while (resultSet.next())
                    {
                        RecordInfo record = new RecordInfo();
                        record.recordName = resultSet.getString("record_name");
                        record.recordManagerId = resultSet.getInt("record_manager_id");
                        recordArray[index++] = record;
                    }
                    return recordArray;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param recordId - the id of the record we want to remove all followers from
     * @return boolean - returns true or false based on if we removed them or not
     * @throws SQLException
     */
    public static boolean removeRecordFollowers(int recordId) throws SQLException
    {
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "DELETE FROM UserFollowers WHERE record_Id = ?";
            try(PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, recordId);
                int deletedRows = statement.executeUpdate();
                return deletedRows > 0;
            }
        }
    }
    /**
     * 
     * @param recordId - the id of the record that we want to remove from the database
     * @return boolean - returns true or false based if we successfully removed it or not
     * @throws SQLException
     */
    public static boolean removeRecord(int recordId) throws SQLException
    {
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "DELETE FROM record WHERE id = ?";
            try(PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, recordId);
                int deletedRows = statement.executeUpdate();
                return deletedRows > 0;
            }
        }
    }

    public static int getFollowedAmount(int recordId) throws SQLException
    {
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "SELECT COUNT(*) FROM userFollowers WHERE record_Id = ?";
            try(PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, recordId);
                try (ResultSet resultSet = statement.executeQuery())
                {
                    if(resultSet.next())
                    {
                        int count = resultSet.getInt(1);
                        return count;
                    }
                }
            }
            
        }
        return -1;
    }
}

