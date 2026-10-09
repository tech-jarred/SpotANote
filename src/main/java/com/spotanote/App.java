package com.spotanote;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;
import java.time.Duration;
import java.util.Map;

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
                Song currentSong = ctx.sessionAttribute("currentSong");
                if (currentSong == null) { // have a fall back default song/sound in case there is no current song in
                                           // session.
                    currentSong = new Song(3, "Hips Don't Lie", Duration.ofSeconds(218), "/music/hips_dont_lie.mp3");
                    currentSong.setTimeStampFromSeconds(60);
                    ctx.sessionAttribute("currentSong", currentSong);
                }

                // Verify user is actually logged in (prevents person from typing /home in URL
                // to bypass login page)
                if (currentUser == null) {
                    ctx.redirect("/login");
                }

                else {
                    // Render Home.html file, passing username through for Pebble formatting.
                    ctx.render("public/Home.html", java.util.Map.of("username", currentUser.getUsername(),
                            "current_song_file_path", currentSong.getFilePath(),
                            "timeStamp", currentSong.getTimeStamp().toSeconds(),
                            "songName", currentSong.getName()));
                }
            });

            /**
             * /music
             * 
             * This route is the landing page for all music on the platform. Users can
             * select either an individual song OR playlist from this page.
             */
            config.routes.get("/music", ctx -> {
                // Verify user is logged in
                User currentUser = ctx.sessionAttribute("currentUser");
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                // Render music selection page
                ctx.render("public/music.html");
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
                if (currentUser == null) {
                    ctx.redirect("/login");
                    return;
                }

                // Read songId from song user selected from the msuic page
                String songIdParam = ctx.formParam("songId");

                if (songIdParam != null) {
                    int songId = Integer.parseInt(songIdParam);
                    Song selectedSong = null;

                    // Map the ID to the appropriate Song instance
                    if (songId == 1) {
                        selectedSong = new Song(1, "Please Please Please", Duration.ofSeconds(187),
                                "/music/please_please_please.mp3");
                    } else if (songId == 2) {
                        selectedSong = new Song(2, "Revenge", Duration.ofSeconds(219), "/music/revenge.mp3");
                    } else if (songId == 3) {
                        selectedSong = new Song(3, "Bailando", Duration.ofSeconds(254), "/music/bailando.mp3");
                    } else if (songId == 4) {
                        selectedSong = new Song(4, "Danza Kuduro", Duration.ofSeconds(235), "/music/danza_kuduro.mp3");
                    } else if (songId == 5) {
                        selectedSong = new Song(5, "Don't Mine At Night", Duration.ofSeconds(214),
                                "/music/dont_mine_at_night.mp3");
                    } else if (songId == 6) {
                        selectedSong = new Song(6, "Drive By", Duration.ofSeconds(197), "/music/drive_by.mp3");
                    } else if (songId == 7) {
                        selectedSong = new Song(7, "Fallen Kingdom", Duration.ofSeconds(252),
                                "/music/fallen_kingdom.mp3");
                    } else if (songId == 8) {
                        selectedSong = new Song(8, "Hips Don't Lie", Duration.ofSeconds(218),
                                "/music/hips_dont_lie.mp3");
                    } else if (songId == 9) {
                        selectedSong = new Song(9, "Lao a Lao", Duration.ofSeconds(184), "/music/lao_a_lao.mp3");
                    } else if (songId == 10) {
                        selectedSong = new Song(10, "Ocean Man", Duration.ofSeconds(126), "/music/ocean_man.mp3");
                    } else if (songId == 11) {
                        selectedSong = new Song(11, "Pa'lla Voy", Duration.ofSeconds(263), "/music/palla_voy.mp3");
                    } else if (songId == 12) {
                        selectedSong = new Song(12, "Triforce Medley", Duration.ofSeconds(242),
                                "/music/tloz_triforce_medley_40.mp3");
                    } else if (songId == 13) {
                        selectedSong = new Song(13, "Vivir Mi Vida", Duration.ofSeconds(252),
                                "/music/vivir_mi_vida.mp3");
                    }

                    // Store selected song in session attribute if valid
                    if (selectedSong != null) {
                        ctx.sessionAttribute("currentSong", selectedSong);
                    }
                }

                // Redirect back to home route
                ctx.redirect("/home");
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
                Song currentSong = ctx.sessionAttribute("currentSong");
                String timeStampInSeconds = ctx.formParam("seconds");

                // As long as there is a currentSong and a timeStamp from the forum, update the
                // time stamp of the current song.
                if (currentSong != null && timeStampInSeconds != null) {
                    double seconds = Double.parseDouble(timeStampInSeconds);
                    currentSong.setTimeStampFromSeconds(seconds);
                    ctx.sessionAttribute("currentSong", currentSong);
                    ctx.status(200); // tells the client the server successfully processed request.
                } else {
                    ctx.status(500); // tells frontend the request came through but wasn't able to finished to due an
                                     // internal service error.
                }

            });

            config.routes.get("/api/promotions", ctx -> {
                CreatePromotion[] promotionArray = CreatePromotion.getAllPromotions();

                if (promotionArray == null) {
                    ctx.status(500).result("Failed to retrieve promotions");
                    return;
                }

                promotionArray = java.util.Arrays.stream(promotionArray)
                        .filter(java.util.Objects::nonNull)
                        .toArray(CreatePromotion[]::new);

                ctx.render("public/promotions.html", Map.of("promotions", promotionArray));
            });
            config.routes.get("/promotions", ctx -> {
                ctx.render("public/promotions.html");
            });


            config.routes.get("/api/specific-promotion", ctx -> {
                // just displaying the first promotion to test the route.
                String promotionName = CreatePromotion.getPromotionName(1);
                String promotionDescription = CreatePromotion.getPromotionDescription(1);
                String promotionSong = CreatePromotion.getPromotionSong(1);
                System.out.println("Promotion Name: " + promotionName);
                System.out.println("Promotion Description: " + promotionDescription);
                System.out.println("Promotion Song: " + promotionSong);

                if (promotionName == null || promotionDescription == null || promotionSong == null) {
                    //if any of the information is null, return a 404 error to the client.
                    ctx.status(404).result("Promotion not found");
                } else {
                    //now we need to show the information to the html file
                    ctx.render("public/SpecificPromotion.html", Map.of(
                            "promotionName", promotionName,
                            "promotionDescription", promotionDescription,
                            "promotionSong", promotionSong
                    ));
                }
            });

            config.routes.get("/specific-promotion/{id}", ctx -> {
                int id = Integer.parseInt(ctx.pathParam("id"));

                System.out.println("\n\nFetching promotion ID: " + id+"\n\n");

                String name = CreatePromotion.getPromotionName(id);
                String description = CreatePromotion.getPromotionDescription(id);
                String song = CreatePromotion.getPromotionSong(id);

                if (name == null || description == null || song == null) {
                    ctx.status(404).result("Promotion not found");
                    return;
                }

                ctx.render("public/SpecificPromotion.html", Map.of(
                        "promotionName", name,
                        "promotionDescription", description,
                        "promotionSong", song));
            });





        });


        return app;

    }

    public static void main(String[] args) {
        // Create Javalin instance.
        Javalin app = createApp();
        int port = 7000;

        // Start the application.
        app.start(port); // use port 8080 if something is running on port 7000

        // Print location of where server is running.
        System.out.println("Server running at http://localhost:" + port + '/');
    }
}
