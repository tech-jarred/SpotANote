package com.spotanote;
import java.time.Duration;

public class Song {
    private final int ID;
    private final String NAME;
    private final Duration DURATION;
    private final String FILE_PATH;
    private Duration timeStamp = Duration.ZERO; // all songs will start at timestamp 0, but can later be paused at a different time stamp.

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

    /**
     * Various getter methods to access the private attributes of the class
     * @return -- the data type/value of each methods respective attribute
     */
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

    public Duration getTimeStamp(){
        return this.timeStamp;
    }
    
    /**
     *  To set the timestamp attribute given an amount of seconds which has been passed through the song
     * 
     * @param secondsPassed -- a positive amount of seconds, which is received from the frontend Javascript. Represents amount of time song has played.
     *                   AND is not to exceed the duration of the song.
     */
    public void setTimeStampFromSeconds(double secondsPassed) {
        if (0 <= secondsPassed && secondsPassed <= this.DURATION.toSeconds()){
            this.timeStamp = Duration.ofMillis((long) (secondsPassed * 1000));
        }
    }
}