package com.sweeftdigital.musicplayer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sweeftdigital.musicplayer.repo.SongsRepository

class SongsViewModelFactory(private val repo: SongsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongsViewModel::class.java)) {
            return SongsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}