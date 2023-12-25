package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.SortStyle
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.databinding.FragmentMediaListBinding
import com.minirogue.starwarscanontracker.view.FilterChip
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter.AdapterInterface
import com.minirogue.starwarscanontracker.view.viewBinding
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel
import dagger.hilt.android.AndroidEntryPoint
import filters.MediaFilter
import kotlinx.coroutines.launch
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@AndroidEntryPoint
class MediaListFragment : Fragment() {
    private val layoutId = R.layout.fragment_media_list
    private val binding by viewBinding(FragmentMediaListBinding::bind)

    private val mediaListViewModel: MediaListViewModel by viewModels()

    private lateinit var sortChip: Chip

    private val adapterInterface: AdapterInterface = object : AdapterInterface {
        override fun onItemClicked(itemId: Int) {
            val viewMediaItemFragment = ViewMediaItemFragment()
            val bundle = Bundle()
            bundle.putInt(getString(R.string.bundleItemId), itemId)
            viewMediaItemFragment.arguments = bundle
            // TODO this should be handled by the activity or swapped to navigation components
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewMediaItemFragment)
                .addToBackStack(null)
                .commit()
        }

        override fun onCheckbox1Clicked(mediaNotesDto: MediaNotesDto) {
            mediaNotesDto.flipCheck1()
            mediaListViewModel.update(mediaNotesDto)
        }

        override fun onCheckbox2Clicked(mediaNotesDto: MediaNotesDto) {
            mediaNotesDto.flipCheck2()
            mediaListViewModel.update(mediaNotesDto)
        }

        override fun onCheckbox3Clicked(mediaNotesDto: MediaNotesDto) {
            mediaNotesDto.flipCheck3()
            mediaListViewModel.update(mediaNotesDto)
        }

        override fun getMediaTypeString(mediaTypeId: Int): String {
            return mediaListViewModel.convertTypeToString(mediaTypeId)
        }

        override fun getSeriesString(seriesId: Int): String {
            // TODO
            return "Series not found"
        }

        override fun isNetworkAllowed(): Boolean {
            return mediaListViewModel.isNetworkAllowed.value
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.media_list_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_date_menu_item -> {
                mediaListViewModel.setSort(SortStyle.SORT_DATE)
                true
            }

            R.id.sort_by_timeline_menu_item -> {
                mediaListViewModel.setSort(SortStyle.SORT_TIMELINE)
                true
            }

            R.id.sort_by_title_menu_item -> {
                mediaListViewModel.setSort(SortStyle.SORT_TITLE)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortChip = makeCurrentSortChip()

        with(binding.mediaRecyclerview) {
            layoutManager = LinearLayoutManager(context)
            adapter = SWMListAdapter(adapterInterface).also {
                mediaListViewModel.filteredMediaAndNotes.observe(viewLifecycleOwner) { mediaAndNotes -> it.submitList(mediaAndNotes) }
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            mediaListViewModel.checkBoxText.collect { newCheckBoxText -> it.updateCheckBoxText(newCheckBoxText) }
                        }
                        launch {
                            mediaListViewModel.checkBoxVisibility.collect { newIsCheckboxActive ->
                                it.updateCheckBoxVisible(newIsCheckboxActive)
                            }
                        }
                    }
                }
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

                FastScrollerBuilder(this).build()
            }
        }

        mediaListViewModel.activeFilters.observe(viewLifecycleOwner) { filters: List<MediaFilter> -> setFilterChips(filters) }
    }

    private fun setFilterChips(filters: List<MediaFilter>) = with(binding.filterChipGroup) {
        removeAllViews()
        addView(sortChip)
        for (filter in filters) {
            addView(makeCurrentFilterChip(filter))
        }
    }

    private fun makeCurrentFilterChip(mediaFilter: MediaFilter): Chip {
        val filterChip: Chip = FilterChip(mediaFilter, requireContext())
        filterChip.setOnCloseIconClickListener { mediaListViewModel.deactivateFilter(mediaFilter) }
        return filterChip
    }

    private fun makeCurrentSortChip(): Chip =
        Chip(context).apply {
            isChipIconVisible = true
            setOnClickListener { mediaListViewModel.reverseSort() }
            mediaListViewModel.sortStyle.observe(
                viewLifecycleOwner,
                { sortStyle: SortStyle -> updateSortChip(sortStyle) }
            )
        }.also {
            binding.filterChipGroup.addView(it)
        }

    private fun updateSortChip(sortStyle: SortStyle) = with(sortChip) {
        text = sortStyle.getText()
        chipIcon = if (sortStyle.isAscending()) {
            ResourcesCompat.getDrawable(resources, R.drawable.ic_ascending_sort, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.ic_descending_sort, null)
        }
    }
}
