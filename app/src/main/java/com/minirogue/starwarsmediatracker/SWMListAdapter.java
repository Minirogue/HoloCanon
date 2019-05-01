package com.minirogue.starwarsmediatracker;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minirogue.starwarsmediatracker.database.*;

import java.util.ArrayList;
import java.util.List;

class SWMListAdapter extends BaseAdapter{


    private List<MediaItem> currentList = new ArrayList<>();


    public SWMListAdapter(){

    }

    public void setList(List<MediaItem> currentList) {
        this.currentList = currentList;
        notifyDataSetChanged();
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
