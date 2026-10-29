
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SongVisibility class
 * Focuses on privating and publicizing songs
 * There will be two methods in this class
 * Both methods will be tested together in one file
 */
public class SongVisibility {

    /**
     * Private Song function
     * 
     * @param song's id which is the int id associated to a song
     * @return no returns, only success/error statements will be sent
     */
    public static void privateSong(int SongID)
    {
        setVisibility(SongID, false);
    }

    /**
    * Public Song function
    * 
    * @param song's id which is the int id associated to a song
    * @return no returns, only success/error statements will be sent
    */
    public static void publicSong(int songID)
    {
        setVisibility(songID, true);
    }

    /**
     * Set Visibility function - helper function for privateSong() and publicSong()
     * 
     *  @param int song id as its stored in the database
     *  @param boolean makePublic, true: make the song public, false: make the song private
     */
    private static void setVisibility(int songID, boolean makePublic) {
        String checkSql = "SELECT is_public FROM Song WHERE id = ?";
        String updateSql = "UPDATE Song SET is_public = ? WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection())
        {
            //check if the song is currently private or public
            try (PreparedStatement checkStmt = connect.prepareStatement(checkSql))
            {
                checkStmt.setInt(1, songID);
                try (ResultSet rs = checkStmt.executeQuery())
                {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Song with ID " + songID + " does not exist.");
                    }
                    
                    if (rs.getBoolean("is_public") == makePublic)
                    {
                        if (makePublic) {
                            throw new IllegalStateException("Song is already public.");
                        } else {
                            throw new IllegalStateException("Song is already private.");
                        }
                    }
                }
            }

            //update database
            try (PreparedStatement updateStmt = connect.prepareStatement(updateSql))
            {
                updateStmt.setBoolean(1, makePublic);
                updateStmt.setInt(2, songID);

                if (updateStmt.executeUpdate() > 0)
                {
                    if (makePublic) {
                        System.out.println("Song set to public successfully.");
                    } else {
                        System.out.println("Song set to private successfully.");
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error during visibility update: " + e.getMessage(), e);
        }
    }
}