package com.spotanote;

import org.junit.jupiter.api.Test;

public class recordInfoTest {

    //creating tests for the recordInfo file

    @Test
    void testGetRecordName() {
        // Test case 1: Valid record ID
        int validRecordId = 1;
        String expectedRecordName = "Test Record";
        String actualRecordName = recordInfo.getRecordName(validRecordId);
        assert expectedRecordName.equals(actualRecordName) : "Test case 1 failed: Expected " + expectedRecordName + ", but got " + actualRecordName;

        // Test case 2: Invalid record ID
        int invalidRecordId = 9999;
        String expectedNullResult = null;
        String actualNullResult = recordInfo.getRecordName(invalidRecordId);
        assert expectedNullResult == actualNullResult : "Test case 2 failed: Expected null, but got " + actualNullResult;

        // Test case 3: Null record ID
        int nullRecordId = 0; // Assuming 0 is considered as null in this context
        String expectedNullResultForZero = null;
        String actualNullResultForZero = recordInfo.getRecordName(nullRecordId);
        assert expectedNullResultForZero == actualNullResultForZero : "Test case 3 failed: Expected null, but got " + actualNullResultForZero;

    }

    @Test
    void testFollowRecord() {
        // Test case 1: Valid user ID and record ID
        int validUserId = 1;
        int validRecordId = 1;
        try {
            recordInfo.followRecord(validUserId, validRecordId);
            // If no exception is thrown, the test passes
        } catch (Exception e) {
            assert false : "Test case 1 failed: Exception thrown - " + e.getMessage();
        }

        // Test case 2: Invalid user ID
        int invalidUserId = -1; // Assuming negative IDs are invalid
        try {
            recordInfo.followRecord(invalidUserId, validRecordId);
            assert false : "Test case 2 failed: Expected an exception for invalid user ID.";
        } catch (Exception e) {
            // Expected behavior, test passes
        }

        // Test case 3: Invalid record ID
        int invalidRecordId = -1; // Assuming negative IDs are invalid
        try {
            recordInfo.followRecord(validUserId, invalidRecordId);
            assert false : "Test case 3 failed: Expected an exception for invalid record ID.";
        } catch (Exception e) {
            // Expected behavior, test passes
        }
    }
    
}
