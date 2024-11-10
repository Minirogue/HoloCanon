package com.minirogue.holocanon.feature.media.list.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.minirogue.holocanon.feature.media.list.internal.R
import com.minirogue.holocanon.feature.media.list.internal.databinding.MediaListFragmentBinding
import com.minirogue.holocanon.feature.media.list.internal.view.SWMListAdapter.AdapterInterface
import com.minirogue.holocanon.feature.media.list.internal.viewmodel.MediaListViewModel
import com.minirogue.starwarscanontracker.core.model.SortStyle
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import com.minirogue.starwarscanontracker.view.FilterChip
import dagger.hilt.android.AndroidEntryPoint
import filters.model.MediaFilter
import kotlinx.coroutines.launch
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@AndroidEntryPoint
internal class MediaListFragment : Fragment() {
    private lateinit var binding: MediaListFragmentBinding

    private val mediaListViewModel: MediaListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private lateinit var sortChip: Chip

    private val adapterInterface: AdapterInterface = object : AdapterInterface {
        override fun onItemClicked(itemId: Long) {
            navigationViewModel.navigateTo(NavigationDestination.MediaItemScreen(itemId))
        }

        override fun onCheckbox1Clicked(itemId: Long, newValue: Boolean) {
            mediaListViewModel.onCheckBox1Clicked(itemId, newValue)
        }

        override fun onCheckbox2Clicked(itemId: Long, newValue: Boolean) {
            mediaListViewModel.onCheckBox2Clicked(itemId, newValue)
        }

        override fun onCheckbox3Clicked(itemId: Long, newValue: Boolean) {
            mediaListViewModel.onCheckBox3Clicked(itemId, newValue)
        }

        override fun getSeriesString(seriesId: Int): String {
            // TODO
            return "Series not found"
        }

        override fun isNetworkAllowed(): Boolean {
            return mediaListViewModel.state.value.isNetworkAllowed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MediaListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenuItems()
        sortChip = makeCurrentSortChip()

        val swmListAdapter = SWMListAdapter(adapterInterface)

        binding.mediaListSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mediaListViewModel.updateSearch(newText)
                return false
            }
        }
        )

        with(binding.mediaRecyclerview) {
            layoutManager = LinearLayoutManager(context)
            adapter = swmListAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            FastScrollerBuilder(this).build()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaListViewModel.state.collect { state ->
                    if (state.checkboxSettings != null) swmListAdapter.updateCheckBoxSettings(state.checkboxSettings)
                    setFilterChips(state.activeFilters)
                    updateSortChip(state.sortStyle)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaListViewModel.mediaList.collect { mediaAndNotes ->
                    swmListAdapter.submitList(mediaAndNotes)
                }
            }
        }
    }

    private fun addMenuItems() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.media_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
        }.also {
            binding.filterChipGroup.addView(it)
        }

    private fun updateSortChip(sortStyle: SortStyle) = with(sortChip) {
        text = sortStyle.getText()
        chipIcon = if (sortStyle.isAscending()) {
            ResourcesCompat.getDrawable(resources, R.drawable.media_list_ascending_sort, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.media_list_decending_sort, null)
        }
    }
}
