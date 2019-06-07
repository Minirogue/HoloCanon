package com.minirogue.starwarscanontracker;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

class MediaListFragment extends Fragment {
    private static final String TAG = "Media List";

    private SWMListAdapter adapter;
    private MediaListViewModel mediaListViewModel;
    private ChipGroup chipGroup;
    private Chip sortChip;
    private FloatingActionButton sortFAB;
    private Context ctx;
    private PopupMenu sortMenu;

    //TODO too much on UI when initializing?
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_media_list, container, false);
        mediaListViewModel = ViewModelProviders.of(this).get(MediaListViewModel.class);
        mediaListViewModel.getFilteredMediaAndNotes().observe(this, mediaAndNotes -> {
            //Log.d("OBSERVER", "filters: " + mediaListViewModel.getFilters().getValue());
            //Log.d("OBSERVER", "List length " + mediaAndNotes.size());
            adapter.setList(mediaAndNotes);
        });

        ctx = getActivity();
        ListView listView = fragmentView.findViewById(R.id.media_by_type_listview);
        chipGroup = fragmentView.findViewById(R.id.filter_chip_group);
        makeCurrentSortChip();
        sortFAB = fragmentView.findViewById(R.id.sort_floating_action_button);
        makeSortMenu();
        sortFAB.setOnClickListener(view -> sortMenu.show());

        adapter = new SWMListAdapter(mediaListViewModel);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            //Log.d(TAG,"Item clicked: "+i);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ViewMediaItemFragment((int)adapter.getItemId(i)))
                    .addToBackStack(null)
                    .commit();
        });
        mediaListViewModel.getFilters().observe(this, this::fillChipGroup);

        fragmentView.findViewById(R.id.filter_floating_action_button).setOnClickListener(view -> selectFilters());

        return fragmentView;
    }

    private void makeSortMenu(){
//        mediaListViewModel.toggleSort();
        sortMenu = new PopupMenu(ctx,sortFAB);
        for (int style : SortStyle.Companion.getAllStyles()){
            sortMenu.getMenu().add(0,style,0,SortStyle.Companion.getSortText(style));
        }
        sortMenu.setOnMenuItemClickListener(menuItem -> {mediaListViewModel.setSort(menuItem.getItemId()); return true;});
    }


    private void selectFilters() {
        LiveData<List<FilterObject>> allFilters = mediaListViewModel.getAllFilters();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Filters");
        final ChipGroup filterChips = new ChipGroup(ctx);
        allFilters.observe(this, filterObjects -> {
            filterChips.removeAllViews();
            for (FilterObject filter : filterObjects) {
                filterChips.addView(makeSelectableFilterChip(filter));
            }
        });


        builder.setView(filterChips);
        builder.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());
        /*builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//TODO cancel with oncancellistener to undo changes
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/
        builder.show();
    }

    private void fillChipGroup(List<FilterObject> filters) {
        chipGroup.removeAllViews();
        chipGroup.addView(sortChip);
        for (FilterObject filter : filters) {
            makeCurrentFilterChip(filter);
        }
    }

    private void makeCurrentFilterChip(final FilterObject filter) {
        final Chip filterChip = new Chip(ctx);
        filterChip.setText(filter.displayText);
        filter.getLiveFilter().observe(this, filterObject -> filterChip.setChipIcon(filter.isPositive() ? getResources().getDrawable(R.drawable.ic_filter_check) : getResources().getDrawable(R.drawable.ic_filter_x)));
        filterChip.setChipIconVisible(true);
        filterChip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close));//TODO deprecated by getDrawable(int, Theme) in later apis
        filterChip.setCloseIconVisible(true);
        filterChip.setOnCloseIconClickListener(view -> mediaListViewModel.removeFilter(filter));
        chipGroup.addView(filterChip);
    }
    private void makeCurrentSortChip(){
        sortChip = new Chip(ctx);
        sortChip.setChipIconVisible(true);
        sortChip.setOnClickListener(view -> mediaListViewModel.reverseSort());
        mediaListViewModel.getSortStyle().observe(this, this::updateSortChip);
        chipGroup.addView(sortChip);
    }
    private void updateSortChip(SortStyle sortStyle){
        sortChip.setText(sortStyle.getText());
        if (sortStyle.isAscending()) {
            sortChip.setChipIcon(getResources().getDrawable(R.drawable.ic_ascending_sort));
        }else{
            sortChip.setChipIcon(getResources().getDrawable(R.drawable.ic_descending_sort));
        }
    }


    private Chip makeSelectableFilterChip(final FilterObject filter) {
        final Chip filterChip = new Chip(ctx);
        filterChip.setText(filter.displayText);
        if(mediaListViewModel.isCurrentFilter(filter)){
            filterChip.setChipIcon(filter.isPositive() ? getResources().getDrawable(R.drawable.ic_filter_check) : getResources().getDrawable(R.drawable.ic_filter_x));
            filterChip.setChipIconVisible(true);
        } else{
            filterChip.setChipIconVisible(false);
        }
        filter.getLiveFilter().observe(this, filterObject -> filterChip.setChipIcon(filter.isPositive() ? getResources().getDrawable(R.drawable.ic_filter_check) : getResources().getDrawable(R.drawable.ic_filter_x)));
        filterChip.setOnClickListener(view -> {
            if (mediaListViewModel.isCurrentFilter(filter)){
                if (filter.isPositive()){
                    filter.setPositive(false);
                } else{
                    mediaListViewModel.removeFilter(filter);
                    filter.setPositive(true);
                    filterChip.setChipIconVisible(false);
                }
            }
            else{
                mediaListViewModel.addFilter(filter);
                filter.setPositive(true);
                filterChip.setChipIconVisible(true);
            }
        });
        return filterChip;
    }
}


