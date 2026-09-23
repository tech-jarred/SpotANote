
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
    private static final String DB_USER = "username";
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
    public static int musicUpload(String songName, int songLength, String filePath, int artistID)
    {
        String sql = "INSERT INTO Song (song_name, song_duration, file_path, artist_id) VALUES (?, ?, ?, ?)";

        //try-catch block to connect to the database and insert into a song into the database
        try (
            Connection connect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement prepstmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            )
        {
            //bind values to placeholders
            prepstmt.setString(1, songName);
            prepstmt.setInt(2, songLength);
            prepstmt.setString(3, filePath);
            prepstmt.setInt(4, artistID);

            int affectedRows = prepstmt.executeUpdate();

            if (affectedRows > 0) {
                //fetching the generated key (id column)
                try (ResultSet generatedKeys = prepstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Music Uploaded Successfully! Generated Song ID: " + generatedId);
                        return generatedId;
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