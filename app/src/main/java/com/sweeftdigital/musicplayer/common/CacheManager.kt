package com.sweeftdigital.musicplayer.common

import com.sweeftdigital.musicplayer.model.Song

/**
 * This class has the following roles;
 * 1.Cache for us simple variables statically. Making them static implies they are not tied to
 * the instance of any class.
 */
object CacheManager {
    var SONG_POSITION = 0
    var HAS_FINISHED = false
    var CURRENT_SONG: Song? = null
    var SONGS_CACHE = ArrayList<Song>()
}