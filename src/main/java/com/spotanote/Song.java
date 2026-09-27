package com.spotanote;
import java.time.Duration;

public class Song {
    private final int ID;
    private final String NAME;
    private final Duration DURATION;
    private final String FILE_PATH;

    /**
     * 
     * @param songId            -- the id of the song as found in the database
     * @param songName          -- the name of the song held in database
     * @param songDuration      -- the duration of the song, 
     * @param filePathToSong    -- the pile path to where the song is stored by our application
     */
    public Song(int songId, String songName, Duration songDuration, String filePathToSong){
        this.ID = songId;
        this.NAME = songName;
        this.DURATION = songDuration;
        this.FILE_PATH = filePathToSong;
    }

    public int getId(){
        return this.ID;
    }

    public String getName(){
        return this.NAME;
    }

    public Duration getDuration(){
        return this.DURATION;
    }

    public String getFilePath(){
        return this.FILE_PATH;
    }
}