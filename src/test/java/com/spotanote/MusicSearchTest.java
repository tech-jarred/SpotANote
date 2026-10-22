
package  com.spotanote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * MusicSearchTest class
 * Focuses on testing the MusicSearch class
 */
class MusicSearchTest
{

    int songID = 0;
    int playlistID = 0;

    /*
     * Sets up environment for testing before each test is ran
     */
    @BeforeEach
    void setUp() throws SQLException
    {
        String sqlSong = "INSERT INTO TestSong (song_name, song_duration, file_path, record_member_id, is_public) VALUES ('Please Please Please', 181, '/testsongs/pleasepleaseplease.mp3', 1, TRUE)";
        String sqlPlaylist = "INSERT INTO TestPlaylist (user_id, playlist_name, is_public) VALUES (1, 'Test Playlist', TRUE)";

        try (Connection connect = DBExplorer.getConnection()) {
            //create a public song to test with
            try (PreparedStatement stmt = connect.prepareStatement(sqlSong, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next())
                        songID = keys.getInt(1);
                    else
                        throw new SQLException("Failed to retrieve generated key for song.");
                }
            }

            //create a public playlist to test with
            try (PreparedStatement stmt = connect.prepareStatement(sqlPlaylist, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next())
                        playlistID = keys.getInt(1);
                    else
                        throw new SQLException("Failed to retrieve generated key for playlist.");
                }
            }
        }
    }

    /*
     * Tears down environemnt after each test is ran
     */
    @AfterEach
    void tearDown()
    {
        String sqlSong = "DELETE FROM TestSong WHERE id = ?";
        String sqlPlaylist = "DELETE FROM TestPlaylist WHERE id = ?";
        
        try (Connection connect = DBExplorer.getConnection()) {
            try (PreparedStatement stmt = connect.prepareStatement(sqlSong)) {
                stmt.setInt(1, songID);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connect.prepareStatement(sqlPlaylist)) {
                stmt.setInt(1, playlistID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Tear down failed during row deletion: " + e.getMessage());
        }
    }

    /**
     * Tests to see if a song is correctly searched for using its full song name
     */
    @Test
    void testSuccessfulSongSearch() throws SQLException
    {
        //searching through the specified table
        List<Integer> ids = MusicSearch.musicSearch("Please Please Please", "TestSong");

        //verifying song list is returned and contains the correct song ID
        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.contains(songID), "List of ids should contain inserted song ID: " + songID);
    }

    /*
     * Tests to see what happens when the alleged full song name is searched for but doesn't show up
     */
    @Test
    void testUnsuccessfulSongSearch() throws SQLException
    {
        //searching a misspelled song
        List<Integer> ids = MusicSearch.musicSearch("pealse", "TestSong");

        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.isEmpty(), "List of ids should be empty when full song title is not found");
    }

    /*
     * Tests to see if a song is correctly searched for using some of its name
     */
    @Test
    void testSuccessfulPartialSongSearch() throws SQLException
    {
        //searching using part of the song name
        List<Integer> ids = MusicSearch.musicSearch("Please", "TestSong");

        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.contains(songID), "Partial search should find the song ID");
    }

    /*
     * Tests to see what happens when part of the song name is searched for but doesn't show up
     */
    @Test
    void testUnsuccessfulPartialSongSearch() throws SQLException
    {
        //searching a misspelled song
        List<Integer> ids = MusicSearch.musicSearch("pealse", "TestSong");
        
        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.isEmpty(), "List of ids should be empty when partial song title is not found");
    }

    /*
     * Tests to see what happens when searching for a song that doesn't exist publicly in the database
     */
    @Test
    void testSearchSongDoesNotExistPublicly() throws SQLException
    {
        //change public song to private
        String sql = "UPDATE TestSong SET is_public = FALSE WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection()) {
            try (PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setInt(1, songID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Fail to make song private for testing purposes: " + e.getMessage());
        }

        //searching for the privated song
        List<Integer> ids = MusicSearch.musicSearch("Please Please Please", "TestSong");

        //assertFalse(ids.contains(songID), "Private songs should not be returned in search results");
        assertFalse(ids.contains(songID), "Private songs should not be returned in search results");
    }

    /*
     * Tests to see what happens when searching for a song that doesn't exist at all in the database
     */
    @Test
    void testSearchSongDoesNotExistAtAll() throws SQLException
    {
        //searching for a song that doesn't exist
        List<Integer> ids = MusicSearch.musicSearch("SongDoesNotExist", "TestSong");

        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.isEmpty(), "List of ids should be empty when full song title is not found");
    }

    /*
     * Tests to see if a playlist is correctly searched for using its full playlist name
     */
    @Test
    void testSuccessfulPlaylistSearch() throws SQLException
    {
        //searching through the specified table
        List<Integer> ids = MusicSearch.musicSearch("Test Playlist", "TestPlaylist");

        //verifying list of playlist ids is returned and contains the correct playlist ID
        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.contains(playlistID), "List of ids should contain inserted playlist ID: " + playlistID);
    }

    /*
     * Test to see what happens when the full playlist name is searched for but doesn't show up
     */
    @Test
    void testUnsuccessfulPlaylistSearch() throws SQLException
    {
        //searching a misspelled playlist
        List<Integer> ids = MusicSearch.musicSearch("Tset pyallist", "TestPlaylist");

        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.isEmpty(), "List of ids should be empty when full playlist name is not found");
    }

    /*
     * Tests to see if a playlist is correctly searched for using some if its name
     */
    @Test
    void testSuccessfulPartialPlaylistSearch() throws SQLException
    {
        //searching using part of the song name
        List<Integer> ids = MusicSearch.musicSearch("Test", "TestPlaylist");

        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.contains(playlistID), "Partial search should find the playlist ID");
    }

    /*
     * Tests to see what happens when part of the playlist name is searched but doesn't show up
     */
    @Test
    void testUnsuccessfulPartialPlaylistSearch() throws SQLException
    {
        //searching a misspelled playlist
        List<Integer> ids = MusicSearch.musicSearch("tset", "TestPlaylist");
        
        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.isEmpty(), "List of ids should be empty when partial playlist name is not found");
    }

    /*
     * Tests to see what happens when searching for a playlist that doesn't exist publicly in the database
     */
    @Test
    void testSearchPlaylistDoesNotExistPublicly() throws SQLException
    {
        //change public playlist to private
        String sql = "UPDATE TestPlaylist SET is_public = FALSE WHERE id = ?";
        try (Connection connect = DBExplorer.getConnection()) {
            try (PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setInt(1, playlistID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Fail to make song private for testing purposes: " + e.getMessage());
        }
    }

    /*
     * Tests to see what happens when searching for a playlist that doesn't exhaust at all in the database
     */
    @Test
    void testSearchPlaylistDoesNotExistAtAll() throws SQLException
    {
        //searching for a song that doesn't exist
        List<Integer> ids = MusicSearch.musicSearch("PlaylistDoesNotExist", "TestPlaylist");

        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.isEmpty(), "List of ids should be empty when full playlist name is not found");
    }
}