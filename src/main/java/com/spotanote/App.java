package com.spotanote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;

public class App {
    public static Javalin createApp() {
        // Initialize needed helper classes.
        LoginManager authy = new LoginManager();

        // Initialize and begin running Javalin server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Tells app to locate static files from src/main/resources/public
            config.fileRenderer(new JavalinPebble()); // Sets up the file renderer to be used

            /**
             * If sent to /, redirect user to login page. Url will always reflect what
             * section user is in.
             * 
             * HOWEVER if there is a current user in the session, they will instead be
             * redirected to home, to avoid having to log in again.
             */
            config.routes.get("/", ctx -> {
                if (ctx.sessionAttribute("currentUser") != null) {
                    ctx.redirect("/home");
                } else {
                    ctx.redirect("/login");
                }
            });

            /**
             * /login route
             * 
             * This route will be the first place any user of our system lands. When
             * entering the system, they must provide their UNIQUE username and password.
             */
            config.routes.get("/login", ctx -> {
                if (ctx.sessionAttribute("currentUser") != null) {
                    ctx.redirect("/home");
                } else {
                    ctx.render("public/LoginPage.html");
                } // makes it so if user is already logged in and loads /login route, they don't
                  // have to log in again.
            });

            config.routes.post("/login", ctx -> {
                // Obtaining information entered by user on the form.
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");

                // Authenticate the user.
                User user = authy.authenticate(username, password);

                if (user != null) {
                    ctx.sessionAttribute("currentUser", user);
                    ctx.sessionAttribute("SongQueue", new SongQueue());
                    password = null;
                    ctx.redirect("/home");
                } else {
                    password = null;
                    ctx.status(401).result("Invalid username or password.");
                }
            });

            /**
             * /home route
             * 
             * This route is the central page for the user. From here they can access every
             * part of the application, and will be presented with the most basic function
             * for their role.
             */
            config.routes.get("/home", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");

                // Verify user is actually logged in (prevents person from typing /home in URL
                // to bypass login page)
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }
                SongQueue sq = ctx.sessionAttribute("SongQueue");
                Song currentSong = sq.getCurrentSong();

                if (currentSong == null && !sq.isQueuedSongsEmpty()) {
                    // If the currentSong is empty and there are songs waaiting in queue, get the first song.
                    sq.getNext();
                    currentSong = sq.getCurrentSong();
                }
                else if (currentSong == null) {
                    currentSong = new Song(0, "Choose A Song To Get Started!", Duration.ZERO, "/music/");
                }

                // Render Home.html file, passing username through for Pebble formatting.
                ctx.render("public/Home.html", java.util.Map.of("username", currentUser.getName(),
                        "current_song_file_path", currentSong.getFilePath(),
                        "timeStamp", currentSong.getTimeStamp().toSeconds(),
                        "songName", currentSong.getName(),
                        "isArtist", currentUser.getRole().equals("Artist")));
            });

            /**
             * /music-library route
             * 
             * This route is the landing page for all music on the platform. Users can
             * select either an individual song OR playlist from this page.
             */
            config.routes.get("/music-library", ctx -> {
                // Verify user is logged in
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                // Render music selection page
                ctx.render("public/music.html", java.util.Map.of("isArtist", currentUser.getRole().equals("Artist")));
            });

            /**
             * /select-song route
             * 
             * This will handle updating the currentSong in a user's session to whatever
             * song they've selected from music screen.
             */
            config.routes.post("/select-song", ctx -> {
                // Verify user is logged in, if not redirect to login page.
                User currentUser = ctx.sessionAttribute("currentUser");
                SongQueue sq = ctx.sessionAttribute("SongQueue");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                // Read songId from song user selected from the music page
                String songIdParam = ctx.formParam("songId");

                if (songIdParam != null) {
                    int songId = Integer.parseInt(songIdParam);

                    // Update the current song in song queue.
                    try {
                        sq.setCurrentSong(songId); 
                    } catch (IllegalArgumentException e) {
                        sq.setCurrentSong(new Song(0, "Choose A Song To Get Started!", Duration.ZERO, "/music/"));
                        ctx.status(400).result("Unrecognized argument");      
                    } catch (RuntimeException e){
                        sq.setCurrentSong(new Song(0, "Choose A Song To Get Started!", Duration.ZERO, "/music/"));
                        ctx.status(500).result("Internal Server Error");
                    }
                    // Store updated song queue in session attribute if valid
                    ctx.sessionAttribute("SongQueue", sq);
                }

                // Redirect back to home route
                ctx.redirect("/home");
            });

            /**
             * /managepublications route
             * 
             * This route will be responsible for managing publications as an artist
             */
            config.routes.get("/managepublications", ctx -> {
                //verify user is logged in
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                // Verify user is an artist. If not, they are not allowed to access this page.
                if (!currentUser.getRole().equals("Artist")){
                    ctx.redirect("/home");
                    return;
                }

                //passing the status parameter to Pebble
                String status = ctx.queryParam("status");
                Map<String, Object> model = new HashMap<>();
                model.put("status", status);

                //fetching public albums created by this artist/user
                List<Playlist> uploadedAlbums = PowerfulPlaylist.getArtistUploadedAlbums(currentUser.getId());
                List<Playlist> draftedAlbums = PowerfulPlaylist.getArtistDraftedAlbums(currentUser.getId());
                model.put("status", ctx.queryParam("status"));

                model.put("uploadedAlbums", uploadedAlbums);
                model.put("draftedAlbums", draftedAlbums);
                model.put("isArtist", currentUser.getRole().equals("Artist"));

                ctx.render("public/managepublications.html", model);
            });

            /**
             * /upload-music route
             * 
             * This will handle uploading whatever song a listener wants to upload
             */
            config.routes.post("/upload-music", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                String songName = ctx.formParam("songName");
                String songLengthStr = ctx.formParam("songLength");
                String filePath = ctx.formParam("filePath");
                String playlistIdStr = ctx.formParam("playlistId");

                if (songLengthStr == null || songLengthStr.trim().isEmpty() ||
                    playlistIdStr == null || playlistIdStr.trim().isEmpty()) {
                    ctx.redirect("/managepublications?status=missing_fields");
                    return;
                }

                try {
                    int songLength = Integer.parseInt(songLengthStr);
                    int playlistId = Integer.parseInt(playlistIdStr);

                    int recordMemberId = MusicUpload.getRecordMemberIdByUserId(currentUser.getId());

                    if (recordMemberId <= 0) {
                        ctx.redirect("/managepublications?status=not_a_record_member");
                        return;
                    }

                    // 1. Call your original musicUpload method unchanged
                    int songId = MusicUpload.musicUpload(songName, songLength, filePath, recordMemberId);

                    if (songId > 0) {
                        // 2. Link the new song to the chosen playlist/album in PlaylistSong
                        String getMaxOrderSql = "SELECT COALESCE(MAX(song_order), 0) + 1 FROM PlaylistSong WHERE playlist_id = ?";
                        String insertJunctionSql = "INSERT INTO PlaylistSong (playlist_id, song_id, song_order) VALUES (?, ?, ?)";

                        try (Connection conn = DBExplorer.getConnection()) {
                            int nextOrder = 1;
                            try (PreparedStatement orderStmt = conn.prepareStatement(getMaxOrderSql)) {
                                orderStmt.setInt(1, playlistId);
                                try (ResultSet rs = orderStmt.executeQuery()) {
                                    if (rs.next()) {
                                        nextOrder = rs.getInt(1);
                                    }
                                }
                            }

                            try (PreparedStatement junctionStmt = conn.prepareStatement(insertJunctionSql)) {
                                junctionStmt.setInt(1, playlistId);
                                junctionStmt.setInt(2, songId);
                                junctionStmt.setInt(3, nextOrder);
                                junctionStmt.executeUpdate();
                            }
                        }

                        ctx.redirect("/managepublications?status=success");
                    } else {
                        ctx.redirect("/managepublications?status=failed");
                    }

                } catch (NumberFormatException e) {
                    ctx.redirect("/managepublications?status=invalid_length");
                } catch (IllegalArgumentException e) {
                    ctx.redirect("/managepublications?status=error");
                } catch (SQLException e) {
                    System.err.println("Error linking song to playlist: " + e.getMessage());
                    ctx.redirect("/managepublications?status=error");
                }
            });

            /**
             * /publish-album route
             * 
             * Publishes a drafted playlist by setting its visibility to true
             */
            config.routes.post("/publish-album", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                String playlistIdStr = ctx.formParam("playlistId");
                if (playlistIdStr != null) {
                    try {
                        int playlistId = Integer.parseInt(playlistIdStr);
                        
                        // Using your publicPlaylist function
                        PlaylistVisibility.publicPlaylist(playlistId);

                        ctx.redirect("/managepublications?status=album_published");
                    } catch (NumberFormatException e) {
                        ctx.redirect("/managepublications?status=error");
                    }
                } else {
                    ctx.redirect("/managepublications");
                }
            });

            /**
             * /delete-song route
             * 
             * Permanently deletes a song from the database
             */
            config.routes.post("/delete-song", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                String songIdStr = ctx.formParam("songId");

                if (songIdStr != null) {
                    try {
                        int songId = Integer.parseInt(songIdStr);

                        SongDelete.deleteSong(songId);

                        ctx.redirect("/managepublications?status=song_deleted");
                    } catch (NumberFormatException e) {
                        ctx.redirect("/managepublications?status=error");
                    }
                } else {
                    ctx.redirect("/managepublications");
                }
            });

            /**
             * /create-playlist route
             * 
             * Handles creating a new draft playlist/album for the logged-in user
             */
            config.routes.post("/create-playlist", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                String playlistName = ctx.formParam("playlistName");

                if (playlistName == null || playlistName.trim().isEmpty()) {
                    ctx.redirect("/managepublications?status=invalid_playlist_name");
                    return;
                }

                try {
                    // Always creates as draft
                    Playlist newPlaylist = PowerfulPlaylist.createPlaylist(currentUser.getId(), playlistName);

                    if (newPlaylist != null) {
                        ctx.redirect("/managepublications?status=playlist_created");
                    } else {
                        ctx.redirect("/managepublications?status=failed");
                    }
                } catch (IllegalArgumentException e) {
                    ctx.redirect("/managepublications?status=error");
                }
            });

            /**
             * /rename-album route
             * 
             * Updates the title of a draft album
             */
            // Route 1: Rename Draft Album
            config.routes.post("/rename-album", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                String playlistIdStr = ctx.formParam("playlistId");
                String newTitle = ctx.formParam("newTitle");

                if (playlistIdStr != null && newTitle != null && !newTitle.trim().isEmpty()) {
                    try {
                        int playlistId = Integer.parseInt(playlistIdStr);
                        
                        // Delegate database update to PowerfulPlaylist
                        boolean success = PowerfulPlaylist.renamePlaylist(playlistId, newTitle);

                        if (success) {
                            ctx.redirect("/managepublications?status=album_renamed");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                
                ctx.redirect("/managepublications?status=rename_failed");
            });

            // Route 2: Delete Draft Album
            config.routes.post("/delete-album", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                String playlistIdStr = ctx.formParam("playlistId");

                if (playlistIdStr != null) {
                    try (Connection conn = DBExplorer.getConnection()) {
                        int playlistId = Integer.parseInt(playlistIdStr);

                        // 1. Unlink song mappings in PlaylistSong table first
                        try (PreparedStatement stmtJunction = conn.prepareStatement(
                                "DELETE FROM PlaylistSong WHERE playlist_id = ?")) {
                            stmtJunction.setInt(1, playlistId);
                            stmtJunction.executeUpdate();
                        }

                        // 2. Delete the draft playlist
                        try (PreparedStatement stmtPlaylist = conn.prepareStatement(
                                "DELETE FROM Playlist WHERE id = ? AND is_public = FALSE")) {
                            stmtPlaylist.setInt(1, playlistId);
                            int rowsDeleted = stmtPlaylist.executeUpdate();

                            if (rowsDeleted > 0) {
                                ctx.redirect("/managepublications?status=album_deleted");
                                return;
                            }
                        }
                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                ctx.redirect("/managepublications?status=delete_failed");
            });

            /**
             * /search route
             * This route will be responsible for searching
             */
            config.routes.get("/search", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }
                ctx.render("public/search.html", Map.of(
                    "isArtist", currentUser.getRole().equals("Artist"),
                    "searchQuery", ctx.queryParam("q") != null ? ctx.queryParam("q") : ""
                ));
            });

            /**
             * /logout route
             * 
             * This route will be responsible for clearing session variables, and
             * redirecting user to login page.
             */
            config.routes.post("/logout", ctx -> {
                ctx.req().getSession().invalidate();
                ctx.redirect("/login");
            });

            /**
             * The below routes will not render any pages on the front end, and are solely
             * responsible for processing data
             * sent from the front end.
             */

            /**
             * /api/song/timeStamp
             * 
             * This route will be responsible for updating the timeStamp of the current song
             * being played, allowing user
             * to pause, and navigate through the site, and return to /home and continue
             * listening from where they left off.
             */
            config.routes.post("/api/song/timeStamp", ctx -> {
                // Initialize needed values for this route.
                SongQueue sq = ctx.sessionAttribute("SongQueue");
                String timeStampInSeconds = ctx.formParam("seconds");

                // As long as there is a currentSong and a timeStamp from the forum, update the
                // time stamp of the current song.
                if (sq.getCurrentSong() != null && timeStampInSeconds != null) {
                    double seconds = Double.parseDouble(timeStampInSeconds);
                    sq.updateCurrentSongTimeStamp(seconds);//currentSong.setTimeStampFromSeconds(seconds);
                    ctx.sessionAttribute("SongQueue", sq);
                    ctx.status(200); // tells the client the server successfully processed request.
                } else {
                    ctx.status(500); // tells frontend the request came through but wasn't able to finished to due an
                                     // internal service error.
                }

            });

            /**
             * /api/liked-songs
             * 
             * This route handles obtaining all liked songs a user has. It returns the result to the frontend
             * as a JSON.
             */
            config.routes.get("/api/liked-songs", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                MusicLike musicLike = new MusicLike();

                // Only allow users who have logged in access to this api.
                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }

                // Call DAO method using current user's ID
                List<SongSummary> likedSongs = null;
                try {
                    likedSongs = musicLike.getUsersLikedSongs(currentUser.getId());
                } catch (Exception e) {
                    ctx.status(500); // internal server error status code if exception thrown.
                    return;
                }
                
                // Automatically serializes List<SongSummary> into JSON text
                ctx.json(likedSongs);
            });

            /**
             * /api/user-playlists
             * 
             * Obtain information to display about a user's playlists.
             */
            config.routes.get("/api/user-playlists", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }

                try {
                    List<PlaylistSummary> userPlaylists = PowerfulPlaylist.getUserPlaylists(currentUser.getId());
                    ctx.json(userPlaylists);
                } catch (Exception e) {
                    ctx.status(500).result("Error fetching user playlists");
                }
            });

            /**
             * /api/like/song
             * 
             * Handles a user liking a song.
             */
            config.routes.post("/api/like/song", ctx -> {
                MusicLike musicLike = new MusicLike();
                User currentUser = ctx.sessionAttribute("currentUser");

                // Only allow auhtenticated users access to this api.
                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }

                int songId = Integer.parseInt(ctx.queryParam("songId"));
                try {
                    musicLike.likeSong(currentUser.getId(), songId);
                } catch (Exception e) {
                    ctx.status(500); // status internal server error if exception thrown.
                    return;
                }

                ctx.status(200).result("Success");
            });

            /**
             * /api/dislike/song
             * 
             * Responsible for handling user disliking song.
             */
            config.routes.post("/api/dislike/song", ctx -> {
                MusicLike musicLike = new MusicLike();
                User currentUser = ctx.sessionAttribute("currentUser");
                
                // Only allow authenticated users access to this route.
                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }

                int songId = Integer.parseInt(ctx.queryParam("songId"));

                try {
                    musicLike.dislikeSong(currentUser.getId(), songId);                
                } catch (Exception e) {
                    ctx.status(500); // internal server error status if exception thrown.
                    return;
                }
                
                ctx.status(200).result("Success");
            });

            /**
             * /api/add/song/personal/playlist
             * 
             * This route is responsible for adding a song to a user's playlist.
             */
            config.routes.post("/api/add/song/personal/playlist", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized"); // only allow authenticated users access to this route.
                    return;
                }

                try {
                    int songId = Integer.parseInt(ctx.queryParam("songId"));
                    int playlistId = Integer.parseInt(ctx.queryParam("playlistId"));

                    // Attempt to create the playlist in the database.
                    PowerfulPlaylist.addSongToPersonalPlaylist(playlistId, songId);
                    ctx.status(200).result("Song added to playlist");
                } catch (Exception e) {
                    ctx.status(500).result("Error adding song to playlist: " + e.getMessage());
                }
            });

            /**
             * /api/create/personal/playlist
             * 
             * This route is responsible for handling requests to create a new playlist for listeners to add songs to.
             */
            config.routes.post("/api/create/personal/playlist", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }

                try {
                    String playlistName = ctx.formParam("playlistName");
                    int songId = Integer.parseInt(ctx.formParam("songId"));

                    // Attempt to create the playlist in the database.
                    PowerfulPlaylist.createPersonalPlaylistWithSong(currentUser.getId(), playlistName, songId);
                    
                    ctx.status(200).result("Playlist created successfully");
                } catch (Exception e) {
                    ctx.status(500).result("Error creating playlist: " + e.getMessage());
                }
            });

            /**
             * /api/queue/add
             * 
             * This is responsible for adding a song to the song queue.
             */
            config.routes.post("/api/queue/add", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                SongQueue sq = ctx.sessionAttribute("SongQueue");

                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized"); // only allow authenticated users to access this route.
                    return;
                }

                try {
                    int songId = Integer.parseInt(ctx.queryParam("songId"));

                    sq.enqueue(songId); // add song to the queue.
                    ctx.sessionAttribute("SongQueue", sq); // update the value of session's song queue.
                    ctx.status(200).result("Song added to queue");
                } catch (IllegalArgumentException e) {
                    ctx.status(400).result("Invalid song ID");
                } catch (RuntimeException e) {
                    ctx.status(500).result("Internal Server Error");
                }
            });

            /**
             * /api/queue/skip
             * 
             * This route handles requests to move to the next song in a queue.
             * In context, this could be either a song finishing or the user wanting to jump ahead.
             */
            config.routes.post("/api/queue/skip", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                SongQueue sq = ctx.sessionAttribute("SongQueue");

                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized"); // only allow authenticated users access to this route.
                    return;
                }

                try {
                    sq.getNext();
                    ctx.sessionAttribute("SongQueue", sq); // make sure to update session state of the song queue.
                    ctx.status(200).result("Moved to next song");
                } catch (Exception e) {
                    ctx.status(500).result("Error moving forward track: " + e.getMessage());
                }
            });

            /**
             * /api/queue/back
             * 
             * This route handles requests to move back a song in the queue.
             */
            config.routes.post("/api/queue/back", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                SongQueue sq = ctx.sessionAttribute("SongQueue");

                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized"); // only allow authenticated users access to this route.
                    return;
                }

                try {
                    sq.getPrior();
                    ctx.sessionAttribute("SongQueue", sq);
                    ctx.status(200).result("Returned to previous song"); // make sure to update session state of the song queue.
                } catch (Exception e) {
                    ctx.status(500).result("Error moving to previous track: " + e.getMessage());
                }
            });

            /**
             * /api/queue/shuffle
             * 
             * This route handles requests to shuffle the upcoming songs in a queue.
             */
            config.routes.post("/api/queue/shuffle", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                SongQueue sq = ctx.sessionAttribute("SongQueue");

                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized"); // only allow authenticated users access to this route.
                    return;
                }

                try {
                    sq.shuffle();
                    ctx.sessionAttribute("SongQueue", sq); // make sure to update session state of the song queue.
                    ctx.status(200).result("Queue shuffled");
                } catch (Exception e) {
                    ctx.status(500).result("Error shuffling queue: " + e.getMessage());
                }
            });

            /**
             * /api/queue/clear
             * 
             * This route is responsible for clearing all upcoming songs from the queue.
             */
            config.routes.post("/api/queue/clear", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                SongQueue sq = ctx.sessionAttribute("SongQueue");

                if (currentUser == null) {
                    ctx.status(401).result("Unauthorized"); // only allow authenticated users access to this route.
                    return;
                }

                // Empty queue, and then update session state.
                sq.empty();
                ctx.sessionAttribute("SongQueue", sq);

                // Give good status message.
                ctx.status(200);
            });

            /**
             * /api/search/artists
             * Searches for Artists using ArtistRecordSearch.java
             */
            config.routes.get("/api/search/artists", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.status(401).json(List.of());
                    return;
                }

                String query = ctx.queryParam("q");
                if (query == null || query.trim().isEmpty()) {
                    ctx.json(List.of());
                    return;
                }

                List<Map<String, Object>> artists = new ArrayList<>();

                try {
                    List<Integer> artistIds = ArtistRecordSearch.discoverySearch(query.trim(), "User");

                    for (Integer id : artistIds) {
                        String name = ArtistRecordSearch.artistLookup(id);
                        if (name != null) {
                            Map<String, Object> artist = new HashMap<>();
                            artist.put("artistId", id);
                            artist.put("artistName", name);
                            artists.add(artist);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    ctx.status(500).result("Database Error");
                    return;
                }

                ctx.json(artists);
            });

            /**
             * /api/search/albums
             * Searches for public Playlists (Albums) using MusicSearch.java
             */
            config.routes.get("/api/search/albums", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.status(401).json(List.of());
                    return;
                }

                String query = ctx.queryParam("q");
                if (query == null || query.trim().isEmpty()) {
                    ctx.json(List.of());
                    return;
                }

                List<Map<String, Object>> albums = new ArrayList<>();

                try {
                    List<Integer> playlistIds = MusicSearch.musicSearch(query.trim(), "Playlist");

                    for (Integer id : playlistIds) {
                        String playlistName = MusicSearch.playlistLookup(id);
                        if (playlistName != null) {
                            Map<String, Object> album = new HashMap<>();
                            album.put("albumId", id);
                            album.put("albumName", playlistName);
                            albums.add(album);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    ctx.status(500).result("Database Error");
                    return;
                }

                ctx.json(albums);
            });

            //This route will be responsible for displaying all promotions to the user.
            //The promotions will be displayed in a list format, and the user can click on a promotion to view more details about it.
            config.routes.get("/promotions", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");

                // Redirect to /login if user is not logged in.
                if (currentUser == null){ ctx.redirect("/login"); }

                //grabbing the list of all promotions from the CreatePromotion class
                CreatePromotion[] promotionArray = CreatePromotion.getAllPromotions();

                if (promotionArray == null) {
                    ctx.status(500).result("Failed to retrieve promotions");
                    return;
                }
                //and filtering out any null values.
                promotionArray = java.util.Arrays.stream(promotionArray)
                        .filter(java.util.Objects::nonNull)
                        .toArray(CreatePromotion[]::new);
                //then rendering the promotions.html page and passing the list of promotions to it.
                ctx.render("public/promotions.html", Map.of("promotions", promotionArray, "isArtist", currentUser.getRole().equals("Artist")));
            });


            //This route will be responsible for displaying a specific promotion to the user.
            //once the user clicks on a promotion from the list, they will be redirected to this route, which will display the details of the promotion.
            config.routes.get("/specific-promotion/{id}", ctx -> {
                int id = Integer.parseInt(ctx.pathParam("id"));

                //grabbing the promotion details from the CreatePromotion class using the promotion ID.
                String name = CreatePromotion.getPromotionName(id);
                String description = CreatePromotion.getPromotionDescription(id);
                String song = CreatePromotion.getPromotionSong(id);
                User currentUser = ctx.sessionAttribute("currentUser");
                int likeAmount = CreatePromotion.getLikeAmount(id);
                Boolean hasLiked = CreatePromotion.isPromotionLikedByUser(id, currentUser.getId());
                //making sure that the promotion is not null, if not, return a 404 error.
                if (name == null || description == null || song == null) {
                    ctx.status(404).result("Promotion not found");
                    return;
                }

                if(hasLiked == true)
                {
                    ctx.render("public/SpecificPromotion.html", Map.of(
                        "promotionName", name,
                        "promotionDescription", description,
                        "promotionSong", song,
                        "promotion_id", id,
                        "userId", currentUser.getId(),
                        "hasLiked", "Unlike",
                        "likeAmount", likeAmount,
                        "isArtist", currentUser.getRole().equals("Artist")));
                }
                else
                {
                    ctx.render("public/SpecificPromotion.html", Map.of(
                        "promotionName", name,
                        "promotionDescription", description,
                        "promotionSong", song,
                        "promotion_id", id,
                        "userId", currentUser.getId(),
                        "hasLiked", "Like",
                        "likeAmount", likeAmount,
                        "isArtist", currentUser.getRole().equals("Artist")));
                }
            });

            config.routes.post("/likePromotion", ctx -> {
                // Get the promotion ID from the form parameter
                int promotionId = Integer.parseInt(ctx.formParam("promotionId"));
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }
                int userId = currentUser.getId();

                // Call the likePromotion method
                CreatePromotion.likePromotion(promotionId, userId);

                // Redirect back to the specific promotion page
                ctx.redirect("/specific-promotion/" + promotionId);
            });

            config.routes.get("/records", ctx ->
            {
                //shows all records from the database
                RecordInfo[] recordArray = RecordInfo.getAllRecords();
                if (recordArray == null) {
                    ctx.status(500).result("Failed to retrieve promotions");
                    return;
                }
                recordArray = java.util.Arrays.stream(recordArray)
                        .filter(java.util.Objects::nonNull)
                        .toArray(RecordInfo[]::new);
                //then rendering the promotions.html page and passing the list of promotions to it.
                ctx.render("public/Records.html", Map.of("records", recordArray));

            });

            config.routes.get("/specific-record/{id}", ctx ->
            {
                User currentUser = ctx.sessionAttribute("currentUser");
                int id = Integer.parseInt(ctx.pathParam("id"));
                String name = RecordInfo.getRecordName(id);
                int managerId = RecordInfo.getRecordManagerId(id);

                if(name == null || managerId <= 0)
                {
                    ctx.status(404).result("Record not found");
                    return;
                }
                boolean isFollowed = RecordInfo.isRecordFollowedByUser(id, currentUser.getId());

                if(isFollowed == true)
                {
                    ctx.render("public/SpecificRecord.html", 
                    Map.of("recordName", name,
                           "managerId", managerId,
                           "recordId", id,
                           "isUserFollowing", "Unfollow")
                    );
                }
                else
                {
                    ctx.render("public/SpecificRecord.html", 
                    Map.of("recordName", name,
                           "managerId", managerId,
                           "recordId", id,
                           "isUserFollowing", "Follow")
                    );
                }     
            });

            config.routes.post("/followRecord", ctx ->
            {
                int recordId = Integer.parseInt(ctx.formParam("recordId"));
                User currentUser = ctx.sessionAttribute("currentUser");
                int userId = currentUser.getId();
                boolean isFollowing = RecordInfo.isRecordFollowedByUser(recordId, userId);
                if(recordId <= 0)
                {
                    ctx.redirect("/records");
                    return;
                }
                if(isFollowing == true)
                {
                    RecordInfo.unfollowRecord(userId, recordId);
                }
                else
                {
                    RecordInfo.followRecord(userId, recordId);
                }
                ctx.redirect("/specific-record/" + recordId);
            });

            config.routes.get("/profile", ctx->
            {
                User currentUser = ctx.sessionAttribute("currentUser");
                int userId = currentUser.getId();
                String username = currentUser.getUsername();
                String role = currentUser.getRole();
                RecordInfo[] recordArray = RecordInfo.allFollowedRecords(userId);
                CreatePromotion[] promotionArray = CreatePromotion.getAllLikedPromotions(userId);
                promotionArray = java.util.Arrays.stream(promotionArray)
                        .filter(java.util.Objects::nonNull)
                        .toArray(CreatePromotion[]::new);
                        
                recordArray = java.util.Arrays.stream(recordArray)
                        .filter(java.util.Objects::nonNull)
                        .toArray(RecordInfo[]::new);
                
                ctx.render("public/Profile.html", 
                    Map.of("recordArray", recordArray,
                           "promotionArray", promotionArray,
                           "username", username,
                           "userRole", role)
                    );
            });





        });


        return app;

    }

    public static void main(String[] args) {
        // Create Javalin instance.
        Javalin app = createApp();
        int port = 8080;

        // Start the application.
        app.start(port); // use port 8080 if something is running on port 7000

        // Print location of where server is running.
        System.out.println("Server running at http://localhost:" + port + '/');
    }
}
