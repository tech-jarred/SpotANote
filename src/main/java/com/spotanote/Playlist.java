package com.spotanote;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private final int id;
    private final String name;
    private final boolean isPublic;
    private List<Song> songs = new ArrayList<>();

    public Playlist(int id, String name, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isPublic() { return isPublic; }

    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }
}