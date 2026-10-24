package com.spotanote;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SongQueueTest {
    // ... test methods to come

    /**
     * Determines if methods to check if the upcoming or prior queue is empty on SongQueue object.
     */
    @Test
    void testIsEmptyMethodsWhenEmpty(){
        // Create instance of SongQueue object.
        SongQueue sq = new SongQueue();

        // Initialized object should be empty, check by calling isEmpty method.
        assertTrue(sq.isQueuedSongsEmpty() && sq.isPriorSongsEmpty(), "SongQueue was expected to be empty, but was not.");
    }

    /**
     * Determines if methods to check if the upcoming or prior queue is empty on SongQueue object when it contains a song object in both the prior song queue and upcoming song queue.
     * 
     * Let the upcoming song object be Song s1 = Song song = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3")
     * Let the prior song object be Song s2 = new Song(3, "Hips Don't Lie", Duration.ofSeconds(218), "/music/hips_dont_lie.mp3")
     * 
     */
    @Test
    void testIsEmptyMethodsWhenNonEmpty(){
        // Create instance of SongQueue object.
        SongQueue sq = new SongQueue();

        // Add song to upcoming songs and prior songs
        Song s1 = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");
        Song s2 = new Song(3, "Hips Don't Lie", Duration.ofSeconds(218), "/music/hips_dont_lie.mp3");
        sq.enqueue(s1);
        sq.addToPriorSongs(s2);

        // Check if methods return false on non-empty upcoming and prior song queues.
        assertFalse(sq.isQueuedSongsEmpty() && sq.isPriorSongsEmpty(), "SongQueue was expected to be not empty, but it was.");
    }

    /**
     * Tests to see if SongQueue can return the previously played song or next played song.
     * 
     * Let song id 1 be the previously played song.
     * Let song id 3 be the next song in queue.
     */
    @Test
    void testGetNextAndGetPriorSong(){
        // Instantiate a song queue with a prior song and upcoming song.
        SongQueue sq = new SongQueue();

        // Add songs to prior songs and upcoming songs.
        Song s1 = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");
        Song s3 = new Song(3, "Hips Don't Lie", Duration.ofSeconds(218), "/music/hips_dont_lie.mp3");
        sq.addToPriorSongs(s1);
        sq.enqueue(s3);

        // Create a different instance of song objects to force comparison of attributes, not by memory location reference.
        Song expectedS1 = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");
        Song expectedS3 = new Song(3, "Hips Don't Lie", Duration.ofSeconds(218), "/music/hips_dont_lie.mp3");

        // Get prior song and next song from SongQueue.
        Song priorSong = sq.getPrior();
        Song nextSong = sq.getNext();

        // Check if songs obtained were ones expected.
        assertTrue(expectedS1.equals(priorSong));
        assertTrue(expectedS3.equals(nextSong));
    }

    /**
     * Determines is the SongQueue is able to add a new song to queue when just given an id number.
     * 
     * Let the song id be 1 (Please Please Please), song id 3 (Bailando), song id 2 (Revenge)
     */
    @Test
    void testQueueingSongFromId(){
        // Create instance of SongQueue
        SongQueue sq = new SongQueue();
        
        // pass song id 1 into queue method, followed by song id 3 and song id 2
        sq.enqueue(1); sq.enqueue(3); sq.enqueue(2);

        // Check if the SongQueue is observationally equivalent to what we expect it to be (i.e., there is a song object for please please please at the head of the upcomingSongs)
        Song s1 = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");
        Song s2 = new Song(2, "Revenge", Duration.ofSeconds(219), "/music/revenge.mp3");
        Song s3 = new Song(3, "Bailando", Duration.ofSeconds(254), "/music/bailando.mp3");
        SongQueue sqExpected = new SongQueue(); sqExpected.enqueue(s1); sqExpected.enqueue(s3); sqExpected.enqueue(s2);

        assertTrue(SongQueue.EQN(sq, sqExpected));
    }

    /**
     * Checks to see if correct exceptions are thrown when there is an error queueing a song.
     * 
     */
    @Test
    void testFailedQueuingSong(){
        // Initialize SongQueue
        SongQueue sq = new SongQueue();

        /* Check to see if correct errors are thrown in different cases. */
        // Case when passed song object but its null.
        assertThrows(IllegalArgumentException.class, () -> {
            sq.enqueue(null);
        });

        // Case when song id doesn't exist in database
        assertThrows(IllegalArgumentException.class, () -> {
            sq.enqueue(1000); // song id of 1000 does not exist in test database
        });

        // Case when database connection fails or server is down
        try (MockedStatic<DBExplorer> mockedDb = Mockito.mockStatic(DBExplorer.class)) {
            // Force DBExplorer.getConnection() to throw SQLException as if DB is offline
            mockedDb.when(DBExplorer::getConnection)
                    .thenThrow(new SQLException("Connection refused / Database offline"));

            assertThrows(RuntimeException.class, () -> {
                sq.enqueue(1); sq.enqueue(3); sq.enqueue(2); // Any ID will trigger the SQLException and re-throw RuntimeException
            });
        }

    }
}
