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
    
}



