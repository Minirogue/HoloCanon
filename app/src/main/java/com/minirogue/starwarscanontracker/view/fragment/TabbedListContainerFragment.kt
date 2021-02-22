package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.databinding.FragmentTabbedListContainerBinding

class TabbedListContainerFragment : Fragment() {

    private enum class TabInfo(val tabNameRes: Int, val fragmentCreator: () -> Fragment) {
        // The order here defines the order of the tabs
        HOME(R.string.nav_home, { HomeFragment() }),
        MEDIA_LIST(R.string.nav_media_list, { MediaListFragment() }),
        FILTERS(R.string.nav_filters, { FilterSelectionFragment() }),
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentTabbedListContainerBinding.inflate(inflater, container, false)
        // Setting ViewPager for each Tabs
        binding.viewpager.adapter = MainTabAdapter(this)
        // Connect tabs to viewpager
        TabLayoutMediator(binding.resultTabs, binding.viewpager) { tab, position ->
            tab.text = getString(TabInfo.values()[position].tabNameRes)
        }.attach()

        return binding.root
    }

    private class MainTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = TabInfo.values().size

        override fun createFragment(position: Int): Fragment = TabInfo.values()[position].fragmentCreator()
    }
}
