package com.minirogue.starwarscanontracker.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.databinding.MediaListItemBinding
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel
import java.util.*

class SWMListAdapter(
        private val mediaListViewModel: MediaListViewModel,
        private val listener: OnClickListener
) : ListAdapter<MediaAndNotes, SWMListAdapter.MediaViewHolder>(DiffCallback) {
    //private final String TAG = "adapter";
    //private AsyncListDiffer<MediaAndNotes> listDiffer = new AsyncListDiffer<>(this, DiffCallback);
    //private List<MediaAndNotes> currentList = new ArrayList<>();
    private var checkBoxText = arrayOf("", "", "")
    private var isCheckBoxActive = booleanArrayOf(true, true, true)

    interface OnClickListener {
        fun onItemClicked(itemId: Int)
        fun onCheckbox1Clicked(mediaNotes: MediaNotes?)
        fun onCheckbox2Clicked(mediaNotes: MediaNotes?)
        fun onCheckbox3Clicked(mediaNotes: MediaNotes?)
    }

    fun updateCheckBoxText(newCheckBoxText: Array<String>) {
        checkBoxText = newCheckBoxText
        notifyDataSetChanged()
    }

    fun updateCheckBoxVisible(newIsCheckboxActive: BooleanArray) {
        isCheckBoxActive = newIsCheckboxActive
        notifyDataSetChanged()
    }

    override fun submitList(list: List<MediaAndNotes>?) {
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
        getItem(position)?.let { currentItem: MediaAndNotes ->
            with(holder) {
                itemView.setOnClickListener { listener.onItemClicked(currentItem.mediaItem.id) }

                with(binding) {
                    bindTextViews(this, currentItem.mediaItem)
                    bindCheckBoxes(checkbox1, checkbox2, checkbox3, currentItem.mediaNotes)
                    bindCoverImage(imageCover, currentItem.mediaItem.imageURL)
                }
            }
        }
    }


    private fun bindTextViews(binding: MediaListItemBinding, mediaItem: MediaItem) = with(binding) {
        mediaTitle.text = mediaItem.title
        dateTextview.text = mediaItem.date
        //TODO get rid of ViewModel reference here.
        mediaType.text = mediaListViewModel.convertTypeToString(mediaItem.type)
        //TODO improve metadata presentation
        extraInfo.visibility = View.INVISIBLE
    }

    private fun bindCheckBoxes(checkbox1: CheckBox, checkbox2: CheckBox, checkbox3: CheckBox, notes: MediaNotes) {
        with(checkbox1) {
            if (isCheckBoxActive[0]) {
                text = checkBoxText[0]
                isChecked = notes.isBox1Checked
                visibility = View.VISIBLE
                tag = notes
                setOnClickListener { view: View -> listener.onCheckbox1Clicked(view.tag as MediaNotes) }
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
                setOnClickListener { view: View -> listener.onCheckbox2Clicked(view.tag as MediaNotes) }
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
                setOnClickListener { view: View -> listener.onCheckbox3Clicked(view.tag as MediaNotes) }
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun bindCoverImage(imageView: SimpleDraweeView, imageUrl: String) = with(imageView) {
        hierarchy.setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE)
        if (imageUrl != "") {
            //TODO replace viewmodel reference here
            val request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(imageUrl))
                    .setLowestPermittedRequestLevel(if (mediaListViewModel.isNetworkAllowed()) ImageRequest.RequestLevel.FULL_FETCH else ImageRequest.RequestLevel.DISK_CACHE)
                    .build()
            setImageRequest(request)
            hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
        } else {
            setActualImageResource(R.drawable.ic_launcher_foreground)
            hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
        }
    }

    class MediaViewHolder(val binding: MediaListItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val DiffCallback: DiffUtil.ItemCallback<MediaAndNotes> = object : DiffUtil.ItemCallback<MediaAndNotes>() {
            override fun areItemsTheSame(oldItem: MediaAndNotes, newItem: MediaAndNotes): Boolean {
                return oldItem.mediaItem.id == newItem.mediaItem.id
            }

            override fun areContentsTheSame(oldItem: MediaAndNotes, newItem: MediaAndNotes): Boolean {
                return oldItem == newItem
            }
        }
    }
}
