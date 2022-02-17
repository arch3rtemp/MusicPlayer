package com.sweeftdigital.musicplayer.model

import com.sweeftdigital.musicplayer.common.Constants.STOPPED

/**
 * RequestCall documentation:
 * This is one of our model class. It is a data object and as such will and should be simple.
 * This class has the following roles:
 * 1. Hold for us an arraylist of songs as well as the status of the current song.
 */
data class RequestCall(
    var status: Int = STOPPED,
    var songs: ArrayList<Song> = ArrayList<Song>()
)