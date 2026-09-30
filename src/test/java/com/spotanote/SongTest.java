package com.spotanote;

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
        Song[] allSongs = Song.getAllSongs();
        assert allSongs != null : "Test case 1 failed: Expected non-null result, but got null.";

        // then we have to compare it to the actual
        String expectedSongName = "Test Song";
        assert java
    }

    @Test
    void testGetSongArtist()
    {
        //testing to see if the song can return the artist
        int artistId = Song.getSongArtist(1);
    }

    @Test
    void testMakeSongPublic()
    {

    }

    @Test 
    void testMakeSongPrivate()
    {

    }

    @Test
    void deleteSong()
    {

    }


}
