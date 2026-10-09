package com.spotanote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;

public class CreatePromotion {
        //database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";
    public String promotionName;
    public String promotionDescription;
    public String promotionSong;
    public int recordId;
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public CreatePromotion() {
        // Default constructor
    }

    public static int createPromotion(String promotionName, String promotionDescription, String promotionSong, int recordId) 
    {
        // Validate input parameters
        if (promotionName == null || promotionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name cannot be null or empty");
        }
        if (promotionDescription == null || promotionDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion description cannot be null or empty");
        }
        if (promotionSong == null || promotionSong.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion song cannot be null or empty");
        }
        if (recordId <= 0) {
            throw new IllegalArgumentException("Record ID cannot be null or empty");
        }

        // Insert the promotion into the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Promotion (promotion_name, promotion_description, promotion_song, record_Id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, promotionName);
                statement.setString(2, promotionDescription);
                statement.setString(3, promotionSong);
                statement.setInt(4, recordId);
                int rowsAffected = statement.executeUpdate();
                //return the promotionId of the newly created promotion
                //by adding 1 to the last promotionId in the database
                if (rowsAffected > 0) {
                    String getLastIdSql = "SELECT LAST_INSERT_ID()";
                    try (PreparedStatement getLastIdStatement = connection.prepareStatement(getLastIdSql);
                         ResultSet resultSet = getLastIdStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt(1); // Return the last inserted promotion ID
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 if there was an error during insertion
        }
        return -1; // Return -1 if the insertion failed for any reason
    }

    public static boolean promotionExists(int promotionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT COUNT(*) FROM Promotion WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // Return true if the promotion exists, false otherwise
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there was an error during the check
    }


    //first we have to search the database to make sure the promotion exists, if it does we can delete it, if not we will return an error message
    public static boolean removePromotion(int promotionId) 
    {
        // Validate input parameters
        if (promotionId <= 0) {
            throw new IllegalArgumentException("Promotion ID cannot be null or empty");
        }
        // Delete the promotion from the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM Promotion WHERE promotion_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0; // Return true if a row was deleted, false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }

    public static String getPromotionName(int promotionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT promotion_name FROM Promotion WHERE promotion_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("promotion_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the promotion does not exist or there was an error
    }

    public static String getPromotionDescription(int promotionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT promotion_description FROM Promotion WHERE promotion_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("promotion_description");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the promotion does not exist or there was an error
    }

    public static String getPromotionSong(int promotionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT promotion_song FROM Promotion WHERE promotion_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("promotion_song");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the promotion does not exist or there was an error
    }

    public static int getPromotionRecordId(int promotionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT record_Id FROM Promotion WHERE promotion_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("record_Id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the promotion does not exist or there was an error
    }

    public static int getPromotionIdByName(String promotionName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT promotion_id FROM Promotion WHERE promotion_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, promotionName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("promotion_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the promotion does not exist or there was an error
    }


    public static CreatePromotion[] getAllPromotions()
    {
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM Promotion";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                // Create an array to hold the promotions
                CreatePromotion[] promotions = new CreatePromotion[100]; // Adjust size as needed
                int index = 0;
                while (resultSet.next()) {
                    // Create a new CreatePromotion object for each promotion
                    CreatePromotion promotion = new CreatePromotion();
                    promotion.promotionName = resultSet.getString("promotion_name");
                    promotion.promotionDescription = resultSet.getString("promotion_description");
                    promotion.promotionSong = resultSet.getString("promotion_song");
                    promotion.recordId = resultSet.getInt("record_Id");
                    promotions[index++] = promotion;
                }
                return promotions;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}