package com.spotanote;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongTest {
    // make instance of Song class to test against.
    private Song song = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");

    /**
     * Tests to see that constructor for Song class correctly assigns values to its attributes.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals(1, song.getId());
        assertEquals("Please Please Please", song.getName());
        assertEquals(Duration.ofSeconds(187), song.getDuration());
        assertEquals("/music/please_please_please.mp3", song.getFilePath());
    }
}