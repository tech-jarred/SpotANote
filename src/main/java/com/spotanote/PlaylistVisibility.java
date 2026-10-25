
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * PlaylistVisibility class
 * Focuses on privating and publicizing playlists
 * There will be two methods in this class
 * Both methods will be tested together in one file
 */
public class PlaylistVisibility {

    /**
     * Private Playlist function
     * 
     * @param playlist's id which is the int id associated to a playlist
     * @return no returns, only success/error statements will be sent
     */
    public static void privatePlaylist(int playlistID)
    {
        setVisibility(playlistID, false);
    }

    /**
    * Public Playlist function
    * 
    * @param playlist's id which is the int id associated to a playlist
    * @return no returns, only success/error statements will be sent
    */
    public static void publicPlaylist(int playlistID)
    {
        setVisibility(playlistID, true);
    }

    /**
     * Set Visibility function - helper function for privatePlaylist() and publicPlaylist()
     * 
     *  @param int playlist id as its stored in the database
     *  @param boolean makePublic, true: make the playlist public, false: make the playlist private
     */
    private static void setVisibility(int playlistID, boolean makePublic) {
        String checkSql = "SELECT is_public FROM Playlist WHERE id = ?";
        String updateSql = "UPDATE Playlist SET is_public = ? WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection())
        {
            //check if the playlist is currently private or public
            try (PreparedStatement checkStmt = connect.prepareStatement(checkSql))
            {
                checkStmt.setInt(1, playlistID);
                try (ResultSet rs = checkStmt.executeQuery())
                {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Playlist with ID " + playlistID + " does not exist.");
                    }
                    
                    if (rs.getBoolean("is_public") == makePublic)
                    {
                        if (makePublic) {
                            throw new IllegalStateException("Playlist is already public.");
                        } else {
                            throw new IllegalStateException("Playlist is already private.");
                        }
                    }
                }
            }

            //update database
            try (PreparedStatement updateStmt = connect.prepareStatement(updateSql))
            {
                updateStmt.setBoolean(1, makePublic);
                updateStmt.setInt(2, playlistID);

                if (updateStmt.executeUpdate() > 0)
                {
                    if (makePublic) {
                        System.out.println("Playlist set to public successfully.");
                    } else {
                        System.out.println("Playlist set to private successfully.");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating playlist visibility: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Frontend Helper Method - retrieves all public albums/playlists uploaded by a specific artist/user.
     * 
     * @param userId The ID of the logged-in artist
     * @return List of public Playlist objects
     */
    public static List<Playlist> getArtistUploadedAlbums(int userId) {
        List<Playlist> albums = new ArrayList<>();
        String albumSql = "SELECT id, playlist_name, is_public FROM Playlist WHERE user_id = ? AND is_public = TRUE";
        String songSql = "SELECT s.id, s.song_name, s.song_duration, s.file_path " +
                        "FROM Song s JOIN PlaylistSong ps ON s.id = ps.song_id " +
                        "WHERE ps.playlist_id = ? ORDER BY ps.song_order ASC";

        try (Connection conn = DBExplorer.getConnection();
            PreparedStatement albumStmt = conn.prepareStatement(albumSql)) {

            albumStmt.setInt(1, userId);
            try (ResultSet rs = albumStmt.executeQuery()) {
                while (rs.next()) {
                    Playlist playlist = new Playlist(
                        rs.getInt("id"),
                        rs.getString("playlist_name"),
                        rs.getBoolean("is_public")
                    );

                    // Fetch tracks for this playlist
                    try (PreparedStatement songStmt = conn.prepareStatement(songSql)) {
                        songStmt.setInt(1, playlist.getId());
                        try (ResultSet songRs = songStmt.executeQuery()) {
                            List<Song> songs = new ArrayList<>();
                            while (songRs.next()) {
                                songs.add(new Song(
                                    songRs.getInt("id"),
                                    songRs.getString("song_name"),
                                    java.time.Duration.ofSeconds(songRs.getInt("song_duration")),
                                    songRs.getString("file_path")
                                ));
                            }
                            playlist.setSongs(songs);
                        }
                    }

                    albums.add(playlist);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching artist albums with songs: " + e.getMessage());
            e.printStackTrace();
        }

        return albums;
    }

    /**
     * Frontend Helper Method - retrieves all drafted (private) albums uploaded by a specific artist/user,
     * including both public and draft songs inside each album.
     * 
     * @param userId The ID of the logged-in artist
     * @return List of drafted Playlist objects
     */
    public static List<Playlist> getArtistDraftedAlbums(int userId) {
        List<Playlist> draftAlbums = new ArrayList<>();
        String albumSql = "SELECT id, playlist_name, is_public FROM Playlist WHERE user_id = ? AND is_public = FALSE";
        String songSql = "SELECT s.id, s.song_name, s.song_duration, s.file_path, s.is_public " +
                        "FROM Song s JOIN PlaylistSong ps ON s.id = ps.song_id " +
                        "WHERE ps.playlist_id = ? ORDER BY ps.song_order ASC";

        try (Connection conn = DBExplorer.getConnection();
            PreparedStatement albumStmt = conn.prepareStatement(albumSql)) {

            albumStmt.setInt(1, userId);
            try (ResultSet rs = albumStmt.executeQuery()) {
                while (rs.next()) {
                    Playlist playlist = new Playlist(
                        rs.getInt("id"),
                        rs.getString("playlist_name"),
                        rs.getBoolean("is_public")
                    );

                    // Fetch songs linked to this drafted album
                    try (PreparedStatement songStmt = conn.prepareStatement(songSql)) {
                        songStmt.setInt(1, playlist.getId());
                        try (ResultSet songRs = songStmt.executeQuery()) {
                            List<Song> songs = new ArrayList<>();
                            while (songRs.next()) {
                                Song song = new Song(
                                    songRs.getInt("id"),
                                    songRs.getString("song_name"),
                                    java.time.Duration.ofSeconds(songRs.getInt("song_duration")),
                                    songRs.getString("file_path")
                                );
                                // We can use or track songRs.getBoolean("is_public") here if needed later
                                songs.add(song);
                            }
                            playlist.setSongs(songs);
                        }
                    }

                    draftAlbums.add(playlist);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching drafted albums: " + e.getMessage());
            e.printStackTrace();
        }

        return draftAlbums;
    }
}