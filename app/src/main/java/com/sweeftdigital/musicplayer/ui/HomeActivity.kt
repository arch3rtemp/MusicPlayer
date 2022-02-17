package com.sweeftdigital.musicplayer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sweeftdigital.musicplayer.common.CacheManager.SONGS_CACHE
import com.sweeftdigital.musicplayer.common.CacheManager.SONG_POSITION
import com.sweeftdigital.musicplayer.common.Constants.PERMISSIONS_REQUEST_CODE_READ_EXTERNAL_STORAGE
import com.sweeftdigital.musicplayer.common.Constants.PLAYING
import com.sweeftdigital.musicplayer.common.Constants.STOPPED
import com.sweeftdigital.musicplayer.databinding.ActivityHomeBinding
import com.sweeftdigital.musicplayer.model.Song
import com.sweeftdigital.musicplayer.repo.SongsRepository
import com.sweeftdigital.musicplayer.ui.adapter.SongsAdapter

/**
 * Main Activity Documentation
 *
 * In android UI's are based on activities. Activities represent pages and do have states as well as
 * lifecycle methods. We listen to the various lifecycle methods and render or work on data based on them.
 *
 */
class HomeActivity : AppCompatActivity() {
    /**
     * Below are the instance properties of this activity.
     */
    private lateinit var binding: ActivityHomeBinding
    private lateinit var songsAdapter: SongsAdapter
    private var hasFinished = false
    private var currentSong: Song? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var factory: SongsViewModelFactory
    private lateinit var songsViewModel: SongsViewModel

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        factory = SongsViewModelFactory(SongsRepository())
        songsViewModel = ViewModelProvider(this, factory).get(SongsViewModel::class.java)

