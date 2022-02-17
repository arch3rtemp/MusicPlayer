package com.sweeftdigital.musicplayer.model

/**
 * Song class Documentation
 *
 * The song class represent a single song or music. It will hold song properties like artist, title and
 * duration. Instead of holding those properties as distinct variables or in an array, we can easily
 * wrap them under one class, as they collectively define our Song instance.
 */
data class Song(
    val id: String,
    val artist: String,
    val title: String,
    val data: String,
    val displayName: String,
    val duration: String,
    var isPlaying: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Song) {
            return false
        }
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}