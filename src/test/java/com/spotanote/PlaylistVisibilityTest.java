/*
 * PlaylistVisibilityTest class
 * Focuses on testing the PlaylistVisibility class
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


public class PlaylistVisibilityTest {
    
    //keeps track of playlistIDs over all tests
    private int pubPlaylistID = 0;
    private int privPlaylistID = 0;

    /*
     * Sets up environment for testing before each test is ran
     */
    @BeforeEach
    void setUp()
    {
        //creates two new playlist rows to test with
        try (Connection connect = DBExplorer.getConnection()) {
            pubPlaylistID = createPlaylist(connect, true);
            privPlaylistID = createPlaylist(connect, false);
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
        //gets rid of the playlist rows that was made at the beginning of the test
        deletePlaylist(pubPlaylistID);
        deletePlaylist(privPlaylistID);
        pubPlaylistID = 0;
        privPlaylistID = 0;
    }

    /*
     * Tests to see if the playlist has been privated successfully
     */
    @Test
    void successfullPrivatePlaylistTest()
    {
        PlaylistVisibility.privatePlaylist(pubPlaylistID);
        assertTrue(checkIsPrivateInDatabase(pubPlaylistID), "Playlist Private Unsuccessful: playlist is still public");
    }

    /*
     * Tests to see what happens when the playlist wasn't actually privated b/c it doesn't exist
     */
    @Test
    void unsuccessfulPrivatePlaylistTest()
    {
        //manually get rid of a playlist so that it no longer exists
        deletePlaylist(pubPlaylistID);

        //attemept to private the deleted playlist ID and throw an exception
        assertThrows(IllegalArgumentException.class, () -> {
            PlaylistVisibility.privatePlaylist(pubPlaylistID);
        });
    }

    /*
     * Tests to see what happens when the incorrect playlist is privated
    */
    @Test
    void incorrectPlaylistPrivatedTest()
    {        
        int incorrectPlaylistID;

        try (Connection connect = DBExplorer.getConnection()) {
            //create incorrect playlist
            incorrectPlaylistID = createPlaylist(connect, true);
            //private the incorrect playlist
            PlaylistVisibility.privatePlaylist(incorrectPlaylistID);
        } catch (SQLException e) {
            System.err.println("Playlist Private Unsuccessful: the incorrect playlist was privated " + e.getMessage());
        } finally {
        }
    }

    /*
     * Tests to see what happens when trying to private an already private playlist
    */
    @Test
    void alreadyPrivatePlaylistTest()
    {
        assertThrows(IllegalStateException.class, () -> {
            PlaylistVisibility.privatePlaylist(privPlaylistID);
        });
    }

    /*
     * createPlaylist - setUp() helper method to create playlists
     * 
     *  @return int playlistID
     */
    private int createPlaylist(Connection connect, boolean isPublic) throws SQLException {
        String sql = "INSERT INTO Playlist (user_id, playlist_name, is_public) VALUES (1, 'Test Playlist', ?)";
        try (PreparedStatement stmt = connect.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, isPublic);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve generated key for playlist.");
    }

    /*
     * checkIsPrivateInDatabase - successfullPrivatePlaylistTest() helper method to check viewable status through the database
     * 
     *  @return True if the playlist is private, else return False
     */
    private boolean checkIsPrivateInDatabase(int PLid)
    {
        String sqlSelect = "SELECT is_public FROM Playlist WHERE id = ?";
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
     * checkIsPrivateInDatabase - IncorrectPlaylistPrivatedTest() helper method to check viewable status through the database
     * 
     *  @return True if the playlist is private, else return False
     */
    private boolean checkIsPublicInDatabase(int PLid)
    {
        String sqlSelect = "SELECT is_public FROM Playlist WHERE id = ?";
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
     * deletePlaylist(int id) - helper method to delete playlists by a given int id
     */
    private void deletePlaylist(int id)
    {
        String sql = "DELETE FROM Playlist WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Setup failed during row deletion: " + e.getMessage());
        }
    }

    /*
     * Tests to see if the playlist has been publicized successfully
     */
    @Test
    void successfullPublicPlaylistTest()
    {
        PlaylistVisibility.publicPlaylist(privPlaylistID);
        assertTrue(checkIsPublicInDatabase(privPlaylistID), "Playlist Public Unsuccessful: playlist is still private");
    }

    /*
     * Tests to see what happens when the playlist wasn't actually publicized b/c it doesn't exist
     */
    @Test
    void unsuccessfulPublicPlaylistTest()
    {
        //manually get rid of a playlist so that it no longer exists
        deletePlaylist(privPlaylistID);

        //attempt to public the deleted playlist ID and throw an exception
        assertThrows(IllegalArgumentException.class, () ->{
            PlaylistVisibility.publicPlaylist(privPlaylistID);
        });
    }

    /*
     * Tests to see what happens when the incorrect playlist is publicized
    */
    @Test
    void incorrectPlaylistPublicTest()
    {
        int incorrectPlaylistID;

        try (Connection connect = DBExplorer.getConnection())
        {
            //create incorrect playlist
            incorrectPlaylistID = createPlaylist(connect, false);
            //public the incorrect playlist
            PlaylistVisibility.publicPlaylist(incorrectPlaylistID);
        } catch (SQLException e) {
            System.err.println("Playlist Public Unsuccessful: the incorrect playlist was publisized " + e.getMessage());
        }
    }

    /*
     * Tests to see what happens when trying to publicize an already public playlist
    */
    @Test
    void alreadyPublicPlaylistTest()
    {
        assertThrows(IllegalStateException.class, () -> {
            PlaylistVisibility.publicPlaylist(pubPlaylistID);
        });
    }
}