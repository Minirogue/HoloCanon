package com.minirogue.starwarsmediatracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.minirogue.starwarsmediatracker.database.Character;
import com.minirogue.starwarsmediatracker.database.MediaDatabase;
import com.minirogue.starwarsmediatracker.database.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class ListMediaActivity extends AppCompatActivity {

    private MediaDatabase db;
    private ListView listView;
    private SWMListAdapter adapter;
    private Spinner typeSpinner;
    private Spinner characterSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_by_type);

        db = MediaDatabase.getMediaDataBase(this);

        listView = findViewById(R.id.media_by_type_listview);
        adapter = new SWMListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ViewMediaItemActivity.class);
                intent.putExtra(getString(R.string.intentMediaID), (int)adapter.getItemId(i));
                Log.d("ListMedia", "Put "+adapter.getItemId(i)+" in intent");
                startActivity(intent);
            }
        });
        setTypeSpinner();
        new setCharacterSpinner().execute();

    }



    private void setTypeSpinner(){
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private class setCharacterSpinner extends AsyncTask<Void, Void, List<Character>> {

        @Override
        protected void onPreExecute() {
            characterSpinner = findViewById(R.id.character_spinner);
        }


        @Override
        protected List<Character> doInBackground(Void... voids){
            return db.getDaoCharacter().getAllCharacters();
        }

        @Override
        protected void onPostExecute(List<Character> characterArray) {
            final ArrayAdapter<Character> characterSpinnerAdapter = new ArrayAdapter<>(characterSpinner.getContext() ,android.R.layout.simple_spinner_item, characterArray);
            characterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Character blankChar = Character.getBlank();
            characterSpinnerAdapter.add(blankChar);
            characterSpinner.setAdapter(characterSpinnerAdapter);
            characterSpinner.setSelection(characterSpinnerAdapter.getPosition(blankChar));
            characterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    adapter.setCharacterFilter(characterSpinnerAdapter.getItem(i).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
}
