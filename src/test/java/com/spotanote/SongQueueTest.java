package com.spotanote;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        sq.queue(s1);
        sq.addToPriorSongs(s2);

        // Check if methods return false on non-empty upcoming and prior song queues.
        assertFalse(sq.isQueuedSongsEmpty() && sq.isPriorSongsEmpty(), "SongQueue was expected to be not empty, but it was.");
    }
}
