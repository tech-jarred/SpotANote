package com.spotanote;
import java.util.ArrayDeque;

public class SongQueue {

    // Initialize attributes.
    private ArrayDeque<Song> upcomingSongs = new ArrayDeque<>(); // to be treated as a normal queue (FIFO)
    private ArrayDeque<Song> priorSongs = new ArrayDeque<>(); // to be treated as a stack UNTIL we reach a max size of prior songs to track (). At this point we treat it as a deque, add new previous song to tail end, remove oldest previous song from head.
    private final int MAX_PREVIOUS_SONGS = 10;

    public SongQueue(){
    }

    // ... more methods to come

    /**
     * queue -- add a song to the tail end of the upcomingSongs deque
     * 
     * @param songToQueue -- the song which will be added into the song queue (upcomingSongs) at the tail end
     */
    public void queue(Song songToQueue){
        // Attempt to add the song to tail end of upcomingSongs. (Think FIFO)
        try {
            upcomingSongs.addLast(songToQueue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * addToPriorSongs -- adds a song to the tail end of the priorSongs deque
     * 
     * @param newPriorSong -- the song which we want to add to the tail end of priorSongs.
     */
    public void addToPriorSongs(Song newPriorSong){
        try {
            priorSongs.addLast(newPriorSong);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return a boolean literal -- true if there are no songs queued to be play next (i.e.  upcomingSongs is empty)
     */
    public boolean isQueuedSongsEmpty(){
        return upcomingSongs.size() == 0;
    }

    /**
     * 
     * @return a boolean literal -- true if there are no songs had been previously played from queue (i.e. priorSongs is empty)
     */
    public boolean isPriorSongsEmpty(){
        return priorSongs.size() == 0;
    }
}
