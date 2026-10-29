
package com.spotanote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ArtistRecordSearch class
 * Focuses on searching for both artists and records
 */
public class ArtistRecordSearch {
    /**
     * discoverySearch function - searches to discover artists and records
     */
    public static List<Integer> discoverySearch(String searchedString, String databaseTable) throws SQLException {
        List<Integer> discoveredIds = new ArrayList<>();

        //assigns correct fields in sql query
        String sql = "";
        if (databaseTable.contains("User")) //artists are actually stored as users in the database
            sql = "SELECT id FROM " + databaseTable + " WHERE name LIKE ? AND roleid2 = 2";
        else if (databaseTable.contains("Record"))
            sql = "SELECT id FROM " + databaseTable + " WHERE record_name LIKE ?";

        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setString(1, "%" + searchedString + "%");

                try(ResultSet rs = stmt.executeQuery()) {
                    //loops through all matching rows
                    while (rs.next()) {
                        discoveredIds.add(rs.getInt("id"));
                    }
                }
            }
        return discoveredIds; //returns an empty list [] of no matches are found
    }

    /**
     * artistLookup() - display helper method that looks up a single Artist by its primary key id
     * 
     * @param lookupID - the id of the artist being looked up
     * @return string artist name or null if not found
     *  Note: assumption is the front end will make the outputted name look nice
     */
    public static String artistLookup(int lookupID) throws SQLException {
        String sql = "SELECT name FROM User WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setInt(1, lookupID);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }
        return null; //return null if no matching artist was found for that ID
    }

    /**
     * recordLookup() - display helped mtheod that looks up a single Record by its primary key id
     * 
     * @param lookupID - the id of the record being looked up
     * @return string record name or null if not found
     *  Note: assumption is the front end will make the outputted record name look nice
     */
    public static String recordLookup(int lookupID) throws SQLException {
        String sql = "SELECT record_name FROM Record WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setInt(1, lookupID);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("record_name");
                    }
                }

            }
        return null; //return null if no matching record was found for that ID
    }
}