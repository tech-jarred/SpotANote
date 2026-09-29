
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * PlaylistDelete class
 * Focuses on deleting a playlist
 */
public class PlaylistDelete {

    /*
     * Delete Playlist  function
     * 
     *  @param playlist id which is the int id corrisponding to the playlist id in the database
     *  @ return no returns, only success/error statements will be sent
     */
    public static void deletePlaylist(int playlistID)
    {
        String checkSql = "SELECT id FROM Playlist WHERE id = ?";
        String deleteSql = "DELETE FROM Playlist WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection())
        {
            //check if playlist exists
            try (PreparedStatement checkStmt = connect.prepareStatement(checkSql))
            {
                checkStmt.setInt(1, playlistID);
                try (ResultSet rs = checkStmt.executeQuery())
                {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Playlist with ID " + playlistID + " does not exist.");
                    }
                }
            }

            //update database
            try (PreparedStatement stmt = connect.prepareStatement(deleteSql))
            {
                stmt.setInt(1, playlistID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }
}