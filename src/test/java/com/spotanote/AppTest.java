package com.spotanote;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}