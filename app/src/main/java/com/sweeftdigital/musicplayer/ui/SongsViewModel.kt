package com.sweeftdigital.musicplayer.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sweeftdigital.musicplayer.model.RequestCall
import com.sweeftdigital.musicplayer.model.Song
import com.sweeftdigital.musicplayer.repo.SongsRepository

/**
 * SongsViewModel. This is our viewmodel class. It connects our repository class to our UI.
 *
 * We can also use it to cache data across device orientation changes.
 */
class SongsViewModel(private val songsRepository: SongsRepository) : ViewModel() {
    fun loadAllSongs(activity: AppCompatActivity): MutableLiveData<RequestCall> {
        return songsRepository.fetchAllSongs(activity)
    }

    fun play(mediaPlayer: MediaPlayer, context: Context, song: Song): MutableLiveData<RequestCall> {
        return songsRepository.play(mediaPlayer, context, song)
    }

    fun pause(mediaPlayer: MediaPlayer, context: Context, song: Song): MutableLiveData<RequestCall> {
        return songsRepository.pause(mediaPlayer, context, song)
    }

    fun getTimeFromProgress(progress: Int, duration: Int): Int {
        return songsRepository.getTimeFromProgress(progress, duration)
    }

    fun getSongProgress(totalDuration: Int, currentDuration: Int): Int {
        return songsRepository.getSongProgress(totalDuration, currentDuration)
    }

    fun convertToTimerMode(songDuration: String): String {
        return songsRepository.convertToTimerMode(songDuration)
    }
}