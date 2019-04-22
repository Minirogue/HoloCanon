package com.minirogue.starwarsmediatracker;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minirogue.starwarsmediatracker.database.*;
import com.minirogue.starwarsmediatracker.database.Character;

import java.util.ArrayList;
import java.util.List;

class SWMListAdapter extends BaseAdapter{

    private MediaDatabase db;
    private List<MediaItem> currentList;
    private int typeFilter;
    private int characterFilter;


    public SWMListAdapter(Context ctx){
        db = MediaDatabase.getMediaDataBase(ctx);
        // If currentList is not initialized here, then asynchronous db querying could cause a null pointer exception
        currentList = new ArrayList();
        setCharacterFilter(-1);
        setTypeFilter(MediaItem.MEDIATYPE_ANY);
    }

    public void setTypeFilter(int type) {
        typeFilter = type;
        new updateList().execute();
    }

    public void setCharacterFilter(int charID) {
        characterFilter = charID;
        new updateList().execute();
    }

    private class updateList extends AsyncTask<Void, Void, Void> {//TODO update DaoMedia to query based on multiple filters

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder queryBuild = new StringBuilder();
            queryBuild.append("SELECT media_items.* FROM media_items");
            boolean filterapplied = false;
            if (characterFilter != -1){
                queryBuild.append(" INNER JOIN media_character_join ON media_items.id = media_character_join.mediaId");
                if (!filterapplied){
                    queryBuild.append(" WHERE");
                    filterapplied = true;
                } else{
                    queryBuild.append(" AND");
                }
                queryBuild.append(" media_character_join.characterID = ");
                queryBuild.append(characterFilter);
            }
            if (typeFilter != MediaItem.MEDIATYPE_ANY) {
                if (!filterapplied){
                    queryBuild.append(" WHERE");
                    filterapplied = true;
                } else{
                    queryBuild.append(" AND");
                }
                queryBuild.append(" type = ");
                queryBuild.append((typeFilter));
            }
            Log.d("ListAdapter", queryBuild.toString());
            currentList = db.getDaoMedia().getMediaFromRawQuery(new SimpleSQLiteQuery(queryBuild.toString()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
            Log.d("SWMListAdapter","List updated to "+currentList.toString());
        }
    }


    @Override
    public int getCount() {
        return currentList.size();
    }

    @Override
    public Object getItem(int position) {
        return currentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return currentList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Adapter", "getView called on "+getItem(position));
        if (convertView == null){
            Log.d("Adapter", "convertView was null");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        MediaItem currentItem = currentList.get(position);
        text1.setText(currentItem.getTitle());
        text2.setText(MediaItem.convertTypeToString(currentItem.getType()));
        return convertView;
    }
}
