package com.sweeftdigital.musicplayer.ui.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sweeftdigital.musicplayer.R
import com.sweeftdigital.musicplayer.model.Song

class SongsAdapter(private val onClickListener: (Song) -> Unit) : RecyclerView.Adapter<SongsAdapter.SongsHolder>(){

    private val differCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsHolder {
        return SongsHolder(LayoutInflater.from(parent.context).inflate(R.layout.model, parent, false))
    }

    override fun onBindViewHolder(holder: SongsHolder, position: Int) {
        val song = differ.currentList[position]
        holder.setData(song, onClickListener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class SongsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.titleTV)
        private var btnPlay = itemView.findViewById<ImageButton>(R.id.playBtn)

        fun setData(song: Song, onClickListener: (Song) -> Unit) {
            tvTitle.text = song.title
            if (song.isPlaying) {
                btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                tvTitle.setTextColor(Color.BLUE)
                tvTitle.setTypeface(null, Typeface.BOLD)
            } else {
                btnPlay.setImageResource(android.R.drawable.ic_media_play)
                tvTitle.setTextColor(Color.BLACK)
                tvTitle.setTypeface(null, Typeface.NORMAL)
            }
            btnPlay.setOnClickListener {
                onClickListener.invoke(song)

                if (btnPlay.id == android.R.drawable.ic_media_pause) {
                    btnPlay.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                }
                tvTitle.setTextColor(Color.BLUE)
                tvTitle.setTypeface(null, Typeface.BOLD)
            }
        }
    }
}