package com.spotanote;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SongQueueTest {
    // ... test methods to come

    /**
     * Determines if methods to check if the upcoming or prior queue is empty on SongQueue object.
     */
    @Test
    void testIsEmptyMethods(){
        // Create instance of SongQueue object.
        SongQueue sq = new SongQueue();

        // Initialized object should be empty, check by calling isEmpty method.
        assertTrue(sq.isQueuedSongsEmpty() && sq.isPriorSongsEmpty(), "SongQueue was expected to be empty, but was not.");
    }
}
