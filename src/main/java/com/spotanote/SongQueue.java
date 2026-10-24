package com.spotanote;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.time.Duration;

public class SongQueue {

    // Initialize attributes.
    private ArrayDeque<Song> upcomingSongs = new ArrayDeque<>(); // to be treated as a normal queue (FIFO)
    private ArrayDeque<Song> priorSongs = new ArrayDeque<>(); // to be treated as a stack UNTIL we reach a max size of prior songs to track (). At this point we treat it as a deque, add new previous song to tail end, remove oldest previous song from head.
    private Song currentSong = null; 
    private final int MAX_PREVIOUS_SONGS;

    public SongQueue(){ this.MAX_PREVIOUS_SONGS = 10; }

    public SongQueue(int MAX_PREVIOUS_SONGS){ this.MAX_PREVIOUS_SONGS = MAX_PREVIOUS_SONGS; }

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
     * getCurrentSong -- a method which will return the currentSong in the SongQueue.
     * 
     * @return song object, the currentSong in the SongQueue. If there is no currentSong, the return is null.
     */
    public Song getCurrentSong(){
        return currentSong;
    }

    /**
     * getNext -- a function which will get the next upcoming song from the SongQueue.
     *            Updates the value of currentSong, and moves old currentSong to tail end of priorSongs deque.
     * 
     * @return song object, the head song of upcomingSongs deque. (i.e., next song in queue).
     *                      If no song object is present, it will return null instead.
     */
    public Song getNext(){
        Song nextSong = upcomingSongs.pollFirst(); // can safely assume next song is not null since ArrayDeque does not allow null
        setCurrentSong(nextSong);
        return this.currentSong;

    }

    /**
     * getPrevious -- a function which will get the last played song from the SongQueue.
     *                Updates the value of currentSong, and moves old currentSong to head end of upcomingSongs deque.
     *
     * @return song object, the tail end of priorSongs deque (i.e., the last song played)
     *                      If no song object is present, it will return null instead.
     */
    public Song getPrior(){
        // Get the last played song.
        Song previousSong = priorSongs.pollLast(); // can safely assume next song is not null since ArrayDeque does not allow null
        
        // Get the old current song (one to be replaced), and if it isn't null, add it to head of upcomingSongs deque.
        Song oldCurrentSong = getCurrentSong();
        if (oldCurrentSong != null){
            upcomingSongs.addFirst(oldCurrentSong);
        }

        // Update the currentSong to be the previous song.
        this.currentSong = previousSong;
        return currentSong;
    }

    /**
     * Helper method for retrieving song details from the database by ID.
     * 
     * @param songID an int, the ID of the song to locate in the database
     * @return a {@link Song} object populated with database attributes
     * @throws IllegalArgumentException if no song with the given ID exists
     * @throws RuntimeException if a database connection error occurs
     */
    private Song findSongInDatabaseFromID(int songID){
        Song s = null;

        // Obtain the song from database, create new song object.
        String sqlQuery = "select song_name, song_duration, file_path from Song where id = ?"; // preparing the string this way "sanitizes" user input, preventing an SQL injection attack.
        try (Connection conn = DBExplorer.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlQuery)){ // this attempts to connect to database
            stmt.setInt(1, songID);
            
            // Attempt executing the query once connected to database
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){ // if a song is present in database...
                    // get information about the song...
                    String songName = rs.getString("song_name");
                    int numOfSeconds = rs.getInt("song_duration");
                    String filePath = rs.getString("file_path");

                    // and create song object.
                    s = new Song(songID, songName, Duration.ofSeconds(numOfSeconds), filePath);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching song ID: " + songID, e); // passes database error to driver class to handle as needed.
        }

        // Validate songToQueue is not null. If it is, throw error.
        if (s == null){
            throw new IllegalArgumentException("Song with ID " + songID + " could not be found.");
        }

