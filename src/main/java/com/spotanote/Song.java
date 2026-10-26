package com.spotanote;
import java.time.Duration;
import java.util.Objects;

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

    /**
     * To format the time duration
     * 
     * @return String formatted as "0:00"
     */
    public String getFormattedDuration() {
        if (this.DURATION == null) return "0:00";
        long minutes = this.DURATION.toMinutes();
        long seconds = this.DURATION.minusMinutes(minutes).getSeconds();
        return String.format("%d:%02d", minutes, seconds);
     * To determine if a different song object is equivalent to this one.
     * 
     * @param obj -- an object we are going to compare this one against to see if they are the same.
     */
    @Override
    public boolean equals(Object obj){
        // check to see if object being compared to is stored at same location in memory
        if (this == obj) return true;

        // if not, see if obj is null or of a different class
        if (obj == null || this.getClass() != obj.getClass()) return false;

        // If neither if clause evalutates to true, we know obj is a different instance of the Song object. Cast obj to Song and compare.
        Song other = (Song) obj;
        return this.ID == other.getId() && this.NAME.equals(other.getName()) && this.DURATION.equals(other.getDuration()) && this.FILE_PATH.equals(other.getFilePath());
    }

    /**
     * Updating the hashing method since we overrode the .equals() method.
     * 
     * @return an integer corresponding to the new hashcode, based on the attributes of a Song object.
     */
    @Override
    public int hashCode(){
        return Objects.hash(this.ID, this.NAME, this.DURATION, this.FILE_PATH);
    }
}