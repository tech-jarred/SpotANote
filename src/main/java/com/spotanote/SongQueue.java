package com.spotanote;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Objects;
import java.time.Duration;

public class SongQueue {

    // Initialize attributes.
    private ArrayDeque<Song> upcomingSongs = new ArrayDeque<>(); // to be treated as a normal queue (FIFO)
    private ArrayDeque<Song> priorSongs = new ArrayDeque<>(); // to be treated as a stack UNTIL we reach a max size of prior songs to track (). At this point we treat it as a deque, add new previous song to tail end, remove oldest previous song from head.
    private final int MAX_PREVIOUS_SONGS = 10;

    public SongQueue(){}

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

    /**
     * queue -- add a song to the tail end of the upcomingSongs deque
     * 
     * @param songToQueue -- a Song, the song which will be added into the song queue (upcomingSongs) at the tail end
     */
    public void queue(Song songToQueue){
        // Attempt to add the song to tail end of upcomingSongs. (Think FIFO)
        try {
            upcomingSongs.addLast(songToQueue);
        } catch (Exception e) {
            System.out.println("Error queing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * queue -- add a song to the tail end of the upcomingSongs deque
     * 
     * @param songIDToQueue -- an int, the id of the song which will be added into the song queue (upcomingSongs) at the tail end
     */
    public void queue(int songIDToQueue){
        // Initialize songToQueue object.
        Song songToQueue = null;

        // Obtain the song from database, create new song object.
        String sqlQuery = "select song_name, song_duration, file_path from Song where id = ?"; // preparing the string this way "sanitizes" user input, preventing an SQL injection attack.
        try (Connection conn = DBExplorer.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlQuery)){ // this attempts to connect to database
            stmt.setInt(1, songIDToQueue);
            
            // Attempt executing the query once connected to database
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){ // if a song is present in database...
                    // get information about the song...
                    String songName = rs.getString("song_name");
                    int numOfSeconds = rs.getInt("song_duration");
                    String filePath = rs.getString("file_path");

                    // and create song object.
                    songToQueue = new Song(songIDToQueue, songName, Duration.ofSeconds(numOfSeconds), filePath);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Attempt to add the song to tail end of upcomingSongs. (Think FIFO)
        try {
            upcomingSongs.addLast(songToQueue);
        } catch (Exception e) {
            System.out.println("Error queing song: " + e.getMessage());
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
            System.out.println("Error moving last played song to prior list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * getNext -- a function which will get the next upcoming song from the SongQueue.
     * 
     * @return song object, the head song of upcomingSongs deque. (i.e., next song in queue).
     *                      If no song object is present, it will return null instead.
     */
    public Song getNext(){
        return upcomingSongs.pollFirst();
    }

    /**
     * getPrevious -- a function which will get the last played song from the SongQueue.
     *
     * @return song object, the tail end of priorSongs deque (i.e., the last song played)
     *                      If no song object is present, it will return null instead.
     */
    public Song getPrevious(){
        return priorSongs.pollLast();
    }

    /**
     * EQN -- a function to determine if two SongQueue objects are observationally equivalent to each other
     *        these two objects are considered to be observationally equivalent WHEN
     *              1) the upcomingSongs attribute of each SongQueue object has the same songs in the same order AND
     *              2) the priorSongs attribute of each SongQueue object has the same songs in the same order
     * 
     * @param sq1 -- the first SongQueue object we are comparing
     * @param sq2 -- the second SongQueue object we are comparing
     * @return a boolean literal -- true if the SongQueue objects are observationally equivalent to each other, false otherwise.
     */
    public static boolean EQN(SongQueue sq1, SongQueue sq2){
        // First check if both song queues have no upcoming or prior songs. This is the base case.
        if (sq1.isPriorSongsEmpty() && sq1.isQueuedSongsEmpty() && sq2.isPriorSongsEmpty() && sq2.isQueuedSongsEmpty()) { return true; }

        // First check if the priorSongs are roughly equal in state (i.e., one is not full while another is empty.)
        if (sq1.isPriorSongsEmpty() != sq2.isPriorSongsEmpty()){ return false; }

        // Then, check if the upcomingSongs are rougly equal in state (i.e., one is not full while another is empty.)
        if (sq1.isQueuedSongsEmpty() != sq2.isQueuedSongsEmpty()){ return false; }

        // If both the priorSongs and upcomingSongs are roughly equal, get last played song and next upcoming song.
        Song priorSong1 = sq1.getPrevious();
        Song priorSong2 = sq2.getPrevious();

        Song nextSong1 = sq1.getNext();
        Song nextSong2 = sq2.getNext();

        // After getting prior song and next song for each respective queue, compare them to see if they are the same.
        boolean priorCheck = Objects.equals(priorSong1, priorSong2); // Objects.equals(a, b) does the following: return (a == b) || (a != null && a.equals(b));
        boolean nextCheck = Objects.equals(nextSong1, nextSong2); // it protects against null pointer exceptions, and calls the .equals() method of the objects passed in.

        if (priorCheck && nextCheck){
            // If the songs are the same, recursively call EQN. Repeat until both SongQueues have no more upcoming or prior songs.
            return EQN(sq1, sq2);
        }

        return false;
    }
}
