package com.spotanote;
import java.util.Objects;

public class User {
    // Declare class variables.
    private final int id;
    private String username;
    private String name;
    private final String role;

    // Define the constructor
    public User(int id, String username, String name, String role){
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
    }

    // Define getter methods
    public int getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getName(){
        return name;
    }

    public String getRole(){
        return role;
    }

    // Overriding .equals() method and hashing method
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true; // check to see if object being compared to is stored at same location in memory
        if (obj == null || this.getClass() != obj.getClass()) return false; // if not, see if obj is null or of a different class
        User user = (User) obj;
        return this.id == user.id; // if not, cast obj to User and check its id against this.
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
