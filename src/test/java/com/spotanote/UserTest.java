package com.spotanote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user1;
    private User user2SameId;
    private User user3DifferentId;

    @BeforeEach
    void setUp() {
        user1 = new User(1, "stars4life", "Jill Valentine", "Listener");
        user2SameId = new User(1, "starsForLife", "Jill Valentine", "Listener");
        user3DifferentId = new User(2, "motorcycleRide", "Claire Redfield", "Listener");
    }

    /**
     * Tests to see that constructor for User class correctly assigns values to its attributes.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals(1, user1.getId());
        assertEquals("stars4life", user1.getUsername());
        assertEquals("Jill Valentine", user1.getName());
        assertEquals("Listener", user1.getRole());
    }

    /**
     * Tests if new equals method can determine equality based on location in memory.
     */
    @Test
    void testEqualsSameReference() {
        assertEquals(user1, user1);
    }

    /**
     * Tests if new equals method can determine equality based on id number, not location in memory.
     */
    @Test
    void testEqualsSameIdDifferentMetadata() {
        assertEquals(user1, user2SameId);
    }

    /**
     * Tests to see if new equals method is able to determine two users are not equal.
     */
    @Test
    void testEqualsDifferentId() {
        assertNotEquals(user1, user3DifferentId);
    }

    /**
     * Tests to see if equals method determines not equal when one object is null.
     */
    @Test
    void testEqualsNull() {
        assertNotEquals(null, user1);
    }

    /**
     * Tests to see if equals method determines not equal when object being compared is of different object type (not User class)
     */
    @Test
    void testEqualsDifferentObjectType() {
        assertNotEquals("a string object", user1);
    }

    /**
     * Tests new Hash method to make sure it creates correct hash codes based on id.
     */
    @Test
    void testHashCodeEqualObjects() {
        assertEquals(user1.hashCode(), user2SameId.hashCode());
    }

    /**
     * Tests to see if hash method creates different hash codes if id is different between Users.
     */
    @Test
    void testHashCodeDifferentObjects() {
        assertNotEquals(user1.hashCode(), user3DifferentId.hashCode());
    }
}