package com.spotanote;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreatePromotionTest 
{

    @Test
    void testUploadToDatabase()
    {
        CreatePromotion.createPromotion("Test Promotion", "This is a test promotion.", "Test Song", 2);

        // Check if the promotion was successfully created in the database


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

    @Test
    void testRemovePromotionFromDatabase() {
        // Create a promotion to be removed
        int promotionId = CreatePromotion.createPromotion("Test Promotion", "This is a test promotion.", "Test Song", 2);

        // Remove the promotion from the database
        boolean removed = CreatePromotion.removePromotion(promotionId);

        // Check if the promotion was successfully removed
        assertTrue(removed, "Promotion should be successfully removed from the database.");
    }

}
