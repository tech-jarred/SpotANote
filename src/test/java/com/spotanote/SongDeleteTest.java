/*
 * SongDeleteTest class
 * Focuses on testing the SongDelete class
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

public class SongDeleteTest {

    private int songID = 0;

    /*
     * Sets up environment for testing before each test is ran
     */
    @BeforeEach
    void setUp()
    {
        //creates a new song row to test with
        try (Connection connect = DBExplorer.getConnection()) {
            songID = createSong(connect);
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
        //gets rid of the song row that was made at the beginning of the test
        String sql = "DELETE FROM Song WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setInt(1, songID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Setup failed during row deletion: " + e.getMessage());
        }
        songID = 0;
    }

    /*
     * Tests to see if the song was successfully deleted
     */
    @Test
    void successfullSongDeletionTest()
    {
        SongDelete.deleteSong(songID);
        assertFalse(checkIdExist(songID), "Song Deletion Unsuccessful: song still exists");
    }

    /*
     * Tests to see what happens when the incorrect song is deleted
     */
    @Test
    void incorrectSongDeletionTest()
    {
        int incorrectSongID;

        try (Connection connect = DBExplorer.getConnection())
        {
            //create incorrect song
            incorrectSongID = createSong(connect);
            //delete the incorrect song
            SongDelete.deleteSong(incorrectSongID);
        } catch (SQLException e) {
            System.err.println("Song Deletion Unsuccessful: the incorrect song was deleted " + e.getMessage());
        }
    }

    /*
     * Tests to see what happens when the song is privated instead of deleted
     */
    @Test
    void songIncorrectlyPrivatedTest() throws SQLException
    {
        //takes the song created in setUp() and makes it public
        String sql = "UPDATE Song SET is_public = True WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection())
        {
            try (PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setInt(1, songID); // The ID of the existing song you want to update

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Update failed: Song with ID " + songID + " does not exist.");
                }
            }
        }
        //accidentally making the song private instead of deleting it
        SongVisibility.privateSong(songID);
        assertTrue(checkIdExist(songID), "Song Deletion Unsuccessful: song was privated instead of deleted");
    }

    /*
     * Tests to see what happens when a non-existing song is trying to be deleted
     */
    @Test
    void songDoesNotExistTest()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            SongDelete.deleteSong(0);
        });
    }

    /*
     * createSong - setUp() helper method to create songs
     * 
     *  @return int songID
     */
    private int createSong(Connection connect) throws SQLException {
        String sql = "INSERT INTO Song (song_name, song_duration, file_path, record_member_id) VALUES ('Test Song', 100, '/testsongs/testsong.mp3', 1)";
        try (PreparedStatement stmt = connect.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve generated key for song.");
    }

    /*
     * checkIdExist - helper method to check if an ID exists in the database
     * 
     *  returns true if ID exists, false if it does not
     */
    private boolean checkIdExist(int id)
    {
        String checkSql = "SELECT id FROM Song WHERE id = ?";
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