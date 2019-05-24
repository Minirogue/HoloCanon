package com.minirogue.starwarsmediatracker;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minirogue.starwarsmediatracker.database.MediaAndNotes;

import java.util.List;

public class MediaListFragment extends Fragment {

    private ListView listView;
    private SWMListAdapter adapter;
    private ListMediaViewModel mediaListViewModel;
    private ChipGroup chipGroup;
    private Context ctx;

    //TODO too much on UI when initializing?
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_media_list, container, false);
        mediaListViewModel = ViewModelProviders.of(this).get(ListMediaViewModel.class);
        mediaListViewModel.getFilteredMediaAndNotes().observe(this, new Observer<List<MediaAndNotes>>() {
            @Override
            public void onChanged(@Nullable List<MediaAndNotes> mediaAndNotes) {
                Log.d("OBSERVER", "filters: " + mediaListViewModel.getFilters());
                Log.d("OBSERVER", "List length " + mediaAndNotes.size());
                adapter.setList(mediaAndNotes);
            }
        });

        listView = fragmentView.findViewById(R.id.media_by_type_listview);
        chipGroup = fragmentView.findViewById(R.id.filter_chip_group);
        ctx = getActivity();

        adapter = new SWMListAdapter(mediaListViewModel);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ViewMediaItemActivity.class);
                intent.putExtra(getString(R.string.intentMediaID), (int) adapter.getItemId(i));
                Log.d("ListMedia", "Put " + adapter.getItemId(i) + " in intent");
                startActivity(intent);
            }
        });
        mediaListViewModel.getFilters().observe(this, new Observer<List<FilterObject>>() {
            @Override
            public void onChanged(List<FilterObject> filterObjects) {
                fillChipGroup(filterObjects);
            }
        });

        FloatingActionButton floatingActionButton = fragmentView.findViewById(R.id.filter_floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFilters();
            }
        });

        return fragmentView;
    }


    public void selectFilters() {
        LiveData<List<FilterObject>> allFilters = mediaListViewModel.getAllFilters();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Filters");
        final ChipGroup filterChips = new ChipGroup(ctx);
        allFilters.observe(this, new Observer<List<FilterObject>>() {
            @Override
            public void onChanged(List<FilterObject> filterObjects) {
                filterChips.removeAllViews();
                for (FilterObject filter : filterObjects) {
                    filterChips.addView(makeSelectableFilterChip(filter));
                }
            }
        });


        builder.setView(filterChips);
        builder.setPositiveButton("Set Filters", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    /*mediaListViewModel.setFilters(currentFilters);
                    chipGroup.removeAllViews();
                    for (FilterObject newFilter : currentFilters){
                        makeFilterChip(newFilter);
                    }*/
                dialog.dismiss();
            }
        });
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
        for (FilterObject filter : filters) {
            makeCurrentFilterChip(filter);
        }
    }

    private void makeCurrentFilterChip(final FilterObject filter) {
        final Chip filterChip = new Chip(ctx);
        filterChip.setText(filter.displayText);
        filter.getLiveFilter().observe(this, new Observer<FilterObject>() {
            @Override
            public void onChanged(FilterObject filterObject) {
                filterChip.setChipIcon(filter.isPositive() ? getResources().getDrawable(R.drawable.ic_filter_check) : getResources().getDrawable(R.drawable.ic_filter_x));
            }
        });
        filterChip.setChipIconVisible(true);
        filterChip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close));//TODO deprecated by getDrawable(int, Theme) in later apis
        filterChip.setCloseIconVisible(true);
        filterChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaListViewModel.removeFilter(filter);
            }
        });
        chipGroup.addView(filterChip);
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
        filter.getLiveFilter().observe(this, new Observer<FilterObject>() {
            @Override
            public void onChanged(FilterObject filterObject) {
                filterChip.setChipIcon(filter.isPositive() ? getResources().getDrawable(R.drawable.ic_filter_check) : getResources().getDrawable(R.drawable.ic_filter_x));
            }
        });
        filterChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        return filterChip;
    }
}


