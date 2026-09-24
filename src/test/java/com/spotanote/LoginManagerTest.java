package com.spotanote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginManagerTest {
private LoginManager loginManager;

    @BeforeEach
    void setUp() {
        loginManager = new LoginManager();
    }

    /**
     * Tests to see if a user who exists in system and enters correct credentials is authenticated and has a user object made with their data.
     */
    @Test
    void testSuccessfulAuthentication() {

        User user = loginManager.authenticate("jarredn", "jarredpswd2");

        // Checks to see that an object was returned
        assertNotNull(user, "Expected a valid User object upon successful authentication.");

        // Checks to make sure object returned has correct information about user who signed in.
        assertEquals("jarredn", user.getUsername());
        assertEquals(3031303, user.getId());
        assertTrue("Jarred".equals(user.getName()));
        assertTrue("Listener".equals(user.getRole()));
    }

    /**
     * Tests to see if a user who enters a valid username but invalid password is prevented from entering system.
     */
    @Test
    void testUnsuccessfulAuthentication() {
        // Check if user is able to login.
        User user = loginManager.authenticate("jarredn", "rdr2");

        assertNull(user, "Expected null when valid username is paired with invalid password.");
    }

    /**
     * Tests to see if a user is allowed to enter system if they provide a username that does not exist in system.
     */
    @Test
    void testNonExistentUsername() {
        // Provide username that does not exist in database.
        User user = loginManager.authenticate("arthurmorgan", "rdr2");

        assertNull(user, "Expected null when username does not exist in database.");
    }

    /**
     * Tests to see if a user is able to not provide a username but still get into system (they should not be allowed in)
     */
    @Test
    void testEmptyUsername() {
        // Test both cases where information passed is considered null and considered empty string.
        User emptyStringUser = loginManager.authenticate("", "password123");
        User nullUser = loginManager.authenticate(null, "password123");

        assertNull(emptyStringUser, "Expected null when username is an empty string.");
        assertNull(nullUser, "Expected null when username is null.");
    }
}