        return s;
    }

    /**
     * enqueue -- add a song to the tail end of the upcomingSongs deque
     * 
     * @param songToQueue -- a Song, the song which will be added into the song queue (upcomingSongs) at the tail end
     * @throws IllegalArgumentException when songToQueue is null.
     */
    public void enqueue(Song songToQueue){
        // Validate songToQueue is not null. If it is, throw error.
        if (songToQueue == null){
            throw new IllegalArgumentException("Song was not able to be queued due to being null.");
        }

        // Add the song to tail end of upcomingSongs. (Think FIFO)
        upcomingSongs.addLast(songToQueue);
    }

    /**
     * enqueue -- add a song to the tail end of the upcomingSongs deque
     * 
     * @param songIDToQueue -- an int, the id of the song which will be added into the song queue (upcomingSongs) at the tail end
     * @throws IllegalArgumentException if no song with the given ID exists in the database
     * @throws RuntimeException if a database access error occurs
     */
    public void enqueue(int songIDToQueue){
        // Initialize songToQueue object.
        Song songToQueue = findSongInDatabaseFromID(songIDToQueue);

        // Add the song to tail end of upcomingSongs. (Think FIFO)
        enqueue(songToQueue);
    }

    /**
     * Adds a song to the tail end of the priorSongs deque (playback history).
     * If the size of priorSongs exceeds that of the MAX_PRIOR_SONGS,
     * it will remove the song at the head before adding another song to the tail.
     * 
     * @param newPriorSong the {@link Song} to be added to the prior songs history
     * @throws IllegalArgumentException if {@code newPriorSong} is null
     */
    public void addToPriorSongs(Song newPriorSong) {
        // Validate newPriorSong is not null
        if (newPriorSong == null) {
            throw new IllegalArgumentException("Cannot add a null song to playback history.");
        }

        // Check if priorSongs exceeds max size.
        if (priorSongs.size() >= MAX_PREVIOUS_SONGS){
            priorSongs.removeFirst(); // remove head of priorSongs if beyond MAX_PREVIOUS_SONGS
        }

        // Add to the tail end of the history deque
        priorSongs.addLast(newPriorSong);
    }

    /**
     * setCurrentSong -- Updates the value of currentSong. If there is already a currentSong, it moves it to priorSongs deque.
     * 
     * @param newCurrentSongID an int, the id number of the new currentSong value.
     * @throws IllegalArgumentException if no song with the given ID exists in the database.
     */
    public void setCurrentSong(Song newCurrentSong){
        // Validate newCurrentSong isn't null.
        if (newCurrentSong == null){
            throw new IllegalArgumentException("New Current Song can not be null.");
        }

        // Get current song, move it to priorSongs deque, update currentSong with new song.
        Song oldCurrentSong = getCurrentSong();
        
        if (oldCurrentSong != null){ // only add oldCurrentSong to priorSongs deque if its not null.
            addToPriorSongs(oldCurrentSong);
        }

        currentSong = newCurrentSong;
    }

    /**
     * setCurrentSong -- Updates the value of currentSong. If there is already a currentSong, it moves it to priorSongs deque.
     * 
     * @param newCurrentSongID an int, the id number of the new currentSong value.
     * @throws IllegalArgumentException if no song with the given ID exists in the database.
     * @throws RuntimeException if a database acces error occurs.
     */
    public void setCurrentSong(int newCurrentSongID){
        // Initialize newCurrentSong object.
        Song newCurrentSong = findSongInDatabaseFromID(newCurrentSongID);

        // Update the current song.
        setCurrentSong(newCurrentSong);
    }

    /**
     * shuffle -- a method which randomizes the order of songs in the upcomingSongs deque (songs which were played previously do not get shuffled)
     * 
     */
    public void shuffle(){
        // Put contents of upcomingSongs into an ArrayList. Then clear the upcomingSongs deque.
        ArrayList<Song> songs = new ArrayList<>(upcomingSongs);
        upcomingSongs.clear();

        // Randomize order of songs.
        Collections.shuffle(songs);

        // Convert ArrayList back into upcomingSongs deque.
        for (Song song : songs){
            enqueue(song);
        }
    }

    /**
     * empty -- a method which removes all songs from the upcomingSongs deque ONLY. currentSong and priorSongs remain.
     * 
     */
    public void empty(){
        upcomingSongs.clear();
    }

    
    
    /**
     * EQN -- a function to determine if two SongQueue objects are observationally equivalent to each other
     *        these two objects are considered to be observationally equivalent WHEN
     *              1) the upcomingSongs attribute of each SongQueue object has the same songs in the same order AND
     *              2) the priorSongs attribute of each SongQueue object has the same songs in the same order AND
     *              3) the currentSong attribute of each SongQueue object matches as songs move through the queue (a strict enforcement of (1) and (2))
     * 
     * @param sq1 -- the first SongQueue object we are comparing
     * @param sq2 -- the second SongQueue object we are comparing
     * @return a boolean literal -- true if the SongQueue objects are observationally equivalent to each other, false otherwise.
     */
    public static boolean EQN(SongQueue sq1, SongQueue sq2) {
        // First check if both song queues have no upcoming, prior songs, or current songs. This is the base case.
        if (sq1.isPriorSongsEmpty() && sq1.isQueuedSongsEmpty()
            && sq2.isPriorSongsEmpty() && sq2.isQueuedSongsEmpty()
            && Objects.equals(sq1.getCurrentSong(), sq2.getCurrentSong())) { return true; }

        // Instant fail if song queues have different max previous songs attributes.
        if (SongQueue.getMaxPreviousSongs(sq1) != SongQueue.getMaxPreviousSongs(sq2)) { return false; }

        // First check if the priorSongs are roughly equal in state (i.e., one is not full while another is empty.)
        if (sq1.isPriorSongsEmpty() != sq2.isPriorSongsEmpty()){ return false; }

        // Then, check if the upcomingSongs are rougly equal in state (i.e., one is not full while another is empty.)
        if (sq1.isQueuedSongsEmpty() != sq2.isQueuedSongsEmpty()){ return false; }

        // Compare current songs.
        if (!Objects.equals(sq1.getCurrentSong(), sq2.getCurrentSong())) { return false; }

        // Pop last played song from priorSongs if not empty (without invoking full getPrior() side effects)
        if (!sq1.isPriorSongsEmpty()) { // if we get to this point, we know each priorSongs is in same rough observational state.
            Song p1 = popPriorRaw(sq1);
            Song p2 = popPriorRaw(sq2);
            if (!Objects.equals(p1, p2)) {
                return false;
            }
        }

        // Pop from upcomingSongs if not empty (without invoking full getNext() side effects)
        if (!sq1.isQueuedSongsEmpty()) { // if we get to this point, we know each upcomingSongs is in same rough observational state.
            Song n1 = popUpcomingRaw(sq1);
            Song n2 = popUpcomingRaw(sq2);
            if (!Objects.equals(n1, n2)) {
                return false;
            }
        }

        // Recursively call method to evaluate rest of SongQueue.
        return EQN(sq1, sq2);
    }

    // Private helper methods for EQN. Allows for static observation of the SongQueue.
    private static Song popPriorRaw(SongQueue sq) {
        return sq.priorSongs.pollLast();
    }

    private static Song popUpcomingRaw(SongQueue sq) {
        return sq.upcomingSongs.pollFirst();
    }

    private static int getMaxPreviousSongs(SongQueue sq){
        return sq.MAX_PREVIOUS_SONGS;
    }
}