
-- delete schema for debugging purposes
-- DROP SCHEMA SpotANote;

-- create statements
CREATE SCHEMA SpotANote;
USE SpotANote;

-- create tables

-- Role
CREATE TABLE Role (
	id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- User
CREATE TABLE User (
	id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NULL,
    is_name_public BOOLEAN NOT NULL DEFAULT FALSE,
    roleid1 INT NOT NULL,
    roleid2 INT NULL,
    FOREIGN KEY (roleid1) REFERENCES Role(id),
    FOREIGN KEY (roleid2) REFERENCES Role(id)
);

-- Song
CREATE TABLE Song (
	id INT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(100) NOT NULL,
    song_duration INT, -- will be in seconds
    file_path VARCHAR(300) NOT NULL,
    artist_id INT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES User(id)
);

-- Playlist
-- Note: playlists are also albums so the playlist_name can also store the album name
CREATE TABLE Playlist (
	id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    playlist_name VARCHAR(150) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Playlist Song
CREATE TABLE PlaylistSong (
	id INT AUTO_INCREMENT PRIMARY KEY,
    playlist_id INT NOT NULL,
    song_id INT NOT NULL,
    song_order INT NOT NULL,
    FOREIGN KEY (playlist_id) REFERENCES Playlist(id),
    FOREIGN KEY (song_id) REFERENCES Song(id)
);

-- Current Queue
CREATE TABLE CurrentQueue (
	id INT PRIMARY KEY,
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    queue_order INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (song_id) REFERENCES Song(id)
);

-- insert statements

-- Role inserts
INSERT INTO Role (id, role_name) VALUES
(1, "Listener"),
(2, "Artist"),
(3, "Record");

-- User inserts
INSERT INTO USER (id, username, password, name, is_name_public, roleid1, roleid2) VALUES
(7071707, hazelp, hazelpswd1, Hazel, TRUE, 1, 2), -- name has to be public for an artist
(3031303, jarredn, jarredpswd2, Jarred, FALSE, 1),
(5150515, connors, connorpswd3, Connor, TRUE, 3); -- record holders should also have public names

-- Song inserts, read more about audio below
-- INSERT INTO Song (song_name, song_duration, file_path, artist_id) VALUES
-- ();

-- How to serve the audio files in your app, Local Filesystem:
-- Save files in your project directory (e.g., public/audio/song_1.mp3).
-- In your database: file_path = '/audio/song_1.mp3'.

-- External Streaming / URLs:
-- If using external sources (S3, Cloudinary, YouTube, or royalty-free audio URLs).
-- In your database: file_path = '[https://example.com/audio/track1.mp3](https://example.com/audio/track1.mp3)'


-- Playlist inserts

-- Playlist Song inserts

-- Current Queue inserts

