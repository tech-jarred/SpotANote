package com.spotanote;
import java.util.ArrayDeque;

public class SongQueue {

    // Initialize attributes.
    private ArrayDeque<Song> queuedSongs = new ArrayDeque<>();
    private ArrayDeque<Song> priorSongs = new ArrayDeque<>();

    public SongQueue(){
    }

    // ... more methods to come

    /**
     * 
     * @return a boolean literal -- true if there are no songs queued to be play next (i.e.  queuedSongs is empty)
     */
    public boolean isQueuedSongsEmpty(){
        return queuedSongs.size() == 0;
    }

    /**
     * 
     * @return a boolean literal -- true if there are no songs had been previously played from queue (i.e. priorSongs is empty)
     */
    public boolean isPriorSongsEmpty(){
        return priorSongs.size() == 0;
    }
}
