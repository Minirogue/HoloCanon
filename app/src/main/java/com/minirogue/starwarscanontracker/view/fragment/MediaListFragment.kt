package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication
import com.minirogue.starwarscanontracker.databinding.FragmentMediaListBinding
import com.minirogue.starwarscanontracker.model.SortStyle
import com.minirogue.starwarscanontracker.model.SortStyle.Companion.getAllStyles
import com.minirogue.starwarscanontracker.model.SortStyle.Companion.getSortText
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.view.FilterChip
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter.AdapterInterface
import com.minirogue.starwarscanontracker.view.viewBinding
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import javax.inject.Inject

class MediaListFragment : Fragment() {
    private val layoutId = R.layout.fragment_media_list
    private val binding by viewBinding(FragmentMediaListBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val mediaListViewModel by viewModels<MediaListViewModel> { viewModelFactory }

    private lateinit var sortChip: Chip

    private val adapterInterface: AdapterInterface = object : AdapterInterface {
        override fun onItemClicked(itemId: Int) {
            val viewMediaItemFragment = ViewMediaItemFragment()
            val bundle = Bundle()
            bundle.putInt(getString(R.string.bundleItemId), itemId)
            viewMediaItemFragment.arguments = bundle
            //TODO this should be handled by the activity or swapped to navigation components
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, viewMediaItemFragment)
                    .addToBackStack(null)
                    .commit()
        }

        override fun onCheckbox1Clicked(mediaNotes: MediaNotes) {
            mediaNotes.flipCheck1()
            mediaListViewModel.update(mediaNotes)
        }

        override fun onCheckbox2Clicked(mediaNotes: MediaNotes) {
            mediaNotes.flipCheck2()
            mediaListViewModel.update(mediaNotes)
        }

        override fun onCheckbox3Clicked(mediaNotes: MediaNotes) {
            mediaNotes.flipCheck3()
            mediaListViewModel.update(mediaNotes)
        }

        override fun getMediaTypeString(mediaTypeId: Int): String {
            return mediaListViewModel.convertTypeToString(mediaTypeId)
        }

        override fun getSeriesString(seriesId: Int): String {
            //TODO
            return "Series not found"
        }

        override fun isNetworkAllowed(): Boolean {
            return mediaListViewModel.isNetworkAllowed()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as CanonTrackerApplication).appComponent.inject(this)
        sortChip = makeCurrentSortChip()
        setupSortMenu()

        with(binding.mediaRecyclerview) {
            layoutManager = LinearLayoutManager(context)
            adapter = SWMListAdapter(adapterInterface).also {
                mediaListViewModel.filteredMediaAndNotes.observe(
                        viewLifecycleOwner,
                        Observer { mediaAndNotes -> it.submitList(mediaAndNotes) })
                mediaListViewModel.checkBoxText.observe(
                        viewLifecycleOwner,
                        Observer { newCheckBoxText -> it.updateCheckBoxText(newCheckBoxText) })
                mediaListViewModel.checkBoxVisibility.observe(
                        viewLifecycleOwner,
                        Observer { newIsCheckboxActive -> it.updateCheckBoxVisible(newIsCheckboxActive) })
            }
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) { //Scroll down
                        binding.sortFloatingActionButton.hide()
                    } else if (dy < 0) { //Scroll up
                        binding.sortFloatingActionButton.show()
                    }
                }
            })
            FastScrollerBuilder(this).build()
        }

        mediaListViewModel.activeFilters.observe(
                viewLifecycleOwner,
                Observer { filters: List<FullFilter> -> setFilterChips(filters) })
    }

    private fun setupSortMenu() {
        val sortMenu = PopupMenu(context, binding.sortFloatingActionButton, Gravity.END).apply {
            for (style in getAllStyles()) {
                menu.add(0, style, 0, getSortText(style))
            }
            setOnMenuItemClickListener { menuItem: MenuItem ->
                mediaListViewModel.setSort(menuItem.itemId)
                true
            }
        }
        binding.sortFloatingActionButton.setOnClickListener { sortMenu.show() }
    }

    private fun setFilterChips(filters: List<FullFilter>) = with(binding.filterChipGroup) {
        removeAllViews()
        addView(sortChip)
        for (filter in filters) {
            addView(makeCurrentFilterChip(filter))
        }
    }

    private fun makeCurrentFilterChip(fullFilter: FullFilter): Chip {
        val filterChip: Chip = FilterChip(fullFilter, requireContext())
        filterChip.setOnCloseIconClickListener { mediaListViewModel.deactivateFilter(fullFilter.filterObject) }
        return filterChip
    }

    private fun makeCurrentSortChip(): Chip =
            Chip(context).apply {
                isChipIconVisible = true
                setOnClickListener { mediaListViewModel.reverseSort() }
                mediaListViewModel.sortStyle.observe(
                        viewLifecycleOwner,
                        Observer { sortStyle: SortStyle -> updateSortChip(sortStyle) }
                )
            }.also {
                binding.filterChipGroup.addView(it)
            }

    private fun updateSortChip(sortStyle: SortStyle) = with(sortChip) {
        text = sortStyle.getText()
        chipIcon = if (sortStyle.isAscending()) {
            resources.getDrawable(R.drawable.ic_ascending_sort, null)
        } else {
            resources.getDrawable(R.drawable.ic_descending_sort, null)
        }
    }
}