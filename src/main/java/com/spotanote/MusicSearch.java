
package com.spotanote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/*
 * MusicSearch class
 * Focuses on searching for music (including both songs and playlists)
 */
public class MusicSearch {
    /**
     * musicSearch function
     * 
     * @param searchedString - string searched for which includes any and all characters
     * @param databaseTable - string name of the table in the database we are looking through
     * @return an arraylist of the ids of all the songs outputted from the search SQL query
     */
    public static List<Integer> musicSearch(String searchedString, String databaseTable) throws SQLException {
        List<Integer> musicIds = new ArrayList<>();
        
        //assigns correct field name
        String name = "";
        if (databaseTable.contains("Song"))
            name = "song_name";
        else if (databaseTable.contains("Playlist"))
            name = "playlist_name";
        
        String sql = "SELECT id FROM " + databaseTable + " WHERE " + name + " LIKE ? AND is_public = TRUE";
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {

                stmt.setString(1, "%" + searchedString + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                //loops through all matching rows
                while (rs.next()) {
                    musicIds.add(rs.getInt("id"));
                }
            }
        }
        return musicIds; //returns an empty list [] if no matches are found
    }

    /**
     * songLookup() - display helper method that looks up a single Song by its primary key id
     * 
     * @param lookupID - the id of the song being looked up
     * @return Song object containing the details of the song or null if not found
     *  Note: assumption is the front end will make the outputted Song look nice
     */
    public static Song songLookup(int lookupID) throws SQLException {
        String sql = "SELECT id, song_name, song_duration, file_path, record_member_id FROM Song WHERE id = ?";

        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {

            stmt.setInt(1, lookupID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Song(
                        rs.getInt("id"),
                        rs.getString("song_name"),
                        Duration.ofSeconds(rs.getInt("song_duration")), //Song constructor uses Duration object not int
                        rs.getString("file_path")
                    );
                }
            }
        }
        return null; //return null if no matching song was found for that ID
    }

    /**
     * playlistLookup() - display helper method that looks up a single Playlist name by its primary key id
     * 
     * @param lookupID - the id of the playlist being looked up
     * @return string playlist name or null if not found
     *  Note: assumtpion is the front end will make the outputted playlist name look nice
     */
    public static String playlistLookup(int lookupID) throws SQLException {
        String sql = "SELECT playlist_name FROM Playlist WHERE id = ?";
        
        try (Connection connect = DBExplorer.getConnection();
            PreparedStatement stmt = connect.prepareStatement(sql)) {
                stmt.setInt(1, lookupID);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("playlist_name");
                    }
                }

            }
        return null; //return null if no matching playlist was found for that ID
    }
}