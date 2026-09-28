
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * PlaylistVisibility class
 * Focuses on privating and publicizing playlists
 * There will be two methods in this class
 * Both methods will be tested together in one file
 */
public class PlaylistVisibility {

    /*
     * Private Playlist function
     * 
     * @param playlist's id which is the int id associated to a playlist
     * @return no returns, only success/error statements will be sent
     */
    public static void privatePlaylist(int playlistID)
    {
        setVisibility(playlistID, false);
    }

    /*
    * Public Playlist function
    * 
    * @param playlist's id which is the int id associated to a playlist
    * @return no returns, only success/error statements will be sent
    */
    public static void publicPlaylist(int playlistID)
    {
        setVisibility(playlistID, true);
    }

    /*
     * Set Visibility function - helper function for privatePlaylist() and publicPlaylist()
     * 
     *  @param int playlist id as its stored in the database
     *  @param boolean makePublic, true: make the playlist public, false: make the playlist private
     */
    private static void setVisibility(int playlistID, boolean makePublic) {
        String checkSql = "SELECT is_public FROM Playlist WHERE id = ?";
        String updateSql = "UPDATE Playlist SET is_public = ? WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection())
        {
            //check if the playlist is currently private or public
            try (PreparedStatement checkStmt = connect.prepareStatement(checkSql))
            {
                checkStmt.setInt(1, playlistID);
                try (ResultSet rs = checkStmt.executeQuery())
                {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Playlist with ID " + playlistID + " does not exist.");
                    }
                    
                    if (rs.getBoolean("is_public") == makePublic)
                    {
                        if (makePublic) {
                            throw new IllegalStateException("Playlist is already public.");
                        } else {
                            throw new IllegalStateException("Playlist is already private.");
                        }
                    }
                }
            }

            //update database
            try (PreparedStatement updateStmt = connect.prepareStatement(updateSql))
            {
                updateStmt.setBoolean(1, makePublic);
                updateStmt.setInt(2, playlistID);

                if (updateStmt.executeUpdate() > 0)
                {
                    if (makePublic) {
                        System.out.println("Playlist set to public successfully.");
                    } else {
                        System.out.println("Playlist set to private successfully.");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating playlist visibility: " + e.getMessage());
            e.printStackTrace();
        }
    }
}