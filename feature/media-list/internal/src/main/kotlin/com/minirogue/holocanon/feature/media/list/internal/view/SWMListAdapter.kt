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
import com.minirogue.api.media.StarWarsMedia
import com.minirogue.holocanon.feature.media.list.internal.R
import com.minirogue.holocanon.feature.media.list.internal.databinding.MediaListItemBinding
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotesDto

class SWMListAdapter(
        private val adapterInterface: AdapterInterface,
) : ListAdapter<MediaAndNotesDto, SWMListAdapter.MediaViewHolder>(DiffCallback) {
    private var checkBoxText = arrayOf("", "", "")
    private var isCheckBoxActive = booleanArrayOf(true, true, true)

    interface AdapterInterface {
        fun onItemClicked(itemId: Int)
        fun onCheckbox1Clicked(mediaNotesDto: MediaNotesDto)
        fun onCheckbox2Clicked(mediaNotesDto: MediaNotesDto)
        fun onCheckbox3Clicked(mediaNotesDto: MediaNotesDto)
        fun getMediaTypeString(mediaTypeId: Int): String
        fun getSeriesString(seriesId: Int): String
        fun isNetworkAllowed(): Boolean
    }

    fun updateCheckBoxText(newCheckBoxText: Array<String>) {
        checkBoxText = newCheckBoxText
        notifyDataSetChanged()
    }

    fun updateCheckBoxVisible(newIsCheckboxActive: BooleanArray) {
        isCheckBoxActive = newIsCheckboxActive
        notifyDataSetChanged()
    }

    override fun submitList(list: List<MediaAndNotesDto>?) {
        if (list != null) {
            super.submitList(ArrayList(list))
        } else {
            super.submitList(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = MediaListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        getItem(position)?.let { currentItem: MediaAndNotesDto ->
            with(holder) {
                itemView.setOnClickListener { adapterInterface.onItemClicked(currentItem.mediaItemDto.id) }

                with(binding) {
                    bindTextViews(this, currentItem.mediaItemDto)
                    bindCheckBoxes(checkbox1, checkbox2, checkbox3, currentItem.mediaNotesDto)
                    bindCoverImage(imageCover, currentItem.mediaItemDto.imageURL)
                }
            }
        }
    }

    private fun bindTextViews(binding: MediaListItemBinding, mediaItem: StarWarsMedia) = with(binding) {
        mediaTitle.text = mediaItem.title
        dateTextview.text = mediaItem.releaseDate
        mediaType.text = mediaItem.type.getSerialName()
//        if (mediaItem.series > 0) {
//            series.text = adapterInterface.getSeriesString(mediaItem.series)
//            series.visibility = View.VISIBLE
//        } else {
        series.visibility = View.INVISIBLE
//        }
    }

    private fun bindCheckBoxes(checkbox1: CheckBox, checkbox2: CheckBox, checkbox3: CheckBox, notes: MediaNotesDto) {
        with(checkbox1) {
            if (isCheckBoxActive[0]) {
                text = checkBoxText[0]
                isChecked = notes.isBox1Checked
                visibility = View.VISIBLE
                tag = notes
                setOnClickListener { view: View -> adapterInterface.onCheckbox1Clicked(view.tag as MediaNotesDto) }
            } else {
                visibility = View.GONE
            }
        }
        with(checkbox2) {
            if (isCheckBoxActive[1]) {
                text = checkBoxText[1]
                isChecked = notes.isBox2Checked
                visibility = View.VISIBLE
                tag = notes
                setOnClickListener { view: View -> adapterInterface.onCheckbox2Clicked(view.tag as MediaNotesDto) }
            } else {
                visibility = View.GONE
            }
        }
        with(checkbox3) {
            if (isCheckBoxActive[2]) {
                text = checkBoxText[2]
                isChecked = notes.isBox3Checked
                visibility = View.VISIBLE
                tag = notes
                setOnClickListener { view: View -> adapterInterface.onCheckbox3Clicked(view.tag as MediaNotesDto) }
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun bindCoverImage(imageView: ImageView, imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            imageView.load(imageUrl) {
                placeholder(R.drawable.media_list_placeholder_image)
                if (adapterInterface.isNetworkAllowed()) {
                    networkCachePolicy(CachePolicy.ENABLED)
                } else networkCachePolicy(CachePolicy.DISABLED)
            }
        } else {
            imageView.load(R.drawable.media_list_placeholder_image)
        }
    }

    class MediaViewHolder(val binding: MediaListItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val DiffCallback: DiffUtil.ItemCallback<MediaAndNotesDto> =
                object : DiffUtil.ItemCallback<MediaAndNotesDto>() {
                    override fun areItemsTheSame(oldItem: MediaAndNotesDto, newItem: MediaAndNotesDto): Boolean {
                        return oldItem.mediaItemDto.id == newItem.mediaItemDto.id
                    }

                    override fun areContentsTheSame(oldItem: MediaAndNotesDto, newItem: MediaAndNotesDto): Boolean {
                        return oldItem == newItem
                    }
                }
    }
}
