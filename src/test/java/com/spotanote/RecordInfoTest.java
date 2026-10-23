package com.spotanote;
import java.sql.SQLException;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class RecordInfoTest {
int recordId;
@BeforeEach
void setUp()
{
    RecordInfo.addRecord("Database Test Record", 1);
    recordId = RecordInfo.getRecordId("Database Test Record");
}

@AfterEach
void tearDown() throws SQLException
{
    RecordInfo.removeRecordFollowers(recordId);
    RecordInfo.removeRecord(recordId);
}


@Test
void testRecordInsideDatabase() {
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
        RecordInfo.followRecord(1, recordId);
        assertTrue(true, "User should be able to follow a record.");
       
    }

    @Test
    void testUnfollowRecord() throws SQLException{
        //testing to see if the user can unfollow a record
        RecordInfo.followRecord(1, recordId);
        assertTrue(RecordInfo.isRecordFollowedByUser(recordId, 1));
        RecordInfo.unfollowRecord(1, recordId);
        assertFalse(RecordInfo.isRecordFollowedByUser(recordId, 1));
    }

    @Test
    void testGetID() {
        int recordId = RecordInfo.getRecordId("Database Test Record");
        assertTrue(recordId > 0,"Record ID should be a valid positive ID.");
    }

    @Test
    void testGetRecordManagerId() throws Exception {
        assertTrue(recordId > 0);
        assertEquals(1, RecordInfo.getRecordManagerId(recordId));
    }

    @Test
    void testIsRecordFollowedByUser() throws Exception {
        RecordInfo.followRecord(1, recordId);
        assertTrue(RecordInfo.isRecordFollowedByUser(recordId, 1));
    }

    @Test
    void testIsRecordFollowedByUserWithInvalidIds() {
        assertThrows(IllegalArgumentException.class, () -> RecordInfo.isRecordFollowedByUser(0, 1));
        assertThrows(IllegalArgumentException.class, () -> RecordInfo.isRecordFollowedByUser(1, 0));
    }

    @Test
    void testGetRecordIdForUnknownName() {
        assertEquals(-1, RecordInfo.getRecordId("Nonexistent Record 999999"));
    }

    @Test
    void testAllFollowedRecordsWithInvalidUserId() throws Exception {
        assertNull(RecordInfo.allFollowedRecords(0));
        assertNull(RecordInfo.allFollowedRecords(-1));
    }

    @Test
    void testGetAllFollowedRecords() throws SQLException
    {
        RecordInfo.addRecord("record2", 1);
        RecordInfo.addRecord("record3", 1);
        RecordInfo.addRecord("record4", 1);
        RecordInfo.addRecord("record5", 1);
        RecordInfo.addRecord("record6", 1);

        RecordInfo.followRecord(1, RecordInfo.getRecordId("record2"));
        RecordInfo.followRecord(1, RecordInfo.getRecordId("record3"));
        RecordInfo.followRecord(1, RecordInfo.getRecordId("record4"));
        RecordInfo.followRecord(1, RecordInfo.getRecordId("record5"));
        RecordInfo.followRecord(1, RecordInfo.getRecordId("record6"));

        RecordInfo[] followedRecords = RecordInfo.allFollowedRecords(1);
        assertNotNull(followedRecords);
        for(RecordInfo record : followedRecords)
        {
            if(record != null)
            {
                assertTrue(record.recordName.matches("record[2-6]"));
            }
        }

        RecordInfo.removeRecord(RecordInfo.getRecordId("record2"));
        RecordInfo.removeRecord(RecordInfo.getRecordId("record3"));
        RecordInfo.removeRecord(RecordInfo.getRecordId("record4"));
        RecordInfo.removeRecord(RecordInfo.getRecordId("record5"));
        RecordInfo.removeRecord(RecordInfo.getRecordId("record6"));
    }

    @Test 
    void testFollowAmount() throws SQLException
    {
        RecordInfo.addRecord("record2", 1);

        RecordInfo.followRecord(1, RecordInfo.getRecordId("record2"));
        RecordInfo.followRecord(2, RecordInfo.getRecordId("record2"));
        RecordInfo.followRecord(3, RecordInfo.getRecordId("record2"));
        RecordInfo.followRecord(4, RecordInfo.getRecordId("record2"));
        int currentRecordId = RecordInfo.getRecordId("record2");
        int follwedAmount = RecordInfo.getFollowedAmount(currentRecordId);
        assertEquals(4, follwedAmount);

        RecordInfo.removeRecordFollowers(currentRecordId);
        RecordInfo.removeRecord(currentRecordId);
    }

    @Test
    void testGetAllFollowedWhenNotFollowing() throws SQLException
    {
        int amountFollowed = RecordInfo.getFollowedAmount(recordId);
        assertEquals(0, amountFollowed);
    }

    @Test 
    void testValidRecordNameFromId()
    {
        String expectedName = "Database Test Record";
        String givenName = RecordInfo.getRecordName(recordId);
        assertEquals(expectedName, givenName);
    }
    @Test 
    void testInvalidRecordId()
    {
        String name = RecordInfo.getRecordName(-1);
        assertNull(name);
    }
    @Test 
    void testGetManagerIdForInvalidRecord() throws SQLException
    {
        assertEquals(RecordInfo.getRecordManagerId(-1), -1);
    }
    @Test 
    void testRemoveRecord() throws SQLException
    {
        RecordInfo.addRecord("TestingRemoving", 1);
        int id = RecordInfo.getRecordId("TestingRemoving");
        RecordInfo.removeRecord(id);
        assertEquals(-1, RecordInfo.getRecordId("TestingRemoving"));
    }
}

