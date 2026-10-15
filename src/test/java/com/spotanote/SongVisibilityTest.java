/*
 * SongVisibilityTest class
 * Focuses on testing the SongVisibility class
 */

//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SongVisibilityTest {
    
    //keeps track of songIDs over all tests
    private int pubSongID = 0;
    private int privSongID = 0;

    /*
     * Sets up environment for testing before each test is ran
     */
    @BeforeEach
    void setUp()
    {
        //creates two new song rows to test with
        try (Connection connect = DBExplorer.getConnection()) {
            pubSongID = createSong(connect, true);
            privSongID = createSong(connect, false);
        } catch (SQLException e) {
            System.err.println("Error setting up before each test: " + e.getMessage());
        }
    }

    /*
     * Tears down environment after each test is ran
     */
    @AfterEach
    void tearDown()
    {
        //gets rid of the song rows that was made at the beginning of the test
        deleteSong(pubSongID);
        deleteSong(privSongID);
        pubSongID = 0;
        privSongID = 0;
    }

    /*
     * Tests to see if the song has been privated successfully
     */
    @Test
    void successfullPrivateSongTest()
    {
        SongVisibility.privateSong(pubSongID);
        assertTrue(checkIsPrivateInDatabase(pubSongID), "Song Private Unsuccessful: song is still public");
    }

    /*
     * Tests to see what happens when the song wasn't actually privated b/c it doesn't exist
     */
    @Test
    void unsuccessfulPrivateSongTest()
    {
        //manually get rid of a song so that it no longer exists
        deleteSong(pubSongID);

        //attemept to private the deleted song ID and throw an exception
        assertThrows(IllegalArgumentException.class, () -> {
            SongVisibility.privateSong(pubSongID);
        });
    }

    /*
     * Tests to see what happens when the incorrect song is privated
    */
    @Test
    void incorrectSongPrivatedTest()
    {        
        int incorrectSongID;

        try (Connection connect = DBExplorer.getConnection()) {
            //create incorrect song
            incorrectSongID = createSong(connect, true);
            //private the incorrect song
            SongVisibility.privateSong(incorrectSongID);
        } catch (SQLException e) {
            System.err.println("Song Private Unsuccessful: the incorrect song was privated " + e.getMessage());
        }
    }

    /*
     * Tests to see what happens when trying to private an already private song
    */
    @Test
    void alreadyPrivateSongTest()
    {
        assertThrows(IllegalStateException.class, () -> {
            SongVisibility.privateSong(privSongID);
        });
    }

    /*
     * createSong - setUp() helper method to create songs
     * 
     *  @return int songID
     */
    private int createSong(Connection connect, boolean isPublic) throws SQLException {
    String sql = "INSERT INTO Song (song_name, song_duration, file_path, record_member_id, is_public) VALUES (?, 100, ?, 1, ?)";
    
    try (PreparedStatement stmt = connect.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
        if (isPublic) {
            stmt.setString(1, "Public Test Song");
            stmt.setString(2, "/testsongs/publicsong.mp3"); // Unique path
            stmt.setBoolean(3, true);
        } else {
            stmt.setString(1, "Private Test Song");
            stmt.setString(2, "/testsongs/privatesong.mp3"); // Unique path
            stmt.setBoolean(3, false);
        }
        
        stmt.executeUpdate();
        try (ResultSet keys = stmt.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
    }
    throw new SQLException("Failed to retrieve generated key for song.");
}

    /*
     * checkIsPrivateInDatabase - successfullPrivateSongTest() helper method to check viewable status through the database
     * 
     *  @return True if the song is private, else return False
     */
    private boolean checkIsPrivateInDatabase(int PLid)
    {
        String sqlSelect = "SELECT is_public FROM Song WHERE id = ?";
        boolean isPublic;
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sqlSelect))
        {
            stmt.setInt(1, PLid);
            try (ResultSet rs = stmt.executeQuery())
            {
                if ((rs.next()) && !(rs.getBoolean("is_public")))
                    return true;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * checkIsPrivateInDatabase - IncorrectSongPrivatedTest() helper method to check viewable status through the database
     * 
     *  @return True if the song is private, else return False
     */
    private boolean checkIsPublicInDatabase(int PLid)
    {
        String sqlSelect = "SELECT is_public FROM Song WHERE id = ?";
        boolean isPublic;
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sqlSelect))
        {
            stmt.setInt(1, PLid);
            try (ResultSet rs = stmt.executeQuery())
            {
                if ((rs.next()) && (rs.getBoolean("is_public")))
                    return true;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * deleteSong(int id) - helper method to delete songs by a given int id
     */
    private void deleteSong(int id)
    {
        String sql = "DELETE FROM Song WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Setup failed during row deletion: " + e.getMessage());
        }
    }

    /*
     * Tests to see if the song has been publicized successfully
     */
    @Test
    void successfullPublicSongTest()
    {
        SongVisibility.publicSong(privSongID);
        assertTrue(checkIsPublicInDatabase(privSongID), "Song Public Unsuccessful: song is still private");
    }

    /*
     * Tests to see what happens when the song wasn't actually publicized b/c it doesn't exist
     */
    @Test
    void unsuccessfulPublicSongTest()
    {
        //manually get rid of a song so that it no longer exists
        deleteSong(privSongID);

        //attempt to public the deleted song ID and throw an exception
        assertThrows(IllegalArgumentException.class, () ->{
            SongVisibility.publicSong(privSongID);
        });
    }

    /*
     * Tests to see what happens when the incorrect song is publicized
    */
    @Test
    void incorrectSongPublicTest()
    {
        int incorrectSongID;

        try (Connection connect = DBExplorer.getConnection())
        {
            //create incorrect song
            incorrectSongID = createSong(connect, false);
            //public the incorrect song
            SongVisibility.publicSong(incorrectSongID);
        } catch (SQLException e) {
            System.err.println("Song Public Unsuccessful: the incorrect song was publisized " + e.getMessage());
        }
    }

    /*
     * Tests to see what happens when trying to publicize an already public song
    */
    @Test
    void alreadyPublicSongTest()
    {
        assertThrows(IllegalStateException.class, () -> {
            SongVisibility.publicSong(pubSongID);
        });
    }
}