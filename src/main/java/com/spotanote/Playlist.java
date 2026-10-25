package com.spotanote;

public class Playlist {
    private final int id;
    private final String name;
    private final boolean isPublic;

    public Playlist(int id, String name, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
    }

    public int getId() { 
        return this.id; 
    }

    public String getName() { 
        return this.name; 
    }

    public boolean isPublic() { 
        return this.isPublic; 
    }
}