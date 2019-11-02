package com.minirogue.starwarscanontracker.view.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.minirogue.starwarscanontracker.view.adapter.FilterSubMenuAdapter;
import com.minirogue.starwarscanontracker.view.adapter.SWMListAdapter;
import com.minirogue.starwarscanontracker.view.adapter.FilterMenuAdapter;
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject;
import com.minirogue.starwarscanontracker.model.room.entity.FilterType;
import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.model.SortStyle;
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel;

import org.jetbrains.annotations.NotNull;

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
    private PopupMenu sortMenu;
    private PopupWindow filterMenu;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ctx = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_media_list, container, false);
        Log.d(TAG, "onCreateView: getting viewmodel");
        mediaListViewModel = ViewModelProviders.of(this).get(MediaListViewModel.class);
        Log.d(TAG, "onCreateView: viewmodel gotten");
        mediaListViewModel.getFilteredMediaAndNotes().observe(getViewLifecycleOwner(), mediaAndNotes -> {
            adapter.submitList((mediaAndNotes));
        });

        RecyclerView recyclerView = fragmentView.findViewById(R.id.media_recyclerview);
        chipGroup = fragmentView.findViewById(R.id.filter_chip_group);
        makeCurrentSortChip();
        sortFAB = fragmentView.findViewById(R.id.sort_floating_action_button);
        makeSortMenu();
        sortFAB.setOnClickListener(view -> sortMenu.show());
        filterFAB = fragmentView.findViewById(R.id.filter_floating_action_button);
        makeFilterMenu(inflater);
        int[] filterFABLocation = new int[]{0,0};
        filterFAB.getLocationOnScreen(filterFABLocation);
        filterFAB.setOnClickListener(view -> filterMenu.showAtLocation(filterFAB, Gravity.BOTTOM|Gravity.END, filterFABLocation[0] + filterFAB.getLeft(), filterFABLocation[1] + filterFAB.getRight()));
        //filterFAB.setOnClickListener(view -> filterMenu.showAsDropDown(filterFAB));

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

        mediaListViewModel.getActiveFilters().observe(getViewLifecycleOwner(), this::setFilterChips);

        //fragmentView.findViewById(R.id.filter_floating_action_button).setOnClickListener(view -> selectFilters());

        return fragmentView;
    }

    private void makeSortMenu() {
//        mediaListViewModel.toggleSort();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//fix the gravity for APIs that support it
            sortMenu = new PopupMenu(ctx, sortFAB, Gravity.BOTTOM|Gravity.END);
        }else{
            sortMenu = new PopupMenu(ctx, sortFAB);
        }
        for (int style : SortStyle.Companion.getAllStyles()) {
            sortMenu.getMenu().add(0, style, 0, SortStyle.Companion.getSortText(style));
        }
        sortMenu.setOnMenuItemClickListener(menuItem -> {
            mediaListViewModel.setSort(menuItem.getItemId());
            return true;
        });
    }

    private void makeFilterMenu(LayoutInflater inflater) {
        View filterMenuView = inflater.inflate(R.layout.filter_popup_menu, null);
//        filterMenuView.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        RecyclerView recyclerView = filterMenuView.findViewById(R.id.filter_menu_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        FilterMenuAdapter filterMenuAdapter = new FilterMenuAdapter();
        recyclerView.setAdapter(filterMenuAdapter);
        mediaListViewModel.getFilterTypes().observe(getViewLifecycleOwner(), filterMenuAdapter::updateList);

        filterMenuAdapter.setOnClickListeners(new FilterMenuAdapter.OnClickListeners() {
            @Override
            public void onFilterTypeClicked(@NotNull FilterType filterType) {
                showFilterSubMenu(filterType);
            }

            @Override
            public void onFilterTypeSwitchClicked(@NotNull FilterType filterType) {
                mediaListViewModel.switchFilterType(filterType);
            }
        });

        filterMenu = new PopupWindow(filterMenuView);
        filterMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        filterMenu.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void showFilterSubMenu(FilterType filterType) {
        View filterSubMenuView = LayoutInflater.from(ctx).inflate(R.layout.filter_popup_submenu, null);

        RecyclerView recyclerView = filterSubMenuView.findViewById(R.id.filter_submenu_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        FilterSubMenuAdapter filterSubMenuAdapter = new FilterSubMenuAdapter();
        recyclerView.setAdapter(filterSubMenuAdapter);
        mediaListViewModel.getFiltersOfType(filterType.getTypeId()).observe(getViewLifecycleOwner(), filterSubMenuAdapter::updateList);

        filterSubMenuAdapter.setOnClickListener(filterObject -> mediaListViewModel.swapFilterIsActive(filterObject));

        PopupWindow filterSubMenu = new PopupWindow(filterSubMenuView);
        filterSubMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        filterSubMenu.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        filterSubMenu.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }




    private void setFilterChips(List<FilterObject> filters) {
        chipGroup.removeAllViews();
        chipGroup.addView(sortChip);
        for (FilterObject filter : filters) {
            chipGroup.addView(makeCurrentFilterChip(filter));
        }
    }

    private Chip makeCurrentFilterChip(final FilterObject filter) {
        final Chip filterChip = new Chip(ctx);
        filterChip.setText(filter.displayText);
        filterChip.setChipIconVisible(true);
        filterChip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close));
        filterChip.setCloseIconVisible(true);
        filterChip.setOnCloseIconClickListener(view -> mediaListViewModel.deactivateFilter(filter));
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


