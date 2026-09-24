/*
 * MusicUploadTest class
 * Focuses on testing the MusicUpload class
 */

 package com.spotanote;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class MusicUploadTest {
    /*@Test
    void sampleTest(){
        int result = 2 + 2;
        assertEquals(4, result, "Sample Unit Test");
    }*/

    @Test
    void validUploadTest()
    {
        String songName = "Please Please Please"; //please please please work lol
        int songLength = 181;
        String filePath = "/music/pleasepleaseplease.mp3"; //not actual mp3 yet
        int recordMemberID = 1; //placeholder recordMemberID b/c real values are not in database yet

        int generatedID = MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);

        //asserts that the generatedID is over 0 meaning an actual entry was created in the database
        assertTrue(generatedID > 0, "Music Upload Unsuccessful: music ID does not exist, instead it was " + generatedID);
    }

    @Test
    void missingFileTest()
    {
        String songName = "Please Please Please";
        int songLength = 181;
        String filePath = "";
        int recordMemberID = 1;

        //asserts that executing without a file throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);
        });
    }

    @Test
    void missingSongNameTest()
    {
        String songName = "";
        int songLength = 181;
        String filePath = "/music/pleasepleaseplease.mp3";
        int recordMemberID = 1;

        //asserts that executing without a song name an exception
        assertThrows(IllegalArgumentException.class, () -> {
            MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);
        });
    }

    @Test
    void missingRecordTest()
    {
        String songName = "Please Please Please";
        int songLength = 181;
        String filePath = "/music/pleasepleaseplease.mp3";
        int recordMemberID = 0;

        //asserts that executing without a record throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);
        });
    }

    @Test
    void missingDurationTest()
    {
        String songName = "Please Please Please";
        int songLength = 0;
        String filePath = "/music/pleasepleaseplease.mp3";
        int recordMemberID = 1;

        //asserts that executing without a song length throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);
        });
    }

    @Test
    void doubleFileUploadTest()
    {
        String songName = "Double Trouble";
        int songLength = 222;
        String filePath = "/music/doubletrouble.mp3";
        int recordMemberID = 1;

        //first upload should succeed and return a valid generated ID (> 0)
        int firstUploadId = MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);
        assertTrue(firstUploadId > 0, "First upload should succeed");

        //second upload with the same file path should throw an exception
        assertThrows(IllegalArgumentException.class, () -> {
            MusicUpload.musicUpload(songName, songLength, filePath, recordMemberID);
        });
    }
}