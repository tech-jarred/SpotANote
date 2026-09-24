
//imports
package com.spotanote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * MusicUpload class
 * Focuses on uploading music
 */

/*
Helpful database connection links:
- https://www.geeksforgeeks.org/java/java-program-to-insert-details-in-a-table-using-jdbc/#google_vignette
- https://stackoverflow.com/questions/24500664/java-insert-to-sql-database-with-jdbc
- https://www.sqlservertutorial.net/java-sql-server/java-sql-server-insert/
*/


public class MusicUpload {

    //database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";

    /*
     * Upload Music function
     * 
     *  @param song's name as a string
     *  @param song's length which denotes the duraction in int seconds
     *  @param song's file path which can be uploaded as a .mp3 or a link starting with https://
     *  @param artist id which is the int id for the artist, this will eventually have its own helper method
     *  @return song id produced by adding the song to the mysql database
     */
    public static int musicUpload(String songName, int songLength, String filePath, int recordMemberID)
    {
        //checking for missing data
        if (songName == null || songName.trim().isEmpty()) {
            throw new IllegalArgumentException("Song name cannot be null or empty");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (recordMemberID <= 0) {
            throw new IllegalArgumentException("Invalid record member ID");
        }
        if (songLength <= 0) {
            throw new IllegalArgumentException("Song duration must be more than 0");
        }

        //checks to see if theres already an upload with that file
        String checkDuplicateSql = "SELECT COUNT(*) FROM Song WHERE file_path = ?";

        //sql insert statement
        String sql = "INSERT INTO Song (song_name, song_duration, file_path, record_member_id) VALUES (?, ?, ?, ?)";

        //try-catch block to connect to the database and insert a song into the database
        try (Connection connect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        
            //check for duplicate files (to fix last test)
            try (PreparedStatement checkStmt = connect.prepareStatement(checkDuplicateSql)) {
                checkStmt.setString(1, filePath);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalArgumentException("Duplicate upload: File path already exists in database.");
                    }
                }
            }
    
            //inserting into the database
            try (PreparedStatement prepstmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                prepstmt.setString(1, songName);
                prepstmt.setInt(2, songLength);
                prepstmt.setString(3, filePath);
                prepstmt.setInt(4, recordMemberID);
    
                int affectedRows = prepstmt.executeUpdate();
    
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = prepstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            System.out.println("Music Uploaded Successfully!");
                            return generatedId;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error uploading music: " + e.getMessage());
            e.printStackTrace();
        }

        return -1; //insertion failed
    }
}
/*
For reference:
CREATE TABLE Song (
	id INT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(100) NOT NULL,
    song_duration INT, -- will be in seconds
    file_path VARCHAR(300) NOT NULL,
    artist_id INT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES User(id)
 */