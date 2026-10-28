package com.spotanote;

import java.util.List;

/**
 * Lightweight DTO representing a playlist and its songs.
 */
public record PlaylistSummary(
    int playlistId, 
    String playlistName, 
    List<SongSummary> songs
) {}