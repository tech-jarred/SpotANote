package com.spotanote;

// This class is a data transfer object. It serves the sole purpose of helping transfer data from backend/memory to frontend
public record SongSummary(int songId, String songName) {}