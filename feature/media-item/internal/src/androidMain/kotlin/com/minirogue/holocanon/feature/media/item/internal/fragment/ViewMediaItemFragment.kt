package com.minirogue.holocanon.feature.media.item.internal.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.minirogue.common.model.MediaType
import com.minirogue.holocanon.feature.media.item.internal.R
import com.minirogue.holocanon.feature.media.item.internal.databinding.MediaItemFragmentBinding
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewMediaItemFragment : Fragment() {

    private val viewModel: ViewMediaItemViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = MediaItemFragmentBinding.inflate(inflater, container, false)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(ITEM_ID_KEY, -1) ?: -1
        if (bundleItemId != -1) viewModel.setItemId(bundleItemId)

        viewModel.state.collectWithLifecy

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.liveMediaItemDto.asFlow()
                .combine(viewModel.isNetworkAllowed) { item, isNetworkAllowed ->
                    Pair(item, isNetworkAllowed)
                }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { updateViews(it.first, it.second, fragmentBinding) }
        }
        viewModel.liveMediaNotesDto.observe(
            viewLifecycleOwner,
            { notes -> updateViews(notes, fragmentBinding) }
        )
        viewModel.liveMediaTypeDto.observe(
            viewLifecycleOwner,
            { mediaType -> updateView(mediaType, fragmentBinding) }
        )
        viewModel.checkBoxText.asLiveData(lifecycleScope.coroutineContext)
            .observe(
                viewLifecycleOwner,
                { arr ->
                    fragmentBinding.checkbox1.text = arr[0]
                    fragmentBinding.checkbox2.text = arr[1]
                    fragmentBinding.checkbox3.text = arr[2]
                }
            )
        viewModel.checkBoxVisibility.asLiveData(lifecycleScope.coroutineContext)
            .observe(
                viewLifecycleOwner,
                { visibilityArray -> updateViews(visibilityArray, fragmentBinding) }
            )

        with(fragmentBinding.checkbox1) { setOnClickListener { viewModel.toggleCheckbox1(isChecked) } }
        with(fragmentBinding.checkbox2) { setOnClickListener { viewModel.toggleCheckbox2(isChecked) } }
        with(fragmentBinding.checkbox3) { setOnClickListener { viewModel.toggleCheckbox3(isChecked) } }

        return fragmentBinding.root
    }

    private fun updateViews(
        visibilityArray: BooleanArray,
        fragmentBinding: MediaItemFragmentBinding
    ) {
        fragmentBinding.checkbox1.visibility = if (visibilityArray[0]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox2.visibility = if (visibilityArray[1]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox3.visibility = if (visibilityArray[2]) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun updateViews(
        item: MediaItemDto,
        isNetworkAllowed: Boolean,
        fragmentBinding: MediaItemFragmentBinding
    ) {
        fragmentBinding.mediaTitle.text = item.title
        fragmentBinding.descriptionTextview.text = if (item.description.isNotBlank()) {
            getString(R.string.media_item_description_header) + " " + item.description
        } else ""
        fragmentBinding.releaseDate.text = item.date
        fragmentBinding.imageCover.load(item.imageURL) {
            placeholder(R.drawable.common_resources_app_icon)
            if (isNetworkAllowed) {
                networkCachePolicy(CachePolicy.ENABLED)
            } else networkCachePolicy(CachePolicy.DISABLED)
        }
        if (item.series > 0) {
            fragmentBinding.viewSeriesButton.visibility = View.VISIBLE
            fragmentBinding.viewSeriesButton.setOnClickListener {
                navigationViewModel.navigateTo(NavigationDestination.SeriesScreen(item.series))
            }
        }
    }

    private fun updateViews(notes: MediaNotesDto, fragmentBinding: MediaItemFragmentBinding) {
        fragmentBinding.checkbox3.isChecked = notes.isBox3Checked
        fragmentBinding.checkbox1.isChecked = notes.isBox1Checked
        fragmentBinding.checkbox2.isChecked = notes.isBox2Checked
    }

    private fun updateView(mediaType: MediaType?, fragmentBinding: MediaItemFragmentBinding) {
        fragmentBinding.mediaType.text = mediaType?.getSerialName() ?: ""
    }

    companion object {
        private const val ITEM_ID_KEY = "item-id"
        fun createInstance(itemId: Int): ViewMediaItemFragment {
            return ViewMediaItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ITEM_ID_KEY, itemId)
                }
            }
        }
    }
}
