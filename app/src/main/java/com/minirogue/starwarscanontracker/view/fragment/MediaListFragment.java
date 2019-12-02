package com.minirogue.starwarscanontracker.view.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter;
import com.minirogue.starwarscanontracker.view.FilterChip;
import com.minirogue.starwarscanontracker.view.activity.MainActivity;
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter;
import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.model.SortStyle;
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel;

import java.util.List;
import java.util.Objects;

public class MediaListFragment extends Fragment {
    private static final String TAG = "Media List";

    private SWMListAdapter adapter;
    private MediaListViewModel mediaListViewModel;
    private ChipGroup chipGroup;
    private Chip sortChip;
    private FloatingActionButton sortFAB;
    private FloatingActionButton filterFAB;
    private Context ctx;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ctx = this.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_media_list, container, false);
        Log.d(TAG, "onCreateView: getting viewmodel");
        mediaListViewModel = ViewModelProviders.of(this).get(MediaListViewModel.class);
        Log.d(TAG, "onCreateView: viewmodel gotten");
        mediaListViewModel.getFilteredMediaAndNotes().observe(getViewLifecycleOwner(), mediaAndNotes -> adapter.submitList((mediaAndNotes)));

        RecyclerView recyclerView = fragmentView.findViewById(R.id.media_recyclerview);
        chipGroup = fragmentView.findViewById(R.id.filter_chip_group);
        makeCurrentSortChip();
        sortFAB = fragmentView.findViewById(R.id.sort_floating_action_button);
        PopupMenu sortMenu = makeSortMenu();
        sortFAB.setOnClickListener(view -> {
            sortMenu.show();
        });
        filterFAB = fragmentView.findViewById(R.id.filter_floating_action_button);
        filterFAB.setOnClickListener(stuff -> ((MainActivity) getActivity()).replaceFragment(MainActivity.FILTERS_TAG));


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        adapter = new SWMListAdapter(mediaListViewModel);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);
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
                    filterFAB.hide();
                } else if (dy < 0) {//Scroll up
                    sortFAB.show();
                    filterFAB.show();
                }
            }
        });

        adapter.setOnItemClickedListener(itemId -> {
            ViewMediaItemFragment viewMediaItemFragment = new ViewMediaItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.bundleItemId), itemId);
            viewMediaItemFragment.setArguments(bundle);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, viewMediaItemFragment)
                    .addToBackStack(null)
                    .commit();
        });
        mediaListViewModel.getCheckBoxText().observe(getViewLifecycleOwner(), adapter::updateCheckBoxText);
        mediaListViewModel.getActiveFilters().observe(getViewLifecycleOwner(), this::setFilterChips);


        return fragmentView;
    }

    private PopupMenu makeSortMenu() {
//        mediaListViewModel.toggleSort();
        PopupMenu newSortMenu;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//fix the gravity for APIs that support it
            newSortMenu = new PopupMenu(ctx, sortFAB, Gravity.END);
        } else {
            newSortMenu = new PopupMenu(ctx, sortFAB);
        }
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
        sortChip.setOnClickListener(view -> {
            mediaListViewModel.reverseSort();
        });
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


