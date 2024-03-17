package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.minirogue.holocanon.feature.home.screen.GetHomeFragment
import com.minirogue.holocanon.feature.media.list.usecase.GetMediaListFragment
import com.minirogue.holocanon.feature.select.filters.usecase.GetSelectFiltersFragment
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.databinding.FragmentTabbedListContainerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TabbedListContainerFragment : Fragment() {

    @Inject
    lateinit var getSelectFiltersFragment: GetSelectFiltersFragment

    @Inject
    lateinit var getMediaListFragment: GetMediaListFragment

    @Inject
    lateinit var getHomeFragment: GetHomeFragment

    private enum class TabInfo(val tabNameRes: Int) {
        // The order here defines the order of the tabs
        HOME(R.string.nav_home),
        MEDIA_LIST(R.string.nav_media_list),
        FILTERS(R.string.nav_filters),
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentTabbedListContainerBinding.inflate(inflater, container, false)
        // Setting ViewPager for each Tabs
        val fragmentCreator = { tabInfo: TabInfo ->
            when (tabInfo) {
                TabInfo.HOME -> getHomeFragment()
                TabInfo.MEDIA_LIST -> getMediaListFragment()
                TabInfo.FILTERS -> getSelectFiltersFragment()
            }
        }
        binding.viewpager.adapter = MainTabAdapter(this, fragmentCreator)
        // Connect tabs to viewpager
        TabLayoutMediator(binding.resultTabs, binding.viewpager) { tab, position ->
            tab.text = getString(TabInfo.entries[position].tabNameRes)
        }.attach()

        return binding.root
    }

    private class MainTabAdapter(
            fragment: Fragment,
            private val fragmentCreator: (TabInfo) -> Fragment
    ) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = TabInfo.entries.size

        override fun createFragment(position: Int): Fragment = fragmentCreator(TabInfo.entries[position])
    }
}
