package com.spotanote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class SongTest {
    

    @Test
    void testGetSongName() {
        // testing to see if the song id is valid
        int validSongId = 1;
        String expectedSongName = "Test Song";
        String actualSongName = Song.getSongName(validSongId);
        assert expectedSongName.equals(actualSongName) : "Test case 1 failed: Expected " + expectedSongName + ", but got " + actualSongName;

        // testing to see if the song id is invalid
        int invalidSongId = 9999;
        String expectedNullResult = null;
        String actualNullResult = Song.getSongName(invalidSongId);
        assert expectedNullResult == actualNullResult : "Test case 2 failed: Expected null, but got " + actualNullResult;

        // testing to see if the song id is null
        int nullSongId = 0;
        String expectedNullResultForZero = null;
        String actualNullResultForZero = Song.getSongName(nullSongId);
        assert expectedNullResultForZero == actualNullResultForZero : "Test case 3 failed: Expected null, but got " + actualNullResultForZero;

    }

    @Test
    void testGetAllSongs() {
        // grabbing all of the songs
        try 
        {
            Song[] allSongs = Song.getAllSongs();
            assert allSongs != null : "Test case 1 failed: Expected non-null result, but got null.";
        } 
        catch (Exception e) {
            fail("getAllSongs threw an unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testGetSongArtist()
    {
        //testing to see if the song can return the artist
        String artistId = Song.getSongArtist(1);
        assertEquals("1", artistId, "testGeSongArtist Failed");
    }

    @Test
    void testMakeSongPublic() throws SQLException
    {
        Song.makeSongPublic(1);

        assertTrue(
            Song.isPublic(1),
            "TestMakeSongPublic Failed"
        );    
    }

    @Test 
    void testMakeSongPrivate() throws SQLException
    {
        Song.makeSongPrivate(1);

        assertFalse(
            Song.isPublic(1),
            "TestMAkeSongPrivate Failed"
        ); 
    }

    @Test
    void deleteSong() throws SQLException
    {
        boolean result = Song.deleteSong(1);

        assertTrue(
            result,
            "Song should be successfully deleted."
        );
        assertNull(
            Song.getSongName(1),
            "Deleted Song Gone From Database"
        );
    }


}
