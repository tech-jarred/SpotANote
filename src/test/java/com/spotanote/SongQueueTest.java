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

    /**
     * Checks to see if the SongQueue only keeps track of last MAX_PREVIOUS_SONGS songs played.
     * MAX_PREVIOUS_SONGS defaults to 10, but a overloaded constructor allows for this to be changed.
     * 
     * Enqueue songs with id's 1...13 (which exist in database), invoke getNext() 12 times.
     * Expected state is... {priorSongs: 2, 3, 4, 5, 6, 7, 8, 9, 10, 11} {currentSong: 12} {upcomingSongs: 13}
     * 
     * Do second SongQueue with MAX value of 3
     * Enqueue songs with id's 4, 5, 9, 2, 3, 6, in that order. Invoke getNext() 6 times
     * Expected state is... {priorSongs: 9, 2, 3} {currentSong: 6} {upcomingSongs: }
     * 
     */
    @Test
    void testMaxPreviousSongsTracking(){
        // Initialize song queues.
        SongQueue sq1 = new SongQueue();
        SongQueue sq2 = new SongQueue(3);

        // Perform enqueing and getNext() operations
        for (int i = 1; i <= 13; i++){
            sq1.enqueue(i);            
        }

        for (int i = 0; i < 12; i++){
            sq1.getNext();
        }

        sq2.enqueue(4); sq2.enqueue(5); sq2.enqueue(9); sq2.enqueue(2); sq2.enqueue(3); sq2.enqueue(6);
        for (int i = 1; i <= 6; i++){
            sq2.getNext();
        }

        // Build expected song queues.
        SongQueue expectedSq1 = new SongQueue();
        SongQueue expectedSq2 = new SongQueue(3);

        Song s2 = new Song(2, "Revenge", Duration.ofSeconds(219), "/music/revenge.mp3");
        Song s3 = new Song(3, "Bailando", Duration.ofSeconds(254), "/music/bailando.mp3");
        Song s4 = new Song(4, "Danza Kuduro", Duration.ofSeconds(235), "/music/danza_kuduro.mp3");
        Song s5 = new Song(5, "Don't Mine At Night", Duration.ofSeconds(214),"/music/dont_mine_at_night.mp3");
        Song s6 = new Song(6, "Drive By", Duration.ofSeconds(197), "/music/drive_by.mp3");
        Song s7 = new Song(7, "Fallen Kingdom", Duration.ofSeconds(252), "/music/fallen_kingdom.mp3");
        Song s8 = new Song(8, "Hips Don't Lie", Duration.ofSeconds(218), "/music/hips_dont_lie.mp3");
        Song s9 = new Song(9, "Lao a Lao", Duration.ofSeconds(184), "/music/lao_a_lao.mp3");
        Song s10 = new Song(10, "Ocean Man", Duration.ofSeconds(126), "/music/ocean_man.mp3");
        Song s11 = new Song(11, "Pa'lla Voy", Duration.ofSeconds(263), "/music/palla_voy.mp3");
        Song s12 = new Song(12, "Triforce Medley", Duration.ofSeconds(242), "/music/tloz_triforce_medley_40.mp3");
        Song s13 = new Song(13, "Vivir Mi Vida", Duration.ofSeconds(252), "/music/vivir_mi_vida.mp3");

        expectedSq1.addToPriorSongs(s2); expectedSq1.addToPriorSongs(s3); expectedSq1.addToPriorSongs(s4); expectedSq1.addToPriorSongs(s5); expectedSq1.addToPriorSongs(s6); expectedSq1.addToPriorSongs(s7); expectedSq1.addToPriorSongs(s8); expectedSq1.addToPriorSongs(s9); expectedSq1.addToPriorSongs(s10); expectedSq1.addToPriorSongs(s11);
        expectedSq1.setCurrentSong(s12); expectedSq1.enqueue(s13);

        expectedSq2.addToPriorSongs(s9); expectedSq2.addToPriorSongs(s2); expectedSq2.addToPriorSongs(s3);
        expectedSq2.setCurrentSong(s6);

        assertTrue(SongQueue.EQN(expectedSq1, sq1));
        assertTrue(SongQueue.EQN(expectedSq2, sq2));
    }

    @Test
    void testEQN() {
        // 1. Setup sample Song objects
        Song song1 = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");
        Song song2 = new Song(2, "Revenge", Duration.ofSeconds(219), "/music/revenge.mp3");
        Song song3 = new Song(3, "Bailando", Duration.ofSeconds(254), "/music/bailando.mp3");

        // Initialize two empty queues
        SongQueue sq1 = new SongQueue();
        SongQueue sq2 = new SongQueue();

        // Case 1: Both queues are completely empty (Base Case -> True)
        assertTrue(SongQueue.EQN(sq1, sq2), "Two empty queues should be observationally equivalent.");

        // Case 2: One queue has a current song, the other is null (-> False)
        sq1.setCurrentSong(song1);
        assertFalse(SongQueue.EQN(sq1, sq2), "Queues with different currentSong values should not be equivalent.");

        // Case 3: Both queues have identical currentSong (-> True)
        sq2.setCurrentSong(song1);
        assertTrue(SongQueue.EQN(sq1, sq2), "Queues with matching currentSong should be equivalent.");

        // Case 4: Populating upcomingSongs - mismatched queue contents (-> False)
        sq1.enqueue(song2);
        sq1.enqueue(song3);
        sq2.enqueue(song2);
        assertFalse(SongQueue.EQN(sq1, sq2), "Queues with different numbers of upcoming songs should not be equivalent.");

        // Case 5: Matching upcomingSongs (-> True)
        sq2.enqueue(song3);
        assertTrue(SongQueue.EQN(sq1, sq2), "Queues with identical currentSong and upcomingSongs should be equivalent.");

        // Case 6: Mismatched order in upcomingSongs (-> False)
        SongQueue sq3 = new SongQueue();
        SongQueue sq4 = new SongQueue();
        sq3.enqueue(song2);
        sq3.enqueue(song3);
        sq4.enqueue(song3); // Reversed order
        sq4.enqueue(song2);
        assertFalse(SongQueue.EQN(sq3, sq4), "Queues with different upcoming song order should not be equivalent.");

        // Case 7: Matching priorSongs history (-> True)
        sq1.addToPriorSongs(song1);
        sq2.addToPriorSongs(song1);
        assertTrue(SongQueue.EQN(sq1, sq2), "Queues with matching prior history, current song, and upcoming queue should be equivalent.");

        // Case 8: Mismatched priorSongs history (-> False)
        SongQueue sq5 = new SongQueue();
        SongQueue sq6 = new SongQueue();
        sq5.addToPriorSongs(song1);
        sq6.addToPriorSongs(song2);
        assertFalse(SongQueue.EQN(sq5, sq6), "Queues with different prior song history should not be equivalent.");
    }

    /**
     * Checks to see if the shuffle method works.
     * 
     * Create a queue with song ids 1...10 in increasing numerical order based on id.
     */
    @Test
    void testShuffle(){
        SongQueue sq = new SongQueue();
        SongQueue sqOrdered = new SongQueue();
        for (int i = 1; i <= 10; i++){
            sq.enqueue(i);
            sqOrdered.enqueue(i);
        }

        // Shuffle SongQueue sq.
        sq.shuffle();

        // Compare the two. Note this test has a 1/3628800 = 0.00002% chance of failing due to the nature of permutations.
        assertFalse(SongQueue.EQN(sq, sqOrdered));
    }
}