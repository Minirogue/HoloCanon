package com.minirogue.holocanon.feature.series.internal.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.holocanon.feature.series.internal.R
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes

internal class SeriesListAdapter(private val onItemClicked: (itemId: Long) -> Unit) :
    ListAdapter<MediaAndNotes, SeriesListAdapter.MediaViewHolder>(DiffCallback) {

    override fun submitList(list: List<MediaAndNotes>?) {
        if (list != null) {
            super.submitList(ArrayList(list))
        } else {
            super.submitList(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_media_list_item, parent, false)
        return MediaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem.mediaItem.id)
        }
        holder.titleTextView.text = currentItem.mediaItem.title
    }

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.media_title)
        }
    }

    companion object {
        private val DiffCallback: DiffUtil.ItemCallback<MediaAndNotes> =
            object : DiffUtil.ItemCallback<MediaAndNotes>() {
                override fun areItemsTheSame(
                    oldItem: MediaAndNotes,
                    newItem: MediaAndNotes,
                ): Boolean {
                    return oldItem.mediaItem.id == newItem.mediaItem.id
                }

                override fun areContentsTheSame(
                    oldItem: MediaAndNotes,
                    newItem: MediaAndNotes,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
