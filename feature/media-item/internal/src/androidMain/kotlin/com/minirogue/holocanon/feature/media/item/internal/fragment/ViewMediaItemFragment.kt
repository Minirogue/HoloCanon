package com.minirogue.holocanon.feature.media.item.internal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.request.CachePolicy
import com.holocanon.feature.media.item.internal.R
import com.holocanon.feature.media.item.internal.databinding.MediaItemFragmentBinding
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import com.minirogue.starwarscanontracker.view.collectWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewMediaItemFragment : Fragment() {

    private val viewModel: ViewMediaItemViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val fragmentBinding = MediaItemFragmentBinding.inflate(inflater, container, false)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(ITEM_ID_KEY, -1) ?: -1
        if (bundleItemId != -1) viewModel.setItemId(bundleItemId)

        viewModel.state.collectWithLifecycle { state ->
            state.mediaItem?.also { updateViews(it, state.isNetworkAllowed, fragmentBinding) }
            state.mediaNotes?.also { updateViews(it, fragmentBinding) }
            updateView(state.mediaItem?.type, fragmentBinding)
            updateCheckboxText(state.checkboxText, fragmentBinding)
            updateViews(state.checkboxVisibility, fragmentBinding)
        }

        with(fragmentBinding.checkbox1) { setOnClickListener { viewModel.toggleCheckbox1(isChecked) } }
        with(fragmentBinding.checkbox2) { setOnClickListener { viewModel.toggleCheckbox2(isChecked) } }
        with(fragmentBinding.checkbox3) { setOnClickListener { viewModel.toggleCheckbox3(isChecked) } }

        return fragmentBinding.root
    }

    private fun updateViews(
        visibilityArray: BooleanArray,
        fragmentBinding: MediaItemFragmentBinding,
    ) {
        fragmentBinding.checkbox1.visibility = if (visibilityArray[0]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox2.visibility = if (visibilityArray[1]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox3.visibility = if (visibilityArray[2]) View.VISIBLE else View.GONE
    }

    private fun updateViews(
        item: StarWarsMedia,
        isNetworkAllowed: Boolean,
        fragmentBinding: MediaItemFragmentBinding,
    ) {
        fragmentBinding.mediaTitle.text = item.title
        fragmentBinding.descriptionTextview.text = if (!item.description.isNullOrBlank()) {
            getString(R.string.media_item_description_header) + " " + item.description
        } else {
            ""
        }
        fragmentBinding.releaseDate.text = item.releaseDate
        fragmentBinding.imageCover.load(item.imageUrl) {
            placeholder(R.drawable.common_resources_app_icon)
            if (isNetworkAllowed) {
                networkCachePolicy(CachePolicy.ENABLED)
            } else {
                networkCachePolicy(CachePolicy.DISABLED)
            }
        }
        val series = item.series
        if (!series.isNullOrBlank()) {
            fragmentBinding.viewSeriesButton.visibility = View.VISIBLE
            fragmentBinding.viewSeriesButton.setOnClickListener {
                navigationViewModel.navigateTo(NavigationDestination.SeriesScreen(series))
            }
        }
    }

    private fun updateViews(notes: MediaNotes, fragmentBinding: MediaItemFragmentBinding) {
        fragmentBinding.checkbox1.isChecked = notes.isBox1Checked
        fragmentBinding.checkbox2.isChecked = notes.isBox2Checked
        fragmentBinding.checkbox3.isChecked = notes.isBox3Checked
    }

    private fun updateView(mediaType: MediaType?, fragmentBinding: MediaItemFragmentBinding) {
        fragmentBinding.mediaType.text = mediaType?.getSerialName() ?: ""
    }

    private fun updateCheckboxText(
        checkboxText: Array<String>,
        fragmentBinding: MediaItemFragmentBinding,
    ) {
        fragmentBinding.checkbox1.text = checkboxText[0]
        fragmentBinding.checkbox2.text = checkboxText[1]
        fragmentBinding.checkbox3.text = checkboxText[2]
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
