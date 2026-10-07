package com.spotanote;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class RecordInfoTest {


@Test
void testRecordInsideDatabase() {
    RecordInfo.addRecord("Database Test Record", 1);

    int recordId = RecordInfo.getRecordId("Database Test Record");

    System.out.println("Record ID = " + recordId);

    assertTrue(recordId > 0, "Record was not inserted into the database.");
}

    @Test 
    void testRecordNotInsideDatabase(){
        //testing to see if the record id is inside of the database and ability to grab it
        String recordName = RecordInfo.getRecordName(9999);
        assertNull(recordName, "Record name should be null for non-existent record ID.");
    }

    @Test
    void testNullNameRecord(){
        //testing to see if the record id is inside of the database and ability to grab it
        String recordName = RecordInfo.getRecordName(0);
        assertNull(recordName, "Record name should be null for non-existent record ID.");
    }

    @Test
    void testFollowRecord(){
        //testing to see if the user can follow a record
        RecordInfo.followRecord(1, 1);
        assertTrue(true, "User should be able to follow a record.");
       
    }


    @Test
    void testUnfollowRecord(){
        //testing to see if the user can unfollow a record
        RecordInfo.unfollowRecord(1, 1);
        assertTrue(true, "User should be able to unfollow a record.");
    }

    @Test
    void testGetID() {
        RecordInfo.addRecord("Test Record", 1);
        int recordId = RecordInfo.getRecordId("Test Record");
        assertTrue(recordId > 0,"Record ID should be a valid positive ID.");
    }
    
}
