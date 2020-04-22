package com.minirogue.starwarscanontracker.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication;
import com.minirogue.starwarscanontracker.model.SortStyle;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter;
import com.minirogue.starwarscanontracker.view.FilterChip;
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter;
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class MediaListFragment extends Fragment {
    private static final String TAG = "Media List";

    private SWMListAdapter adapter;
    private MediaListViewModel mediaListViewModel;
    private ChipGroup chipGroup;
    private Chip sortChip;
    private FloatingActionButton sortFAB;
    private Context ctx;

    private SWMListAdapter.AdapterInterface adapterInterface = new SWMListAdapter.AdapterInterface() {
        @Override
        public void onItemClicked(int itemId) {
            ViewMediaItemFragment viewMediaItemFragment = new ViewMediaItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.bundleItemId), itemId);
            viewMediaItemFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, viewMediaItemFragment)
                    .addToBackStack(null)
                    .commit();//TODO this should be handled by the activity
        }

        @Override
        public void onCheckbox1Clicked(MediaNotes mediaNotes) {
            mediaNotes.flipCheck1();
            mediaListViewModel.update(mediaNotes);
        }

        @Override
        public void onCheckbox2Clicked(MediaNotes mediaNotes) {
            mediaNotes.flipCheck2();
            mediaListViewModel.update(mediaNotes);
        }

        @Override
        public void onCheckbox3Clicked(MediaNotes mediaNotes) {
            mediaNotes.flipCheck3();
            mediaListViewModel.update(mediaNotes);
        }

        @NotNull
        @Override
        public String getMediaTypeString(int mediaTypeId) {
            return mediaListViewModel.convertTypeToString(mediaTypeId);
        }

        @NotNull
        @Override
        public String getSeriesString(int seriesId) {
            //TODO
            return "Series not found";
        }

        @Override
        public boolean isNetworkAllowed() {
            return mediaListViewModel.isNetworkAllowed();
        }
    };

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_media_list, container, false);
        ctx = fragmentView.getContext();
        ((CanonTrackerApplication) getActivity().getApplication()).appComponent.inject(this);
        mediaListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MediaListViewModel.class);
        mediaListViewModel.getFilteredMediaAndNotes().observe(getViewLifecycleOwner(), mediaAndNotes -> adapter.submitList((mediaAndNotes)));

        RecyclerView recyclerView = fragmentView.findViewById(R.id.media_recyclerview);
        chipGroup = fragmentView.findViewById(R.id.filter_chip_group);
        makeCurrentSortChip();
        sortFAB = fragmentView.findViewById(R.id.sort_floating_action_button);
        PopupMenu sortMenu = makeSortMenu();
        sortFAB.setOnClickListener(view -> sortMenu.show());


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        adapter = new SWMListAdapter(adapterInterface);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {//Scroll down
                    sortFAB.hide();
                } else if (dy < 0) {//Scroll up
                    sortFAB.show();
                }
            }
        });

        new FastScrollerBuilder(recyclerView).build();

        mediaListViewModel.getCheckBoxText().observe(getViewLifecycleOwner(), adapter::updateCheckBoxText);
        mediaListViewModel.getCheckBoxVisibility().observe(getViewLifecycleOwner(), adapter::updateCheckBoxVisible);
        mediaListViewModel.getActiveFilters().observe(getViewLifecycleOwner(), this::setFilterChips);

        return fragmentView;
    }

    private PopupMenu makeSortMenu() {
//        mediaListViewModel.toggleSort();
        PopupMenu newSortMenu;
        newSortMenu = new PopupMenu(ctx, sortFAB, Gravity.END);
        for (int style : SortStyle.Companion.getAllStyles()) {
            newSortMenu.getMenu().add(0, style, 0, SortStyle.Companion.getSortText(style));
        }
        newSortMenu.setOnMenuItemClickListener(menuItem -> {
            mediaListViewModel.setSort(menuItem.getItemId());
            return true;
        });
        return newSortMenu;
    }

    private void setFilterChips(List<FullFilter> filters) {
        chipGroup.removeAllViews();
        chipGroup.addView(sortChip);
        for (FullFilter filter : filters) {
            chipGroup.addView(makeCurrentFilterChip(filter));
        }
    }

    private Chip makeCurrentFilterChip(final FullFilter fullFilter) {
        Chip filterChip = new FilterChip(fullFilter, ctx);
        filterChip.setOnCloseIconClickListener(view -> mediaListViewModel.deactivateFilter(fullFilter.getFilterObject()));
        return filterChip;
    }

    private void makeCurrentSortChip() {
        sortChip = new Chip(ctx);
        sortChip.setChipIconVisible(true);
        sortChip.setOnClickListener(view -> mediaListViewModel.reverseSort());
        mediaListViewModel.getSortStyle().observe(getViewLifecycleOwner(), this::updateSortChip);
        chipGroup.addView(sortChip);
    }

    private void updateSortChip(SortStyle sortStyle) {
        sortChip.setText(sortStyle.getText());
        if (sortStyle.isAscending()) {
            sortChip.setChipIcon(getResources().getDrawable(R.drawable.ic_ascending_sort));
        } else {
            sortChip.setChipIcon(getResources().getDrawable(R.drawable.ic_descending_sort));
        }
    }
}


