
-- delete schema for debugging purposes
DROP SCHEMA SpotANote;

-- create statements
CREATE SCHEMA SpotANote;
USE SpotANote;

-- create user for datavase connection access
-- DROP USER 'spotanote_user'@'localhost';
FLUSH PRIVILEGES;
CREATE USER IF NOT EXISTS 'spotanote_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON SpotANote.* TO 'spotanote_user'@'localhost';
FLUSH PRIVILEGES;

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
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NULL,
    is_name_public BOOLEAN NOT NULL DEFAULT FALSE,
    roleid1 INT NOT NULL,
    roleid2 INT NULL,
    FOREIGN KEY (roleid1) REFERENCES Role(id),
    FOREIGN KEY (roleid2) REFERENCES Role(id)
);

-- Record
CREATE TABLE Record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_name VARCHAR(100) NOT NULL UNIQUE,
    record_manager_id INT NOT NULL,
    userId INT NOT NULL,
    FOREIGN KEY (record_manager_id) REFERENCES Role(id)
);

-- Record Member
CREATE TABLE RecordMember (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    artist_id INT NOT NULL UNIQUE,
    FOREIGN KEY (record_id) REFERENCES Record(id),
    FOREIGN KEY (artist_id) REFERENCES User(id)
);

-- Song
CREATE TABLE Song (
	id INT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(100) NOT NULL,
    song_duration INT, -- will be in seconds
    file_path VARCHAR(300) NOT NULL UNIQUE,
    record_member_id INT NOT NULL, -- encompasses artist_id and record_id
    is_public BOOLEAN NOT NULL DEFAULT FALSE, -- will always default to false
    FOREIGN KEY (record_member_id) REFERENCES RecordMember(id)
);

CREATE TABLE UsersLikedSongs (
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    dateLiked DATE NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (user_id, song_id),
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES Song(id) ON DELETE CASCADE
);

