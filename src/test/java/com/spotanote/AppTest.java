package com.spotanote;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.HttpClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    // A helper method which will create a temporary instance of web app so routes can be tested.
    private Javalin createApp() {
        return App.createApp();
    }

    // A helper method to validate that authentication is successful in any test which has a precondition of a user being logged in.
    private void loginTestUser(HttpClient client) {
        var response = client.post("/login", "username=jarredn&password=jarredpswd2");
        assertEquals(302, response.code(), "Precondition Failed: Could not authenticate test user.");
    }

    /**
     * Tests if a status is displayed when an incorrect password is entered on login page.
     */
    @Test
    void testUnsuccessfulAuthenticationHTTPStatus() {
        JavalinTest.test(createApp(), (server, client) -> {
            var response = client.post("/login", "username=jarrednpassword=rdr2");
            
            assertEquals(401, response.code());
            assertTrue(response.body().string().contains("Invalid username or password."));
        });
    }

    /**
     * Tests if user is successfully redirected when logging out.
     */
    @Test
    void testSuccessfulLogout() {
        JavalinTest.test(createApp(), (server, client) -> {
            var response = client.post("/logout");

            // Make sure we got the HTTP response code we expected.
            int code = response.code();
            assertTrue(code == 200 || code == 302, "Expected HTTP status 200 or 302, but got: " + code);
        });
    }

    /**
     * Tests if the backend can handle a request from frontend to upadte timeStamp attribute, as well as pass back that information
     * when reloading home page (simulating a successful music pause and resume)
     */
    @Test
    void testSuccessfulMusicPauseAndResume() {
        Javalin app = App.createApp();

        JavalinTest.test(app, (server, client) -> {
            // Simulate user logging in.
            loginTestUser(client);; 

            // Similate user being redirected to home screen.
            client.get("/home");

            // Simulate JavaScript fetch request when user pauses at 75 seconds
            var pauseResponse = client.post("/api/song/timeStamp", "seconds=75");

            // Verify 200 OK status code (fetch request was received and processed)
            assertEquals(200, pauseResponse.code());

            // Simulate user going home again. They should return to the song stopped where they paused.
            var homeResponse = client.get("/home");
            String responseBody = homeResponse.body().string();

            // Verify the time user paused at (75 seconds) is present in the reconstructed Home.html file.
            assertTrue(responseBody.contains("parseFloat(\"75.0\")") || responseBody.contains("parseFloat(\"75\")"), 
                "Expected /home response to contain the updated song timestamp of 75 seconds, but it did not.");
        });
    }

    /**
     * Tests if a user is able to select a song from music page and then play it from the beginning.
     */
    @Test
    void testSuccessfulSongSelectionAndPlayback() {
        Javalin app = App.createApp();

        JavalinTest.test(app, (server, client) -> {
            // Validate precondition of logging in.
            loginTestUser(client);

            // Simulate and verify user navigating to music selection page.
            var musicPageResponse = client.get("/music");
            assertEquals(200, musicPageResponse.code(), "Should successfully render the music library page.");

            // Simulate user selecting song "Revenge" from music page.
            var selectSongResponse = client.post("/select-song", "songId=2");

            // Verify selecting a song results in user being redirected to /home route.
            assertEquals(302, selectSongResponse.code());
            assertTrue(selectSongResponse.headers().get("Location").toString().contains("/home"), "Expected redirect location to contain /home");

            // Verify song starts at 0 secongs when loaded on home page.
            var homeResponse = client.get("/home");
            assertEquals(200, homeResponse.code());

            // Verify the HTML contains the file path for "Revenge", meaning correct song was added to session variable.
            String responseBody = homeResponse.body().string();
            assertTrue(responseBody.contains("/music/revenge.mp3"), 
                "Expected home page to load the file path for 'Revenge'.");

            // Verify the HTML contains the song name "Revenge"
            assertTrue(responseBody.contains("Revenge"), 
                "Expected home page to render the song title 'Revenge'.");

            // Verify the timestamp starts from the beginning (0 seconds)
            assertTrue(responseBody.contains("parseFloat(\"0\")"), 
                "Expected song to begin playing from 0 seconds.");
        });
    }
}