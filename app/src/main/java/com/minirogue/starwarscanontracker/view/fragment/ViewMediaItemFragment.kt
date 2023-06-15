package com.minirogue.starwarscanontracker.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaTypeDto
import com.minirogue.starwarscanontracker.databinding.FragmentViewMediaItemBinding
import com.minirogue.starwarscanontracker.viewmodel.ViewMediaItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewMediaItemFragment : Fragment() {

    companion object {
        private const val MENU_ITEM_AMAZON_BUY = 1
        private const val MENU_ITEM_AMAZON_STREAM = 2
    }

    private val viewModel: ViewMediaItemViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentViewMediaItemBinding.inflate(inflater, container, false)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        if (bundleItemId != -1) viewModel.setItemId(bundleItemId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.liveMediaItem.asFlow()
                .combine(viewModel.isNetworkAllowed) { item, isNetworkAllowed -> Pair(item, isNetworkAllowed) }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { updateViews(it.first, it.second, fragmentBinding) }
        }
        viewModel.liveMediaNotes.observe(viewLifecycleOwner, { notes -> updateViews(notes, fragmentBinding) })
        viewModel.liveMediaTypeDto.observe(viewLifecycleOwner, { mediaType -> updateView(mediaType, fragmentBinding) })
        viewModel.checkBoxText.asLiveData(lifecycleScope.coroutineContext).observe(viewLifecycleOwner, { arr ->
            fragmentBinding.checkbox1.text = arr[0]
            fragmentBinding.checkbox2.text = arr[1]
            fragmentBinding.checkbox3.text = arr[2]
        })
        viewModel.checkBoxVisibility.asLiveData(lifecycleScope.coroutineContext).observe(viewLifecycleOwner,
            { visibilityArray -> updateViews(visibilityArray, fragmentBinding) })

        fragmentBinding.checkbox3.setOnClickListener { viewModel.toggleCheckbox3() }
        fragmentBinding.checkbox2.setOnClickListener { viewModel.toggleCheckbox2() }
        fragmentBinding.checkbox1.setOnClickListener { viewModel.toggleCheckbox1() }

        return fragmentBinding.root
    }

    private fun updateViews(visibilityArray: BooleanArray, fragmentBinding: FragmentViewMediaItemBinding) {
        fragmentBinding.checkbox1.visibility = if (visibilityArray[0]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox2.visibility = if (visibilityArray[1]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox3.visibility = if (visibilityArray[2]) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun updateViews(item: MediaItem, isNetworkAllowed: Boolean, fragmentBinding: FragmentViewMediaItemBinding) {
        fragmentBinding.mediaTitle.text = item.title
        fragmentBinding.descriptionTextview.text = if (item.description.isNotBlank()) {
            getString(R.string.description_header) + " " + item.description
        } else ""
        fragmentBinding.releaseDate.text = item.date
        fragmentBinding.imageCover.load(item.imageURL) {
            placeholder(R.drawable.ic_launcher_foreground)
            if (isNetworkAllowed) {
                networkCachePolicy(CachePolicy.ENABLED)
            } else networkCachePolicy(CachePolicy.DISABLED)
        }
        makeShoppingMenu(item, fragmentBinding)
        if (item.series > 0) {
            fragmentBinding.viewSeriesButton.visibility = View.VISIBLE
            fragmentBinding.viewSeriesButton.setOnClickListener {
                val seriesFragment = SeriesFragment()
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundleItemId), item.series)
                seriesFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, seriesFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun updateViews(notes: MediaNotes, fragmentBinding: FragmentViewMediaItemBinding) {
        fragmentBinding.checkbox3.isChecked = notes.isBox3Checked
        fragmentBinding.checkbox1.isChecked = notes.isBox1Checked
        fragmentBinding.checkbox2.isChecked = notes.isBox2Checked
    }

    private fun updateView(mediaTypeDto: MediaTypeDto?, fragmentBinding: FragmentViewMediaItemBinding) {
        fragmentBinding.mediaType.text = mediaTypeDto?.text ?: ""
    }

    private fun makeShoppingMenu(item: MediaItem, fragmentBinding: FragmentViewMediaItemBinding) {
        val shoppingMenu = PopupMenu(fragmentBinding.root.context, fragmentBinding.affiliateLinksFab)
        if (item.amazonLink != "") {
            fragmentBinding.affiliateLinksFab.show()
            shoppingMenu.menu.add(0, MENU_ITEM_AMAZON_BUY, 0, "Buy on Amazon")
        }
        if (item.amazonStream != "") {
            fragmentBinding.affiliateLinksFab.show()
            shoppingMenu.menu.add(0, MENU_ITEM_AMAZON_STREAM, 0, "Stream on Amazon Video")
        }
        shoppingMenu.setOnMenuItemClickListener { menuItem ->
            val url = when (menuItem.itemId) {
                MENU_ITEM_AMAZON_BUY -> item.amazonLink
                MENU_ITEM_AMAZON_STREAM -> item.amazonStream
                else -> ""
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
            true
        }
        fragmentBinding.affiliateLinksFab.setOnClickListener { shoppingMenu.show() }
    }
}