-- Playlist
-- Note: playlists are also albums so the playlist_name can also store the album name
CREATE TABLE Playlist (
	id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    playlist_name VARCHAR(150) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    is_personal BOOLEAN NOT NULL DEFAULT TRUE, 
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- TestSong for unit tests
CREATE TABLE TestSong (
	id INT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(100) NOT NULL,
    song_duration INT, -- will be in seconds
    file_path VARCHAR(300) NOT NULL UNIQUE,
    record_member_id INT NOT NULL, -- encompasses artist_id and record_id
    is_public BOOLEAN NOT NULL DEFAULT FALSE, -- will always default to false
    FOREIGN KEY (record_member_id) REFERENCES RecordMember(id)
);

-- TestPlaylist for unit tests
CREATE TABLE TestPlaylist (
	id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    playlist_name VARCHAR(150) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE, 
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- TestArtist for unit tests
CREATE TABLE TestUser (
    id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NULL,
    is_name_public BOOLEAN NOT NULL DEFAULT FALSE,
    roleid1 INT NOT NULL,
    roleid2 INT NULL,
    FOREIGN KEY (roleid1) REFERENCES Role(id),
    FOREIGN KEY (roleid2) REFERENCES Role(id)
);

-- TestRecord for unit tests
CREATE TABLE TestRecord (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_name VARCHAR(100) NOT NULL UNIQUE,
    record_manager_id INT NOT NULL,
    FOREIGN KEY (record_manager_id) REFERENCES Role(id)
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

CREATE TABLE Promotion (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_name VARCHAR(100) NOT NULL,
    promotion_description TEXT NOT NULL,
    promotion_song VARCHAR(300) NOT NULL,
    record_id INT NOT NULL,
    FOREIGN KEY (record_id) REFERENCES Record(id)
);

CREATE TABLE LikePromotion (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    promotion_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (promotion_id) REFERENCES Promotion(promotion_id)
);

CREATE TABLE UserFollowers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_Id INT,
    record_Id INT
);

-- insert statements

-- Role inserts
INSERT INTO Role (id, role_name) VALUES
(1, "Listener"),
(2, "Artist"),
(3, "Record");

-- User inserts
INSERT INTO USER (id, username, password, name, is_name_public, roleid1, roleid2) VALUES
(7071707, "hazelp", "hazelpswd1", "Hazel", TRUE, 1, 2), -- name has to be public for an artist
(3031303, "jarredn", "jarredpswd2", "Jarred", FALSE, 1, 1),
(5150515, "connors", "connorpswd3", "Connor", TRUE, 3, 1), -- record holders should also have public names
(1, "test", "testpswd", "Test", FALSE, 1, 3),
(2, "testAccount2", "recordpswd", "Record", TRUE, 3, 1),
(3, "testAccount3", "testpswd", "Bot", TRUE, 3, 1),
(4, "testAccount4", "testpswd", "Bot2", TRUE, 3, 1),
(5, "jvalentine", "stars4life", "Jill", TRUE, 1, 2),
(6, "leonkennedy", "RCPD1996", "Leon S. Kennedy", TRUE, 1, 1),
(7, "aripley", "WhereIsNostromo", "Amanda Ripley", TRUE, 1, 1),
(8, "Dutch", "tahiti", "Van Der Linde", TRUE, 1, 1),
(9, "reddeadshooterguy", "rdr2", "Arthur", TRUE, 1, 1);

-- Record inserts
INSERT INTO Record (record_name, record_manager_id, userId) VALUES
("Test Record", 3, 1),
("Second Record", 3, 5150515),
("Third Record", 3, 5150515),
("Fourth Record", 3, 5150515);

-- Record Member inserts
INSERT INTO RecordMember(record_id, artist_id) VALUES
(1, 7071707),
(1, 1),
(2, 2);


INSERT INTO Promotion (promotion_name, promotion_description, promotion_song, record_id) VALUES
("Summer Hits", "A collection of the hottest tracks for the summer season.", "Soaking Sets", 1),
("Chill Vibes", "Relax and unwind with these chill tunes.", "Frosty Frills", 1),
("Top 40", "The most popular songs right now.", "Crazy Crew", 1),
("Autumn Hits", "A song of the from the hottest tracks for the autumn season.", "Crimson Cloak", 1);

-- TestSong Inserts
-- INSERT INTO TestSong (id, song_name, song_duration, file_path, record_member_id, is_public) VALUES 
-- (1, 'Please Please Please', 187, '/music/please_please_please.mp3', 2, TRUE), 
-- (2, 'Revenge', 219, '/music/revenge.mp3', 2, TRUE), 
-- (3, 'Bailando', 254, '/music/bailando.mp3', 2, TRUE), 
-- (4, 'Danza Kuduro', 235, '/music/danza_kuduro.mp3', 2, TRUE), 
-- (5, 'Don''t Mine At Night', 214, '/music/dont_mine_at_night.mp3', 2, TRUE), 
-- (6, 'Drive By', 197, '/music/drive_by.mp3', 2, TRUE), 
-- (7, 'Fallen Kingdom', 252, '/music/fallen_kingdom.mp3', 2, TRUE), 
-- (8, 'Hips Don''t Lie', 218, '/music/hips_dont_lie.mp3', 2, TRUE), 
-- (9, 'Lao a Lao', 184, '/music/lao_a_lao.mp3', 2, TRUE), 
-- (10, 'Ocean Man', 126, '/music/ocean_man.mp3', 2, TRUE), 
-- (11, 'Pa''lla Voy', 263, '/music/palla_voy.mp3', 2, TRUE), 
-- (12, 'Triforce Medley', 242, '/music/tloz_triforce_medley_40.mp3', 2, TRUE), 
-- (13, 'Vivir Mi Vida', 252, '/music/vivir_mi_vida.mp3', 2, TRUE);

-- Song Inserts
INSERT INTO Song (id, song_name, song_duration, file_path, record_member_id, is_public) VALUES 
(1, 'Please Please Please', 187, '/music/please_please_please.mp3', 2, TRUE), 
(2, 'Revenge', 219, '/music/revenge.mp3', 2, TRUE), 
(3, 'Bailando', 254, '/music/bailando.mp3', 2, TRUE), 
(4, 'Danza Kuduro', 235, '/music/danza_kuduro.mp3', 2, TRUE), 
(5, 'Don''t Mine At Night', 214, '/music/dont_mine_at_night.mp3', 2, TRUE), 
(6, 'Drive By', 197, '/music/drive_by.mp3', 2, TRUE), 
(7, 'Fallen Kingdom', 252, '/music/fallen_kingdom.mp3', 2, TRUE), 
(8, 'Hips Don''t Lie', 218, '/music/hips_dont_lie.mp3', 2, TRUE), 
(9, 'Lao a Lao', 184, '/music/lao_a_lao.mp3', 2, TRUE), 
(10, 'Ocean Man', 126, '/music/ocean_man.mp3', 2, TRUE), 
(11, 'Pa''lla Voy', 263, '/music/palla_voy.mp3', 2, TRUE), 
(12, 'Triforce Medley', 242, '/music/tloz_triforce_medley_40.mp3', 2, TRUE), 
(13, 'Vivir Mi Vida', 252, '/music/vivir_mi_vida.mp3', 2, TRUE);

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


-- select statements

SELECT *
FROM Playlist;

SELECT *
FROM User;

SELECT *
FROM Song;

-- DELETE FROM Song WHERE file_path LIKE '/songs/%';
-- DELETE FROM Song WHERE id > 50;
-- INSERT INTO Playlist (user_id, playlist_name, is_public) VALUES (7071707, "Does this Work?", TRUE);
-- INSERT INTO Song (song_name, song_duration, file_path, record_member_id, is_public) VALUES ("Yo-Yo", 100, "/music/test.mp3", 1, TRUE);
-- INSERT INTO PlaylistSong (playlist_id, song_id, song_order) VALUES (1, 1, 1);

SELECT *
FROM Record;

-- TO BE DELETED SHORTLY AFTER
-- 2. Ensure RecordMember mapping exists for this Artist
-- (Connects artist_id 7071707 to Record ID 1)
INSERT IGNORE INTO RecordMember (id, record_id, artist_id) 
VALUES (1, 1, 7071707);

-- 3. Insert Tracks for the Albums
INSERT INTO Song (id, song_name, song_duration, file_path, record_member_id, is_public) VALUES
(101, 'Midnight Horizon', 210, '/music/midnight_horizon.mp3', 1, TRUE),
(102, 'Neon Lights', 195, '/music/neon_lights.mp3', 1, TRUE),
(103, 'Starlight Echoes', 240, '/music/starlight_echoes.mp3', 1, TRUE),
(104, 'Coastal Breeze', 180, '/music/coastal_breeze.mp3', 1, TRUE),
(105, 'Golden Hour', 225, '/music/golden_hour.mp3', 1, TRUE);

-- 4. Insert Published Albums/Playlists owned by the Artist
INSERT INTO Playlist (id, user_id, playlist_name, is_public) VALUES
(10, 7071707, 'Synthwave Dreams (Album)', TRUE),
(11, 7071707, 'Summer Acoustic Sessions', TRUE);

-- 5. Link Songs to Albums in PlaylistSong table
-- Album 1: Synthwave Dreams
INSERT INTO PlaylistSong (playlist_id, song_id, song_order) VALUES
(10, 101, 1),
(10, 102, 2),
(10, 103, 3);

-- Album 2: Summer Acoustic Sessions
INSERT INTO PlaylistSong (playlist_id, song_id, song_order) VALUES
(11, 104, 1),
(11, 105, 2);

-- Extra adds

-- Draft Songs (is_public = FALSE)
INSERT INTO Song (id, song_name, song_duration, file_path, record_member_id, is_public) VALUES
(201, 'Unreleased Demo 1', 185, '/music/demo1.mp3', 1, FALSE),
(202, 'Unreleased Demo 2', 215, '/music/demo2.mp3', 1, FALSE);

-- Draft Album (is_public = FALSE)
INSERT INTO Playlist (id, user_id, playlist_name, is_public) VALUES
(20, 7071707, 'Work In Progress (Draft)', FALSE);

-- Link Draft Songs to Draft Album
INSERT INTO PlaylistSong (playlist_id, song_id, song_order) VALUES
(20, 201, 1),
(20, 202, 2);