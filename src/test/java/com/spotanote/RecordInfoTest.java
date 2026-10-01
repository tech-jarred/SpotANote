package com.spotanote;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class RecordInfoTest {

    @Test
    void testRecordInsideDatabase(){
        //testing to see if the record id is inside of the database and ability to grab it
        String recordName = RecordInfo.getRecordName(1);
        assertEquals("Test Record", recordName, "Record name does not match expected value.");
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
    
}
