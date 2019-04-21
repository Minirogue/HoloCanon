package com.minirogue.starwarsmediatracker;

import android.content.Context;
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

    private MediaDatabase db;
    private List<MediaItem> currentList;
    private int typeFilter;


    public SWMListAdapter(Context ctx){
        db = MediaDatabase.getMediaDataBase(ctx);
        setTypeFilter(MediaItem.MEDIATYPE_ANY);
    }

    public void setTypeFilter(int type) {
        typeFilter = type;
        updateList();
    }

    private void updateList(){//TODO update DaoAccess to query based on multiple filters
        if (typeFilter == MediaItem.MEDIATYPE_ANY) {
            currentList = db.daoAccess().getAll();
        }
        else{
            currentList = db.daoAccess().getMediaByType(typeFilter);
        }
        notifyDataSetChanged();
        Log.d("SWMListAdapter","List updated to "+currentList.toString());
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
        return currentList.get(position).getMediaID();
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
