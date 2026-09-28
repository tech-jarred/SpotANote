
//imports
package com.spotanote;
/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * PrivatePlaylist class
 * Focuses on privating playlists
 */

 public class PrivatePlaylist {
    
    //database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";

    /*
     * Private Playlist function
     * 
     * @param playlist's id which is the int id associated to a playlist, this will eventually have its own helper method
     * @return no returns, only success/error statements will be sent
     */

     public static void privatePlaylist(int playlistID)
     {
        //check to see if the playlist is already private
        String checkPlaylistAlreadyPrivate = "SELECT playlist_is_public FROM Playlist WHERE id = ?";

        //sql insert statement
        String sql = "UPDATE Playlist SET playlist_is_public = false WHERE id = ?";

        //try-catch block to connect to the database and update playlist_is_public to false
        try (Connection connect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        
            //check to see if playlist is already private
            try (PreparedStatement checkStmt = connect.prepareStatement(checkPlaylistAlreadyPrivate)) {
                checkStmt.setInt(1, playlistID);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        boolean isPublic = rs.getBoolean("playlist_is_public");
                        if (!isPublic) {
                            throw new IllegalStateException("Playlist is already private.");
                        } else {
                            throw new IllegalArgumentException("Playlist with ID " + playlistID + " does not exist.");
                        }
                    }
                }
            }
    
            //updating the database
            try (PreparedStatement updatestmt = connect.prepareStatement(sql))
            {
                updatestmt.setInt(1, playlistID);
                int rowsAffected = updatestmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Playlist set to private successfully");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error privating playlist: " + e.getMessage());
            e.printStackTrace();
        }
     }
 }

 /*
 FOR REFERENCE ONLY

 /*
    //
 */