package com.spotanote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Song {
    
        private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
        private static final String DB_USER = "spotanote_user";
        private static final String DB_PASSWORD = "password";
        private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    // grabbing the song name

    public static String getSongName(int songId) {
        String songName = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT song_name FROM Song WHERE song_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, songId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        songName = resultSet.getString("song_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songName;
    }


        public static Song[] getAllSongs() throws SQLException{
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM Song";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Count the number of rows
                    resultSet.last();
                    int rowCount = resultSet.getRow();
                    resultSet.beforeFirst();

                    Song[] songs = new Song[rowCount];
                    int index = 0;
                    while (resultSet.next()) {
                        songs[index++] = new Song();
                    }
                    return songs;
                }
            }
        }
    
    }


    public static String getSongArtist(int songId) {
        String songArtist = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT song_artist FROM Song WHERE song_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, songId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        songArtist = resultSet.getString("song_artist");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songArtist;
    }

    public static void makeSongPublic(int songId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE Song SET song_is_public = true WHERE song_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, songId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Song set to public successfully");
                } else {
                    System.out.println("No song found with the given ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error making song public: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void makeSongPrivate(int songId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE Song SET song_is_public = false WHERE song_Id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, songId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Song set to private successfully");
                } else {
                    System.out.println("No song found with the given ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error making song private: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



