package com.minirogue.starwarsmediatracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.minirogue.starwarsmediatracker.database.MediaItem;

public class ListMediaActivity extends AppCompatActivity {

    private ListView listView;
    private SWMListAdapter adapter;
    private Spinner typeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_by_type);

        listView = findViewById(R.id.media_by_type_listview);
        adapter = new SWMListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ViewMediaItemActivity.class);
                intent.putExtra("media_id", adapter.getItemId(i));
                startActivity(intent);
            }
        });

        typeSpinner = findViewById(R.id.media_type_spinner);
        String[] typeArray = new String[]{MediaItem.convertTypeToString(MediaItem.MEDIATYPE_ANY),
                MediaItem.convertTypeToString(MediaItem.MEDIATYPE_MOVIE),
                MediaItem.convertTypeToString(MediaItem.MEDIATYPE_BOOK)};//TODO find a good way to generate this array generically
        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeArray);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setSelection(0);//TODO this will likely cause an error once the typeArray is generated a different way
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setTypeFilter(position);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



}
