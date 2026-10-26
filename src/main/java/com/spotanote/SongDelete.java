
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * SongDelete class
 * Focuses on deleting a song
 */
public class SongDelete {

    /*
     * Delete Song function
     * 
     *  @param song id which is the int id corrisponding to the song id in the database
     *  @ return no returns, only success/error statements will be sent
     */
    public static void deleteSong(int songID)
    {
        String checkSql = "SELECT id FROM Song WHERE id = ?";
        String deleteJunctionSql = "DELETE FROM PlaylistSong WHERE song_id = ?"; //added method for dependencies
        String deleteSql = "DELETE FROM Song WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection())
        {
            //check if song exists
            try (PreparedStatement checkStmt = connect.prepareStatement(checkSql))
            {
                checkStmt.setInt(1, songID);
                try (ResultSet rs = checkStmt.executeQuery())
                {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Song with ID " + songID + " does not exist.");
                    }
                }
            }

            //remove entries in junction table
            try (PreparedStatement junctionStmt = connect.prepareStatement(deleteJunctionSql)) {
                junctionStmt.setInt(1, songID);
                junctionStmt.executeUpdate();
            }

            //update database
            try (PreparedStatement stmt = connect.prepareStatement(deleteSql))
            {
                stmt.setInt(1, songID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting song: " + e.getMessage());
            e.printStackTrace();
        }
    }
}