package com.spotanote;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MusicLikeTest {
    private MusicLike musicLike;
    private final int TEST_USER_ID = 1;

    @BeforeEach
    void setUp() {
        musicLike = new MusicLike();
    }

    @AfterEach
    void tearDown() {
        String query = "DELETE FROM UsersLikedSongs WHERE user_id > 0 AND song_id > 0";

        try (Connection conn = DBExplorer.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Use executeUpdate() for DELETE operations
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Database cleanup failed during tearDown: " + e.getMessage());
        }
    }
    
    /**
     * Checks to make sure a song is added to users liked songs.
     * 
     * Let the user have id 1 and the song have id 1. Both exist in the database.
     */
    @Test
    void testSuccessfulLike() {
        int testSongID = 1;
        // Ensure song is not currently liked before test
        try {
            musicLike.dislikeSong(TEST_USER_ID, testSongID);
        } catch (IllegalArgumentException ignored) {}

        // Execute likeSong
        assertDoesNotThrow(() -> musicLike.likeSong(TEST_USER_ID, testSongID));

        // Verify postcondition: song exists in user's liked list
        List<SongSummary> likedSongs = musicLike.getUsersLikedSongs(TEST_USER_ID);
        boolean isLiked = likedSongs.stream()
                .anyMatch(song -> song.songId() == testSongID);

        assertTrue(isLiked, "The liked song should be present in the user's liked songs list.");
    }

    /**
     * Checks to see if a song is successfully removed from the database
     */
    @Test
    void testSuccessfulDislike() {
        // Precondition: Song is currently liked by the user
        int testSongID = 1;
        musicLike.likeSong(TEST_USER_ID, testSongID);

        // Execute dislikeSong
        assertDoesNotThrow(() -> musicLike.dislikeSong(TEST_USER_ID, testSongID));

        // Verify postcondition: song is no longer in user's liked list
        List<SongSummary> likedSongs = musicLike.getUsersLikedSongs(TEST_USER_ID);
        boolean isLiked = likedSongs.stream()
                .anyMatch(song -> song.songId() == testSongID);

        assertFalse(isLiked, "The song should be removed from the user's liked songs list after disliking.");
    }

    /**
     * 
     */
    @Test
    void testLikeSongWhenDatabaseDown() {
        int testSongID = 1;
        try (MockedStatic<DBExplorer> mockedDb = mockStatic(DBExplorer.class)) {
            // Force DBExplorer.getConnection() to throw a SQLException
            mockedDb.when(DBExplorer::getConnection).thenThrow(new SQLException("Database unreachable"));

            // Verify that likeSong catches the SQLException and throws a RuntimeException
            assertThrows(RuntimeException.class, () -> {
                musicLike.likeSong(TEST_USER_ID, testSongID);
            });
        }
    }

    /**
     * 
     */
    @Test
    void testSuccessfulGettingLikedSongs() {
        // Precondition: User has liked songs with ids 1, 12, and 5
        musicLike.likeSong(TEST_USER_ID, 1);
        musicLike.likeSong(TEST_USER_ID, 12);
        musicLike.likeSong(TEST_USER_ID, 5);

        // Get user's liked songs from database.
        List<SongSummary> likedSongs = musicLike.getUsersLikedSongs(TEST_USER_ID);

        // 3. Assert: Extract song IDs from SongSummary objects and check contents
        List<Integer> returnedSongIds = likedSongs.stream().map(SongSummary::songId).toList();

        assertEquals(3, returnedSongIds.size(), "User should have exactly 3 liked songs.");
        assertTrue(returnedSongIds.containsAll(List.of(1, 12, 5)), "Returned list must contain song IDs 1, 12, and 5.");
    }

    /**
     * 
     */
    @Test
    void testUnsuccessfulGettingLikedSongsWhenDatabaseDown() {
        try (MockedStatic<DBExplorer> mockedDb = mockStatic(DBExplorer.class)) {
            mockedDb.when(DBExplorer::getConnection).thenThrow(new SQLException("Database connection lost"));

            assertThrows(RuntimeException.class, () -> {
                musicLike.getUsersLikedSongs(TEST_USER_ID);
            });
        }
    }
}
