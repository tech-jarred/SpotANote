
package  com.spotanote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ArtistRecordSearchTest class
 * Focuses on testing the ArtistRecordSearch class
 */
class ArtistRecordSearchTest
{
    int artistID = 0;
    int recordManagerID = 0;
    int recordID = 0;
    
    /*
     * Sets up environment for testing before each test is ran
     */
    @BeforeEach
    void setUp() throws SQLException
    {
        String sqlUser = "INSERT INTO TestUser (id, username, password, name, is_name_public, roleid1, roleid2) VALUES (?, ?, ?, ?, TRUE, ?, ?)";
        String sqlRecord = "INSERT INTO TestRecord (record_name, record_manager_id) VALUES ('Test Record', ?)";

        try (Connection connect = DBExplorer.getConnection())
        {
            //finding the max id available for user inserts because user ids are not autoincremented
            //used AI for this because I didn't even know this existed tbh
            int nextId = 1;
            try (PreparedStatement stmt = connect.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM TestUser");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nextId = rs.getInt(1);
                }
            }

            //create an artist to test with
            artistID = nextId++;
            try (PreparedStatement stmt = connect.prepareStatement(sqlUser)) {
                stmt.setInt(1, artistID);
                stmt.setString(2, "testArtist");
                stmt.setString(3, "testartistpswd");
                stmt.setString(4, "Sabrina Carpenter");
                stmt.setInt(5, 1);
                stmt.setInt(6, 2);
                stmt.executeUpdate();
            }

            //create a record manager to help create a record to test with
            recordManagerID = nextId++;
            try (PreparedStatement stmt = connect.prepareStatement(sqlUser)) {
                stmt.setInt(1, recordManagerID);
                stmt.setString(2, "testRecord");
                stmt.setString(3, "testrecordpswd");
                stmt.setString(4, "Test Record");
                stmt.setInt(5, 3);
                stmt.setNull(6, java.sql.Types.INTEGER);
                stmt.executeUpdate();
            }

            //create a record to test with
            try (PreparedStatement stmt = connect.prepareStatement(sqlRecord, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, recordManagerID);
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        recordID = keys.getInt(1); // Assuming recordID is defined at the class level
                    } else {
                        throw new SQLException("Failed to retrieve generated key for record.");
                    }
                }
            }
        }
    }

    /**
     * Tears down environemnt after each test is ran
     */
    @AfterEach
    void tearDown()
    {
        String sqlUser = "DELETE FROM TestUser WHERE id IN (?, ?)";
        String sqlRecord = "DELETE FROM TestRecord WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection()) {
            try (PreparedStatement stmt = connect.prepareStatement(sqlUser)) {
                stmt.setInt(1, artistID);
                stmt.setInt(2, recordManagerID);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connect.prepareStatement(sqlRecord)) {
                stmt.setInt(1, recordID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Tear down failed during row deletion: " + e.getMessage());
        }
    }

    /**
     * Tests to see if an artist is correctly searched for using their full artist name
     */
    @Test
    void testSuccessfulArtistSearchTest() throws SQLException
    {
        //searching through the specified table
        List<Integer> ids = ArtistRecordSearch.discoverySearch("Sabrina Carpenter", "TestUser");

        //verifying artist list is returned and contains the correct artist ID
        assertNotNull(ids, "Returned list of ids should not be null");
        assertTrue(ids.contains(artistID), "List of ids should contain artist ID: " + artistID);
    }

    /**
     * Tests to see what happens when the alleged full artist name is seatched for but doesn't show up
     */
    void testUnsuccessfulArtistSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see if an artist is correctly searched for using some of their name
     */
    void testSuccessfulPartialArtistSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see what happens when part of the artist name is searched for but doesn't show up
     */
    void testUnsuccessfulPartialArtistSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see what happens when searching for an artist that doesn't exist in the database
     */
    void testArtistDoesNotExist() throws SQLException
    {
        //
    }

    /**
     * Tests to see if a record is correctly searched for using its full record name
     */
    void testSuccessfulRecordSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see what happens when the full record name is searched for but doesn't show up
     */
    void testUnsuccessfulRecordSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see if a record is correctly searched for using some of its name
     */
    void testSuccessfulPartialRecordSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see what happens when part of the record name is searched for but doesn't show up
     */
    void testUnsuccessfulPartialRecordSearch() throws SQLException
    {
        //
    }

    /**
     * Tests to see what happens when searching for a record that doesn't exist in the database
     */
    void testSearchRecordDoesNotExist() throws SQLException
    {
        //
    }
}