package com.minirogue.starwarsmediatracker;

import android.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.minirogue.starwarsmediatracker.database.MediaAndNotes;

import java.util.List;

public class ListMediaActivity extends AppCompatActivity {

    private ListView listView;
    private SWMListAdapter adapter;
    private ListMediaViewModel mediaListViewModel;
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_by_type);

        mediaListViewModel = ViewModelProviders.of(this).get(ListMediaViewModel.class);
        mediaListViewModel.getFilteredMediaAndNotes().observe(this, new Observer<List<MediaAndNotes>>() {
            @Override
            public void onChanged(@Nullable List<MediaAndNotes> mediaAndNotes) {
                Log.d("OBSERVER", "filters: "+mediaListViewModel.getFilters());
                Log.d("OBSERVER", "List length "+mediaAndNotes.size());
                adapter.setList(mediaAndNotes);
            }
        });

        listView = findViewById(R.id.media_by_type_listview);
        chipGroup = findViewById(R.id.filter_chip_group);
        for (FilterObject filter : mediaListViewModel.getFilters().getValue()){
            makeFilterChip(filter);
        }

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
    }

    public void selectFilters(View view){
        List<FilterObject> allFilters = mediaListViewModel.getAllFilters();
        final List<FilterObject> currentFilters = mediaListViewModel.getFilters().getValue();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Filters");
        final ChipGroup filterChips = new ChipGroup(this);
        for (final FilterObject filter : allFilters){
            Chip filterChip = new Chip(this);
            filterChip.setText(filter.displayText);
            filterChip.setCheckable(true);
            filterChip.setCheckedIconVisible(true);
            filterChip.setChecked(currentFilters.contains(filter));
            filterChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        currentFilters.add(filter);
                    } else{
                        currentFilters.remove(filter);
                    }
                }
            });
            filterChips.addView(filterChip);
        }
        builder.setView(filterChips);
        builder.setPositiveButton("Set Filters", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    mediaListViewModel.setFilters(currentFilters);
                    chipGroup.removeAllViews();
                    for (FilterObject newFilter : currentFilters){
                        makeFilterChip(newFilter);
                    }
                }
            });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void makeFilterChip(final FilterObject filter){
        final Chip filterChip = new Chip(this);
        filterChip.setText(filter.displayText);
        filterChip.setCloseIcon(getResources().getDrawable(R.drawable.ic_close));//TODO deprecated by getDrawable(int, Theme) in later apis
        filterChip.setCloseIconVisible(true);
        filterChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaListViewModel.removeFilter(filter);
                chipGroup.removeView(filterChip);
            }
        });
        chipGroup.addView(filterChip);
    }
}


