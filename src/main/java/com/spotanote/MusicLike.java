package com.spotanote;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;


public class MusicLike {
    public MusicLike(){}

    /**
     * likeSong -- Handles liking a song for a user by creating new entry to UsersLikedSongs table in db.
     * 
     * @param userID an int, the id number of the user liking a song
     * @param songID an int, the id number of the song being liked by the user
     * @throws IllegalArgumentException if the user OR song with provided id can not be found in the database
     * @throws RuntimeException if the database connection fails or isn't established
     */
    public void likeSong(int userID, int songID){
        String insertSQL = "INSERT INTO UsersLikedSongs (user_id, song_id) VALUES (?, ?)";

            try (Connection conn = DBExplorer.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertSQL)){ // Prepare the statement and connection.
                stmt.setInt(1, userID);
                stmt.setInt(2, songID);
                
                // Attempt to perform the insert
                stmt.executeUpdate();
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();

                // MySQL Error 1452: Foreign Key Constraint Violation (User or Song ID does not exist)
                if (errorCode == 1452) {
                    throw new IllegalArgumentException(
                        "Unable to like song: User ID " + userID + " or Song ID " + songID + " does not exist."
                    );
                }

                // MySQL Error 1062: Duplicate Primary Key Entry (User already liked this song)
                if (errorCode == 1062) {
                    return; // Idempotent: ignore duplicate likes quietly
                }

                throw new RuntimeException("Database error while user ID " + userID + " liked song ID " + songID, e);
            }
    }

    /**
     * dislikeSong
     * 
     * @param userID an int, the id number of the user disliking a song
     * @param songID an int, the id number of the song being disliked by the user
     * @throws IllegalArgumentException if the user+song id combination can not be found in the database
     * @throws RuntimeException if the database connection fails or isn't established
     */
    public void dislikeSong(int userID, int songID){
        String deleteSQL = "DELETE FROM UsersLikedSongs WHERE user_id = ? AND song_id = ?";

            try (Connection conn = DBExplorer.getConnection();
                PreparedStatement stmt = conn.prepareStatement(deleteSQL)) { // Prepare statement and connection.
                stmt.setInt(1, userID);
                stmt.setInt(2, songID);

                int rowsAffected = stmt.executeUpdate();

                // If no rows were deleted, the user had not previously liked this song
                if (rowsAffected == 0) {
                    throw new IllegalArgumentException(
                        "Liked song record for user ID " + userID + " and song ID " + songID + " could not be found."
                    );
                }

            } catch (SQLException e) {
                throw new RuntimeException("Database error while user ID " + userID + " disliked song ID " + songID, e);
            }
    }

    /**
     * getUsersLikedSongs
     * 
     * @param userID an int, the id of the user who's liked songs we need to get.
     * @throws RuntimeException if the database connection fails or isn't established
     * a {@link List} of {@link SongSummary} objects (empty list if the user has no liked songs)
     */
    public List<SongSummary> getUsersLikedSongs(int userID) {
        List<SongSummary> likedSongs = new ArrayList<>();

        // Build query.
        String query = "SELECT s.id, s.song_name " +
                    "FROM UsersLikedSongs uls " +
                    "JOIN Song s ON uls.song_id = s.id " +
                    "WHERE uls.user_id = ? " +
                    "ORDER BY uls.dateLiked DESC";

        try (Connection conn = DBExplorer.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int songId = rs.getInt("id");
                    String songName = rs.getString("song_name");

                    likedSongs.add(new SongSummary(songId, songName));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching liked songs for user ID: " + userID, e);
        }

        return likedSongs;
    }
}
