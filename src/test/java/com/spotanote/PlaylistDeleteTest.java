/*
 * PlaylistDeleteTest class
 * Focuses on testing the PlaylistDelete class
 */

//imports
package  com.spotanote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlaylistDeleteTest {

    private int playlistID = 0;

    /*
     * Sets up environment for testing before each test is ran
     */
    @BeforeEach
    void setUp()
    {
        //creates a new playlist row to test with
        try (Connection connect = DBExplorer.getConnection()) {
            playlistID = createPlaylist(connect);
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
        //gets rid of the playlist row that was made at the beginning of the test
        String sql = "DELETE FROM Playlist WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setInt(1, playlistID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Setup failed during row deletion: " + e.getMessage());
        }
        playlistID = 0;
    }

    /*
     * Tests to see if the playlist was successfully deleted
     */
    @Test
    void successfullPlaylistDeletionTest()
    {
        PlaylistDelete.deletePlaylist(playlistID);
        assertFalse(checkIdExist(playlistID), "Playlist Deletion Unsuccessful: playlist still exists");
    }

    /*
     * Tests to see what happens when the incorrect playlist is deleted
     */
    @Test
    void incorrectPlaylistDeletionTest()
    {
        int incorrectPlaylistID;

        try (Connection connect = DBExplorer.getConnection())
        {
            //create incorrect playlist
            incorrectPlaylistID = createPlaylist(connect);
            //delete the incorrect playlist
            PlaylistDelete.deletePlaylist(incorrectPlaylistID);
        } catch (SQLException e) {
            System.err.println("Playlist Deletion Unsuccessful: the incorrect playlist was deleted " + e.getMessage());
        }
    }

    /*
     * Tests to see what happens when the playlist is privated instead of deleted
     */
    @Test
    void playlistIncorrectlyPrivatedTest()
    {
        PlaylistVisibility.privatePlaylist(playlistID);
        assertTrue(checkIdExist(playlistID), "Playlist Deletion Unsuccessful: playlist was privated instead of deleted");
    }

    /*
     * Tests to see what happens when a non-existing playlist is trying to be deleted
     */
    @Test
    void playlistDoesNotExistTest()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            PlaylistDelete.deletePlaylist(0);
        });
    }

    /*
     * createPlaylist - setUp() helper method to create playlists
     * 
     *  @return int playlistID
     */
    private int createPlaylist(Connection connect) throws SQLException {
        String sql = "INSERT INTO Playlist (user_id, playlist_name, is_public) VALUES (1, 'Test Playlist', TRUE)";
        try (PreparedStatement stmt = connect.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve generated key for playlist.");
    }

    /*
     * checkIdExist - helper method to check if an ID exists in the database
     * 
     *  returns true if ID exists, false if it does not
     */
    private boolean checkIdExist(int id)
    {
        String checkSql = "SELECT id FROM Playlist WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection())
        {
            try (PreparedStatement checkstmt = connect.prepareStatement(checkSql))
            {
                checkstmt.setInt(1, id);
                //is comparison is null, there is nod id
                try (ResultSet rs = checkstmt.executeQuery())
                {
                    if (rs.next()) 
                    {
                        return true;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}