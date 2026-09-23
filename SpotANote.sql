
-- delete schema for debugging purposes
-- DROP SCHEMA SpotANote;

-- create statements
CREATE SCHEMA SpotANote;
USE SpotANote;

-- create tables

CREATE TABLE Role (
	id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

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

CREATE TABLE Artist (
	id INT PRIMARY KEY,
    artist_name VARCHAR(150) NOT NULL
);

CREATE TABLE Song (
	id INT PRIMARY KEY,
    song_name VARCHAR(100) NOT NULL,
    artist_id INT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES artist(id)
);

CREATE TABLE Playlist (
	id INT PRIMARY KEY,
    user_id INT NOT NULL,
    playlist_name VARCHAR(150) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE PlaylistSong (
	id INT AUTO_INCREMENT PRIMARY KEY,
    playlist_id INT NOT NULL,
    song_id INT NOT NULL,
    song_order INT NOT NULL,
    FOREIGN KEY (playlist_id) REFERENCES Playlist(id),
    FOREIGN KEY (song_id) REFERENCES Song(id)
);

CREATE TABLE CurrentQueue (
	id INT PRIMARY KEY,
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    queue_order INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (song_id) REFERENCES Song(id)
);