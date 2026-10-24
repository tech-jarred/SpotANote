package com.spotanote;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    /**
     * Tests to see that method to set time stamps from seconds works on a valid input (secondsPassed < duration of song)
     */
    @Test
    void testSettingTimeStampValidInput(){
        song.setTimeStampFromSeconds(25);
        assertEquals(Duration.ofSeconds(25), song.getTimeStamp());
    }

    /**
     * Tests to see that method to set time stamps from seconds will now set time stamp to new value if input is invalid (< 0 or > songDuration)
     */
    @Test
    void testSettingTimeStampInvalidInput(){
        song.setTimeStampFromSeconds(1000);
        assertEquals(Duration.ZERO, song.getTimeStamp());
    }

    /**
     * Tests to see if the equals method returns true when two song objects are equal and false when they aren't.
     */
    @Test
    void testSongEquals(){
        Song equalSong = new Song(1, "Please Please Please", Duration.ofSeconds(187), "/music/please_please_please.mp3");
        Song notEqualSong = new Song(2, "Revenge", Duration.ofSeconds(219), "/music/revenge.mp3");
        Integer number = 1;

        assertTrue(song.equals(song));
        assertFalse(song.equals(null));
        assertFalse(song.equals(number));
        assertTrue(song.equals(equalSong));
        assertFalse(song.equals(notEqualSong));
    }

}