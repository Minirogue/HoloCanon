package com.minirogue.holocanon.feature.media.list.internal.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.holocanon.feature.media.list.internal.databinding.MediaListItemBinding
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import settings.model.CheckboxSettings

internal class SWMListAdapter(
    private val adapterInterface: AdapterInterface,
) : ListAdapter<MediaAndNotes, SWMListAdapter.MediaViewHolder>(DiffCallback) {
    private var checkBoxText = arrayOf("", "", "")
    private var isCheckBoxActive = booleanArrayOf(true, true, true)

    interface AdapterInterface {
        fun onItemClicked(itemId: Long)
        fun onCheckbox1Clicked(itemId: Long, newValue: Boolean)
        fun onCheckbox2Clicked(itemId: Long, newValue: Boolean)
        fun onCheckbox3Clicked(itemId: Long, newValue: Boolean)
        fun isNetworkAllowed(): Boolean
    }

    fun updateCheckBoxSettings(checkboxSettings: CheckboxSettings) {
        checkBoxText = arrayOf(
            checkboxSettings.checkbox1Setting.name.orEmpty(),
            checkboxSettings.checkbox2Setting.name.orEmpty(),
            checkboxSettings.checkbox3Setting.name.orEmpty(),
        )
        isCheckBoxActive = booleanArrayOf(
            checkboxSettings.checkbox1Setting.isInUse,
            checkboxSettings.checkbox2Setting.isInUse,
            checkboxSettings.checkbox3Setting.isInUse,
        )
    }

    override fun submitList(list: List<MediaAndNotes>?) {
        if (list != null) {
            super.submitList(ArrayList(list))
        } else {
            super.submitList(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding =
            MediaListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        getItem(position)?.let { currentItem: MediaAndNotes ->
            with(holder) {
                itemView.setOnClickListener { adapterInterface.onItemClicked(currentItem.mediaItem.id) }

                with(binding) {
                    bindTextViews(this, currentItem.mediaItem)
                    bindCheckBoxes(checkbox1, checkbox2, checkbox3, currentItem)
                    bindCoverImage(imageCover, currentItem.mediaItem.imageUrl)
                }
            }
        }
    }

    private fun bindTextViews(binding: MediaListItemBinding, mediaItem: StarWarsMedia) =
        with(binding) {
            mediaTitle.text = mediaItem.title
            dateTextview.text = mediaItem.releaseDate
            mediaType.text = mediaItem.type.getSerialName()
            series.visibility = View.INVISIBLE
        }

    private fun bindCheckBoxes(
        checkbox1: CheckBox,
        checkbox2: CheckBox,
        checkbox3: CheckBox,
        mediaAndNotes: MediaAndNotes,
    ) {
        with(checkbox1) {
            if (isCheckBoxActive[0]) {
                text = checkBoxText[0]
                isChecked = mediaAndNotes.notes.isBox1Checked
                visibility = View.VISIBLE
                setOnClickListener {
                    adapterInterface.onCheckbox1Clicked(
                        mediaAndNotes.mediaItem.id,
                        !mediaAndNotes.notes.isBox1Checked,
                    )
                }
            } else {
                visibility = View.GONE
            }
        }
        with(checkbox2) {
            if (isCheckBoxActive[1]) {
                text = checkBoxText[1]
                isChecked = mediaAndNotes.notes.isBox2Checked
                visibility = View.VISIBLE
                setOnClickListener {
                    adapterInterface.onCheckbox2Clicked(
                        mediaAndNotes.mediaItem.id,
                        !mediaAndNotes.notes.isBox2Checked,
                    )
                }
            } else {
                visibility = View.GONE
            }
        }
        with(checkbox3) {
            if (isCheckBoxActive[2]) {
                text = checkBoxText[2]
                isChecked = mediaAndNotes.notes.isBox3Checked
                visibility = View.VISIBLE
                setOnClickListener {
                    adapterInterface.onCheckbox3Clicked(
                        mediaAndNotes.mediaItem.id,
                        !mediaAndNotes.notes.isBox3Checked,
                    )
                }
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun bindCoverImage(imageView: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrBlank()) {
            imageView.load(imageUrl) {
                placeholder(com.holocanon.library.common.resources.R.drawable.common_resources_app_icon)
                if (adapterInterface.isNetworkAllowed()) {
                    networkCachePolicy(CachePolicy.ENABLED)
                } else {
                    networkCachePolicy(CachePolicy.DISABLED)
                }
            }
        } else {
            imageView.load(com.holocanon.library.common.resources.R.drawable.common_resources_app_icon)
        }
    }

    class MediaViewHolder(val binding: MediaListItemBinding) : RecyclerView.ViewHolder(binding.root)

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
