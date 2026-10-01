package com.spotanote;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

public class AppTest {

    // A helper method which will create a temporary instance of web app so routes can be tested.
    private Javalin createApp() {
        return App.createApp();
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
     * Tests 
     */
    @Test
    @DisplayName("IDENTIFIER: Successful-Music-Pause")
    void testSuccessfulMusicPause() {
        Javalin app = App.createApp();

        JavalinTest.test(app, (server, client) -> {
            // Simulate user logging in.
            client.post("/login", "username=jarredn&password=jarredpswd2"); 

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
}