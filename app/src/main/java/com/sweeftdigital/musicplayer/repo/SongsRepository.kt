package com.sweeftdigital.musicplayer.repo

import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.sweeftdigital.musicplayer.model.RequestCall
import com.sweeftdigital.musicplayer.model.Song
import com.sweeftdigital.musicplayer.common.Constants.LOADING
import com.sweeftdigital.musicplayer.common.Constants.PLAYING
import com.sweeftdigital.musicplayer.common.Constants.STOPPED

/**
 * SongRepository documentation
 *
 * Now that we have defined our model classes, we need to define a class we call the repository. This
 * class will still be under the model package as it deals with our data. In fact, the aim of this class
 * is to do its thing without touching any UI component. We call this separation of concerns. It makes
 * the class testable.
 *
 * This class will be responsible for:
 * 1. All manipulations to our song e.g fetching songs, playing/pausing songs, getting song progress
 * etc. Basically the logic involving our songs.
 */
class SongsRepository {
    private fun convertToSong(cursor: Cursor): Song {
        return Song(
            cursor.getString(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            cursor.getString(5),
            false
        )
    }

    /**
     * Fetch all songs from the device
     * @param activity
     * @return
     */
    fun fetchAllSongs(activity: AppCompatActivity): MutableLiveData<RequestCall> {
        val songs = ArrayList<Song>()
        val requestCall = RequestCall().apply {
            status = LOADING
            this.songs = songs
        }
        val mutableLiveData = MutableLiveData<RequestCall>()

        mutableLiveData.value = requestCall

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
        )

        val cursor = activity.applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null)

        while (cursor!!.moveToNext()) {
            songs.add(convertToSong(cursor))
        }
        requestCall.status = STOPPED
        requestCall.songs = songs
        mutableLiveData.postValue(requestCall)
        return mutableLiveData
    }

    /**
     * Play A Song
     * @param player
     * @param context
     * @param song
     * @return
     */
    fun play(mediaPlayer: MediaPlayer, context: Context, song: Song): MutableLiveData<RequestCall> {
        val requestCall = RequestCall().apply {
            status = STOPPED
            songs = ArrayList<Song>()
        }
        val mutableLiveData = MutableLiveData<RequestCall>().apply {
            value = requestCall
        }


        mediaPlayer.start()

        requestCall.status = PLAYING
        mutableLiveData.postValue(requestCall)
        return mutableLiveData
    }

    /**
     * Pause a MediaPlayer
     * @param player
     * @param context
     * @param song
     * @return
     */
    fun pause(mediaPlayer: MediaPlayer, context: Context, song: Song): MutableLiveData<RequestCall> {
        val requestCall = RequestCall().apply {
            songs = ArrayList<Song>()
        }

        val mutableLiveData = MutableLiveData<RequestCall>().apply {
            value = requestCall
        }

        mediaPlayer.pause()
        requestCall.status = STOPPED
        mutableLiveData.postValue(requestCall)
        return mutableLiveData
    }

    fun getTimeFromProgress(progress: Int, duration: Int): Int {
        return (duration * progress) / 100
    }

    /**
     * @param totalDuration
     * @param currentDuration
     * @return
     */
    fun getSongProgress(totalDuration: Int, currentDuration: Int): Int {
        return (currentDuration * 100) / totalDuration
    }

    /**
     * Convert To Timer Mode
     * @param songDuration
     * @return
     */
    fun convertToTimerMode(songDuration: String): String {
        val duration = songDuration.toInt()
        val hour = duration / (1000* 60 * 60)
        val minute = (duration % (1000* 60 * 60)) / (1000 * 60)
        val second = ((duration % (1000* 60 * 60)) % (1000 * 60)) / (1000)

        var finalString = StringBuilder()
        if (hour < 10) {
            finalString.append("0")
        }
        finalString.append("$hour:")
        if (minute < 10) {
            finalString.append("0")
        }
        finalString.append("$minute:")
        if (second < 10) {
            finalString.append("0")
        }
        finalString.append(second)

        return finalString.toString()
    }
}