
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * PowerfulPlaylist class
 * Contains additional playlist methods that were needed
 * for the frontend but where not explicitly developed
 */
public class PowerfulPlaylist {
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
        String albumSql = "SELECT id, playlist_name, is_public FROM Playlist WHERE user_id = ? AND is_public = FALSE AND is_personal = FALSE";
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

    /**
     * Creates a new playlist or album in the database defaulting to private/draft.
     * 
     * @param userId       The user ID of the playlist creator/artist
     * @param playlistName The title/name of the playlist
     * @return The newly created Playlist object, or null if creation failed
     */
    public static Playlist createPlaylist(int userId, String playlistName) {
        if (playlistName == null || playlistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be null or empty.");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        // is_public always defaults to FALSE (draft/private)
        String sql = "INSERT INTO Playlist (user_id, playlist_name, is_public) VALUES (?, ?, FALSE)";

        try (Connection conn = DBExplorer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, playlistName.trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Playlist created successfully with ID: " + generatedId);
                        return new Playlist(generatedId, playlistName.trim(), false);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating playlist: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Renames a playlist/album.
     * 
     * @param playlistId The ID of the playlist
     * @param newName The new title for the playlist
     * @return true if updated successfully
     */
    public static boolean renamePlaylist(int playlistId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }
    
        // Match the column name 'playlist_name' used across PowerfulPlaylist
        String sql = "UPDATE Playlist SET playlist_name = ? WHERE id = ?";
                
        try (Connection conn = DBExplorer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newName.trim());
            stmt.setInt(2, playlistId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error renaming playlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 
     * @param userId an int, the id of the user who's playlists we want to obtain.
     * @throws RuntimeException in the event of a database connection failure.
     * @return List of PlaylistsSummary records, all playlists (and associated songs) a user has
     */
    public static List<PlaylistSummary> getUserPlaylists(int userId) {
        String query = """
            SELECT p.id AS playlist_id, p.playlist_name, s.id AS song_id, s.song_name
            FROM Playlist p
            LEFT JOIN PlaylistSong ps ON p.id = ps.playlist_id
            LEFT JOIN Song s ON ps.song_id = s.id
            WHERE p.user_id = ? AND p.is_personal = TRUE
            ORDER BY p.id, ps.song_order ASC
        """;

        // Use LinkedHashMap to preserve query sort order for playlists
        Map<Integer, String> playlistNames = new LinkedHashMap<>();
        Map<Integer, List<SongSummary>> playlistSongs = new HashMap<>();

        try (Connection conn = DBExplorer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int playlistId = rs.getInt("playlist_id");
                    String playlistName = rs.getString("playlist_name");

                    playlistNames.putIfAbsent(playlistId, playlistName);
                    playlistSongs.putIfAbsent(playlistId, new ArrayList<>());

                    int songId = rs.getInt("song_id");
                    // Check if the LEFT JOIN produced a valid song (playlist is not empty)
                    if (!rs.wasNull()) {
                        String songName = rs.getString("song_name");
                        playlistSongs.get(playlistId).add(new SongSummary(songId, songName));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user playlists for userId: " + userId, e);
        }

        // Build result DTO list
        List<PlaylistSummary> result = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : playlistNames.entrySet()) {
            int pId = entry.getKey();
            result.add(new PlaylistSummary(pId, entry.getValue(), playlistSongs.get(pId)));
        }

        return result;
    }

    /**
     * Will add a song to a person playlist based on the given playlistId and songId.
     * 
     * @param playlistId
     * @param songId
     * @throws IllegalArgumentExeception when either the playlist or song id can not be found in the database.
     * @throws RuntimeException when the connection to the database fails.
     */
    public static void addSongToPersonalPlaylist(int playlistId, int songId){
        String insertSongSql = "INSERT INTO PlaylistSong (playlist_id, song_id, song_order) SELECT ?, ?, COALESCE(MAX(song_order), 0) + 1 FROM PlaylistSong WHERE playlist_id = ?";

            // Insert the song into PlaylistSong
            try (Connection conn = DBExplorer.getConnection(); PreparedStatement stmt = conn.prepareStatement(insertSongSql)) {
                stmt.setInt(1, playlistId);
                stmt.setInt(2, songId);
                stmt.setInt(3, playlistId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Database connection or execution failed while adding song to playlist", e);
            }
    }

    /**
     * Creates a personal playlist and immediately adds its initial song in a single transaction.
     * 
     * @param userId the id of the user creating the playlist
     * @param playlistName the name of the new playlist
     * @param songId the id of the initial song to add
     * @throws IllegalArgumentException if the song does not exist
     * @throws RuntimeException if the database connection or execution fails
     */
    public static void createPersonalPlaylistWithSong(int userId, String playlistName, int songId) {
        String insertPlaylistSql = "INSERT INTO Playlist (user_id, playlist_name, is_public, is_personal) VALUES (?, ?, FALSE, TRUE)";
        String insertSongSql = "INSERT INTO PlaylistSong (playlist_id, song_id, song_order) VALUES (?, ?, 1)";

            // Single try-with-resources managing both PreparedStatements
        try (Connection conn = DBExplorer.getConnection();
             PreparedStatement stmtPlaylist = conn.prepareStatement(insertPlaylistSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtSong = conn.prepareStatement(insertSongSql)) {

            // Insert new Playlist
            stmtPlaylist.setInt(1, userId);
            stmtPlaylist.setString(2, playlistName);
            stmtPlaylist.executeUpdate();

            // Retrieve generated playlist ID
            ResultSet rs = stmtPlaylist.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Creating playlist failed, no ID returned.");
            }

            int newPlaylistId = rs.getInt(1);

            // Insert initial song as song_order = 1
            stmtSong.setInt(1, newPlaylistId);
            stmtSong.setInt(2, songId);
            stmtSong.executeUpdate();

        } catch (SQLException e){
            if (e.getErrorCode() == 1452){ throw new IllegalArgumentException("Error in creating playlist: either invalid playlist id or song id."); }
            throw new RuntimeException("Error occured in connecting to the database.");
        }
    }
}