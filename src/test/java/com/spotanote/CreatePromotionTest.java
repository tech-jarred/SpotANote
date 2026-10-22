package com.spotanote;
import java.sql.SQLException;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreatePromotionTest 
{
    int currentPromotionId;

    @BeforeEach
    void setUp() 
    {
        // Set up any necessary preconditions for the tests, such as creating a promotion in the database.
        CreatePromotion.createPromotion("Test Promotion2", "This is a test promotion.", "Test Song", 1);
        int promotionId = CreatePromotion.getPromotionIdByName("Test Promotion2");
        //System.out.println("Promotion ID: " + promotionId);
        assertTrue(promotionId > 0, "Promotion should be created");
        this.currentPromotionId = promotionId;

    }
    @AfterEach
    void tearDown() throws SQLException {
        // Clean up any resources or reset the state after each test, such as deleting the test promotion from the database.
        CreatePromotion.clearLikesForUser(1, currentPromotionId);
        CreatePromotion.clearLikesForUser(2, this.currentPromotionId);
        CreatePromotion.clearLikesForUser(3, this.currentPromotionId);
        CreatePromotion.clearLikesForUser(4, this.currentPromotionId);
        //System.out.println("\n\n\n\nCurrent PromotionId: "+this.currentPromotionId+"\n\n\n\n");
        CreatePromotion.removePromotion(this.currentPromotionId);

    }

    @Test
    void testUploadToDatabase() throws SQLException
    {
        //CreatePromotion.createPromotion("Test Promotion", "This is a test promotion.", "Test Song", 2);
        CreatePromotion.createPromotion("TestPromotion", "From Testing To See if it exists", "Test Song", 1);
        // Check if the promotion was successfully created in the database
        int id = CreatePromotion.getPromotionIdByName("TestPromotion");
        boolean outcome = CreatePromotion.promotionExists(id);

        CreatePromotion.removePromotion(id);
        assertTrue(outcome);

    }

    //testing to see if the removePromotion function works correctly
    @Test
    void removePromotion()
    {
        CreatePromotion.createPromotion("Test Deletion", "This is a test promotion.", "Test Song", 2);
        int id = CreatePromotion.getPromotionIdByName("Test Deletion");
        boolean outcome = CreatePromotion.removePromotion(id);
        assertTrue(outcome);
    }

    @Test
    void testCreatePromotionWithNullName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CreatePromotion.createPromotion(null, "Description", "Song", 1);
        });
        assertEquals("Promotion name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePromotionWithNullDescription() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CreatePromotion.createPromotion("Name", null, "Song", 1);
        });
        assertEquals("Promotion description cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePromotionWithNullSong() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CreatePromotion.createPromotion("Name", "Description", null, 1);
        });
        assertEquals("Promotion song cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePromotionWithInvalidRecordId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CreatePromotion.createPromotion("Name", "Description", "Song", 0);
        });
        assertEquals("Record ID cannot be null or empty", exception.getMessage());
    }

    //here we are trying to test to see if we can get all the promotions from inside the database
    @Test void getAllPromotions()
    {
        
    }

    @Test 
    void savingCorrectTitle()
    {
        CreatePromotion.createPromotion("Name2", "Description", "Song", 1);
        int id = CreatePromotion.getPromotionIdByName("Name2");
        String actual = CreatePromotion.getPromotionName(id);
        String correctName = "Name2";
        CreatePromotion.removePromotion(id);
        assertEquals(correctName, actual);
    }
    @Test 
    void savingCorrectDescription()
    {
        CreatePromotion.createPromotion("Name2", "Description", "Song", 1);
        int id = CreatePromotion.getPromotionIdByName("Name2");
        String actual = CreatePromotion.getPromotionDescription(id);
        String correctName = "Description";
        CreatePromotion.removePromotion(id);
        assertEquals(correctName, actual);
    }

    @Test
    void savingCorrectSong()
    {
        CreatePromotion.createPromotion("Name2", "Description", "SongTest", 1);
        int id = CreatePromotion.getPromotionIdByName("Name2");
        String actual = CreatePromotion.getPromotionSong(id);
        String correctName = "SongTest";
        CreatePromotion.removePromotion(id);
        assertEquals(correctName, actual);
    }

    //here we are testing the like promotion functionality that was created in CreatePromotion.java. This test will check if the like promotion functionality is working as expected.
    @Test
    void testLikePromotion() {

        assertTrue(currentPromotionId > 0, "Promotion should be created");

        // First call: add the like.
        assertTrue(
            CreatePromotion.likePromotion(this.currentPromotionId, 1)
        );

    }

    @Test
    void testIsPromotionLikedByUser()
    {
        // Check if the promotion is liked by the user
        boolean isLiked = CreatePromotion.isPromotionLikedByUser(this.currentPromotionId, 1); // Assuming user_id = 1 and promotion_id = 1
        //System.out.println("Is promotion liked by user: " + isLiked);
        if (!isLiked) {
            // If not liked, like the promotion first
            isLiked = CreatePromotion.likePromotion(this.currentPromotionId, 1);
            //System.out.println("Promotion liked by user.");
        }
        //System.out.println("Checking if promotion is liked by user after liking it.");
        assertTrue(isLiked);
    }

    //for this test we first want to like the promotion, then we call it again since
    //like checks to see if it is already liked and if so it unlikes itself
    @Test
    void testUnlikePromotion() {
        // First, like the promotion
        boolean isLiked = CreatePromotion.likePromotion(this.currentPromotionId, 1);
        assertTrue(isLiked, "Promotion should be liked by the user");

        // now we need to unlike it
        isLiked = CreatePromotion.likePromotion(this.currentPromotionId, 1);
        assertTrue(isLiked, "Promotion should be liked by the user");

    }

    //here we are just testing to see if we can count the right number of likes that is associated with the promotion
    //also if the account / user has already liked it we don't unlike because we are just checking to see if there
    //are the right nnumber of likes in the database
    @Test
    void testLikeAmount() throws SQLException
    {
        if (CreatePromotion.isPromotionLikedByUser(this.currentPromotionId, 4) != true)
            {
                CreatePromotion.likePromotion(this.currentPromotionId, 4);
            }

        if (CreatePromotion.isPromotionLikedByUser(this.currentPromotionId, 3) != true)
            {
                CreatePromotion.likePromotion(this.currentPromotionId, 3);
            }
        if (CreatePromotion.isPromotionLikedByUser(this.currentPromotionId, 2) != true)
            {
                CreatePromotion.likePromotion(this.currentPromotionId, 2);
            }
        int likeAmount = CreatePromotion.getLikeAmount(currentPromotionId);
        assertEquals(likeAmount, 3);

    }

}
