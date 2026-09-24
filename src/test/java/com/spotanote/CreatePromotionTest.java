package com.spotanote;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreatePromotionTest {


    private Promotion promotion = new Promotion(1, "Test Promotion", "This is a test promotion description.", "Test Song", "Test Record");

    @BeforeEach
    void setUp() {
    }

    /**
     * Tests to see that constructor for User class correctly assigns values to its attributes.
     */
    @Test
    void testPromotionName()
    {
        Boolean expected = (!promotion.getPromotionName().equals("") && promotion.getPromotionName() != null);
        Boolean actual = !CreatePromotion.create().getPromotionName().equals("") && CreatePromotion.create().getPromotionName() != null;
        assertEquals(expected, actual);
    }

    @Test
    void testPromotionDescription()
    {
        Boolean expected = (!promotion.getPromotionDescription().equals("") && promotion.getPromotionDescription() != null);
        Boolean actual = !CreatePromotion.create().getPromotionDescription().equals("") && CreatePromotion.create().getPromotionDescription() != null;
        assertEquals(expected, actual);
    }

    @Test
    void testPromotionSong()
    {
        Boolean expected = (!promotion.getPromotionSong().equals("") && promotion.getPromotionSong() != null);
        Boolean actual = !CreatePromotion.create().getPromotionSong().equals("") && CreatePromotion.create().getPromotionSong() != null;
        assertEquals(expected, actual);
    }

    @Test
    void testUploadPromotion()
    {
        //Need more time to finish up this test
    }

    @Test
    void testAccessToSelectedSong()
    {
        String songName = promotion.getPromotionSong();
        String recordName = Song.getRecordName(songName);
        Boolean expected = true; //recordName.equals(promotion.getRecordName());
        Boolean actual = CreatePromotion.create().getRecordName().equals(Song.getRecordName(songName));
        assertEquals(expected, actual);
    }

    @Test
    void testTooMuchTextTitle()
    {
        int maxLength = 25; // Assuming the maximum length for the title is 25 characters
        int titleLength = promotion.getPromotionName().length();
        Boolean expected = (titleLength <= maxLength);
        Boolean actual = (CreatePromotion.create().getPromotionName().length() <= maxLength);
        assertEquals(expected, actual);
    }

    @Test
    void testTooMuchTextDescription()
    {
        int maxLength = 25; // Assuming the maximum length for the title is 25 characters
        int descriptionLength = promotion.getPromotionDescription().length();
        Boolean expected = (descriptionLength <= maxLength);
        Boolean actual = (CreatePromotion.create().getPromotionDescription().length() <= maxLength);
        assertEquals(expected, actual);
    }
}