        initializeViews()
        handleEvents()
    }

    override fun onResume() {
        super.onResume()

        checkPermissionsThenLoadSongs()
        setupRecycler(SONGS_CACHE)
    }

    private fun initializeViews() {
        with(binding) {
            songsAdapter = SongsAdapter { song ->
                onItemPlayButtonClicked(song)
            }

            content.songsRV.apply {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = songsAdapter
            }
        }
    }

    private fun onItemPlayButtonClicked(song: Song) {
        if (!song.isPlaying) {
            if (SONG_POSITION != getPosition(song)) {
                cleanUpMediaPlayer()
            }
            SONG_POSITION = getPosition(song)
            currentSong = SONGS_CACHE[SONG_POSITION]

            playOrPause(song)
            show("Now Playing: ${song.title}")

        } else {
            if (SONG_POSITION != getPosition(song)) {
                cleanUpMediaPlayer()
            }
            SONG_POSITION = getPosition(song)

            show("Stopped: ${song.title}")
            playOrPause(song)
        }
    }

    private fun handleEvents() {
        binding.content.playBtn.setOnClickListener {
            currentSong?.let { playOrPause(it) } ?: show("Please add some songs first")
        }

        binding.content.nextBtn.setOnClickListener {
            if (currentSong != null) {
                refreshRecyclerView(false)
                SONG_POSITION = getPosition(currentSong!!) + 1
                if (SONG_POSITION >= SONGS_CACHE.size) {
                    SONG_POSITION = 0
                }
                cleanUpMediaPlayer();
                val nextSong = SONGS_CACHE[SONG_POSITION]
                playOrPause(nextSong)
            }
        }

        binding.content.prevBtn.setOnClickListener {
            if (currentSong != null) {
                refreshRecyclerView(false)
                SONG_POSITION--
                if (SONG_POSITION < 0) {
                    SONG_POSITION = if (SONGS_CACHE.size > 0) {
                        SONGS_CACHE.size - 1
                    } else {
                        0
                    }
                }
                cleanUpMediaPlayer()
                val prevSong = SONGS_CACHE[SONG_POSITION]
                playOrPause(prevSong)
            }
        }

        binding.content.progressSB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                if (b) {
                    if (getPlayer() == null) {
                        show("Please add some songs to Play")
                        return
                    }
                    mediaPlayer!!.seekTo(
                        songsViewModel.getTimeFromProgress(
                            seekBar!!.progress,
                            getPlayer()!!.duration
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    /**
     * Setting up a recyclerview
     * @param songs
     */
    private fun setupRecycler(songs: ArrayList<Song>) {

    }

    /**
     * Because we are reading songs from the user's device, we need to ask the user for permissions
     * first at runtime.
     */
    private fun checkPermissionsThenLoadSongs() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
            show("Hey Please grant this app the permission to read external storage first")
        } else {
            fetchAllSongs()
        }
    }

    /**
     * The following method cleans up the mediaplayer, releasing it's resources from the memory.
     */
    private fun cleanUpMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun refreshRecyclerView(playing: Boolean) {
        if (currentSong != null) {
            currentSong!!.isPlaying = playing
            SONGS_CACHE[getPosition(currentSong!!)] = currentSong!!
            songsAdapter.differ.submitList(SONGS_CACHE)
        }
    }

    private fun fetchAllSongs() {
        songsViewModel.loadAllSongs(this).observe(this) { requestCall ->
            val linkedHashSet: LinkedHashSet<Song> = LinkedHashSet<Song>(requestCall.songs)
            SONGS_CACHE.clear()
            SONGS_CACHE.addAll(linkedHashSet)
            if (currentSong == null && SONGS_CACHE.size > 0) {
                currentSong = SONGS_CACHE[0]
            }
        }
    }

    /**
     * If you are given a song object, can you give us it's position. We may need that position
     * so that we know the next song in our playlist. Yes, the following method provides you the position.
     * @param s
     * @return
     */
    private fun getPosition(song: Song): Int {
        val pos = 0
        for (s in SONGS_CACHE) {
            if (song.id.equals(s.id, ignoreCase = true)) {
                return SONGS_CACHE.indexOf(song)
            }
        }
        return pos
    }

    /**
     * Instantiating media player
     * @return
     */
    private fun getPlayer(): MediaPlayer? {
        if (mediaPlayer == null) {
            if (currentSong == null) {
                currentSong = if (SONGS_CACHE.size > 0) {
                    SONGS_CACHE[0]
                } else {
                    return null
                }
            }
            mediaPlayer = MediaPlayer.create(this, Uri.parse(currentSong!!.data))
        }
        return mediaPlayer
    }

    /**
     * Showing a toast message
     * @param message
     */
    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun playOrPause(song: Song) {
        currentSong = song

        if (getPlayer() == null) {
            show("You don't have any song to play.Please add some songs first")
            return
        }

        hasFinished = false

        if (getPlayer()!!.isPlaying) {
            songsViewModel.pause(getPlayer()!!, this, song).observe(this) { requestCall ->
                if (requestCall.status == STOPPED) {
                    song.isPlaying = false
                    refreshRecyclerView(song.isPlaying)
                    binding.content.playBtn.setImageResource(android.R.drawable.ic_media_play)
                }
            }
        } else {
            songsViewModel.play(getPlayer()!!, this, song).observe(this) { requestCall ->
                if (requestCall.status == PLAYING) {
                    song.isPlaying = true
                    SONG_POSITION = getPosition(song)
                    refreshRecyclerView(song.isPlaying)
                    binding.content.playBtn.setImageResource(android.R.drawable.ic_media_pause)
                    updateSongProgress()
                }
            }
        }
    }

    private fun updateSongProgress() {
        handler.postDelayed(runnable, 1000)
    }

    private val runnable = object : Runnable {
        override fun run() {
            if (!hasFinished) {
                val currentDuration = mediaPlayer?.currentPosition
                val totalDuration = mediaPlayer?.duration

                binding.content.currentPosTV.text = songsViewModel.convertToTimerMode(currentDuration.toString())
                binding.content.progressSB.progress = songsViewModel.getSongProgress(totalDuration!!, currentDuration!!)
                binding.content.totalDurationTV.text = songsViewModel.convertToTimerMode(totalDuration.toString())

                if (binding.content.progressSB.progress >= 99 && !mediaPlayer!!.isPlaying) {
                    binding.content.playBtn.setImageResource(android.R.drawable.ic_media_play)
                    hasFinished = true
                    songsAdapter.differ.submitList(SONGS_CACHE)

                    binding.content.nextBtn.performClick()
                } else {
                    hasFinished = false
                }
                handler.postDelayed(this, 1000)
            }
        }
    }
}