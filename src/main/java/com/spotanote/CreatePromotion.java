package com.spotanote;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.protocol.Resultset;

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

    //here we are returning the promotionId of the newly created promotion
    //and takes in the name, description, song name as strings and also  takes in the recordId as an int

    /**
     * 
     * @param promotionName - the name in which a promotion has
     * @param promotionDescription - the description in which a promotion will have
     * @param promotionSong - the name of the song a promotion will have
     * @param recordId - the int id of the record this promotion is connected to
     * @return int - the promotionId of the created promotion or -1 if something went wrong
     * **/
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

    //here we check to make sure that the promotion exists in the database
    //and returns either true or false based on it
    /**
     * 
     * @param promotionId - the id we are using to check the database to see if the promotion exists
     * @return boolean - a true false statement checking to see if the function worked properly or not
     */
    public static boolean promotionExists(int promotionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT COUNT(*) FROM promotion WHERE promotion_id = ?";
            System.out.println("PromotionId: "+ promotionId);
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
    /**
     * 
     * @param promotionId - the id of the promotion we want to remove
     * @return boolean - true or false based if it removed the promotion or not from the database
     */
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

    //here we are returning the promotion name based on the promotion id
    /**
     * 
     * @param promotionId - the id of the promotion we are using to get the name of
     * @return string - returns the name of the promotion or null if an error occured
     */
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

    //here we are returning the promotion description based on the promotion id
    /**
     * 
     * @param promotionId - the id of the promotion we are using to get the description of
     * @return string - returns the description of the given promotion or null if an error occured
     */
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

    //here we return a string of what the promotion song name is based on the promotionId
    /**
     * 
     * @param promotionId - the id of the promotion we want to get the name of the song from
     * @return string - returns the name of the song or null if an error occured
     */
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

    //here we are getting the record id associated with the given promotion
    //takes in a promotionId and returns the id of the record
    /**
     * 
     * @param promotionId - the id of the promotion we want to get the recordId from
     * @return int - returns the int id of the record from the promotion or -1 if an error occured
     */
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

    //returns the id of the promotion name that was given and returns -1 if there was any errors or if it doesn't exist
    /**
     * 
     * @param promotionName - takes in a string of the promotion name to get the id back from (only works if names are unique in database)
     * @return int - returns the id of the promotion with the same name or -1 if the names do not match anything
     */
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


    //this takes all of the promotions that are stored in the database
    //this returns an array of all promotions and their attributes using the CreatePromotion datatype
    /**
     * @param - takes in no parameters
     * @return CreatePromotion array - returns an array of all promotions with their information
     */
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


    //here I am going to create the function that will like the current promotion
    //based on the promotionId and userId making sure that someone can only like a promotion once, if they try to like it again it will remove the like from the promotion
    //returns a boolean to say if they liked the promotion or not
    /**
     * 
     * @param promotionId - the id of the promotion we want to use
     * @param userId - the current user who is logged into the system
     * @return boolean - true or false based on if the function worked or not
     */
    public static boolean likePromotion(int promotionId, int userId) 
    {
        // Validate input parameters
        if (promotionId <= 0) {
            throw new IllegalArgumentException("Promotion ID cannot be null or empty");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // Check if the user has already liked the promotion
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM likePromotion WHERE promotion_id = ? AND user_id = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                checkStatement.setInt(1, promotionId);
                checkStatement.setInt(2, userId);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // User has already liked the promotion, so remove the like
                        System.out.println("User has already liked the promotion. Removing like.");
                        String deleteSql = "DELETE FROM likePromotion WHERE promotion_id = ? AND user_id = ?";
                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                            deleteStatement.setInt(1, promotionId);
                            deleteStatement.setInt(2, userId);
                            int rowsAffected = deleteStatement.executeUpdate();
                            return rowsAffected > 0; // Return true if a row was deleted
                        }
                    } else {
                        // User has not liked the promotion yet, so add the like
                        System.out.println("User has not liked the promotion yet. Adding like.");
                        String insertSql = "INSERT INTO likePromotion (promotion_id, user_id) VALUES (?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                            insertStatement.setInt(1, promotionId);
                            insertStatement.setInt(2, userId);
                            int rowsAffected = insertStatement.executeUpdate();
                            return rowsAffected > 0; // Return true if a row was inserted
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }

    //this is to check to see if the given post is already liked by the user that is signed in
    //returns a true or false based on it
    /**
     * 
     * @param promotionId - the promotion we want to check
     * @param userId - the user that is currently logged in
     * @return boolean - true false based on if the user liked or hasn't liked the given promotion
     */
    public static boolean isPromotionLikedByUser(int promotionId, int userId) {
        // Validate input parameters
        if (promotionId <= 0) {
            throw new IllegalArgumentException("Promotion ID cannot be null or empty");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // Check if the user has liked the promotion
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT COUNT(*) FROM likePromotion WHERE promotion_id = ? AND user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, promotionId);
                statement.setInt(2, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0; // Return true if the user has liked the promotion
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there was an error
    }

    //this function is used when we want to display the total likes amount a given post has
    //so that way we can display it on the screen
    //returns the number of likes a post has
    /**
     * 
     * @param promotionId - the id of the promotion that we want to get the amount of likes
     * @return int - returns the amount of likes the current promotion has or -1 if anything unexpected happened
     * @throws SQLException
     */
    public static int getLikeAmount(int promotionId) throws SQLException
    {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "SELECT COUNT(like_id) FROM likePromotion WHERE promotion_id = ?";
            try(PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, promotionId);
                try (ResultSet resultSet = statement.executeQuery()) 
                {
                    if(resultSet.next())
                        {
                            return resultSet.getInt(1);
                        }
                }
            }
            return 0;
        }
    }


    //created for the reason that we need a way to clear the likes from the promotion for a certain user. This can be
    //used where we want to make sure that the user has absolutly no likes for the given promotion
    //returns a true false if the deletion worked or not
    /**
     * 
     * @param userId - the id of the user who is currently logged in or the user we want to clear likes from
     * @param promotionId - the promotion that we want to clear the likes from
     * @return boolean - returns true false based on if the function worked correctly or not
     * @throws SQLException
     */
    public static boolean clearLikesForUser(int userId, int promotionId) throws SQLException
    {
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "DELETE FROM likePromotion WHERE user_id = ? AND promotion_id = ?";
            try(PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, userId);
                statement.setInt(2, promotionId);
                int deletedRows = statement.executeUpdate();

                return deletedRows > 0;
            }
        }
    }

    /**
     * 
     * @param userId - the id of the user who is currently logged in
     * @return CreatePromotion array - returns an array of all the promotions the specific user has ever liked
     * @throws SQLException
     */
    public static CreatePromotion[] getAllLikedPromotions(int userId) throws SQLException
    {
        CreatePromotion[] promotions = new CreatePromotion[100];
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String sql = "SELECT * FROM LikePromotion lp JOIN Promotion p ON lp.promotion_id = p.promotion_id WHERE lp.user_id = ?";
            try(PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setInt(1, userId);
                try(ResultSet resultSet = statement.executeQuery())
                {
                    int index = 0;
                    while (resultSet.next())
                    {
                        //FIX THIS I NEED TO FIRST GET ID FIRST THEN I NEED TO GET THE INFORMATION, SO I NEED TO DO A JOIN ON SOMETHING
                        CreatePromotion promotion = new CreatePromotion();
                        promotion.promotionName = resultSet.getString("promotion_name");
                        promotion.promotionDescription = resultSet.getString("promotion_description");
                        promotion.promotionSong = resultSet.getString("promotion_song");
                        promotion.recordId = resultSet.getInt("record_Id");
                        promotions[index++] = promotion;
                    }
                    return promotions;
                }
            }
        }
    }

}