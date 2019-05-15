package com.minirogue.starwarsmediatracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.arch.core.util.Function;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.minirogue.starwarsmediatracker.database.*;

import java.util.ArrayList;
import java.util.List;

class SWMListAdapter extends BaseAdapter{


    private List<MediaAndNotes> currentList = new ArrayList<>();

    private ListMediaViewModel listMediaViewModel;

    public SWMListAdapter(ListMediaViewModel listMediaViewModel){
        this.listMediaViewModel = listMediaViewModel;
    }

    public void setList(List<MediaAndNotes> currentList) {
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
        return currentList.get(position).mediaItem.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("Adapter", "getView called on "+getItem(position));
        if (convertView == null){
            //Log.d("Adapter", "convertView was null");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.media_list_item, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.media_title);
        TextView typeTextView = convertView.findViewById(R.id.media_type);
        CheckBox checkBoxWatchedRead = convertView.findViewById(R.id.checkbox_watched_or_read);
        CheckBox checkBoxWantToWatchRead = convertView.findViewById(R.id.checkbox_want_to_watch_or_read);
        CheckBox checkBoxOwned = convertView.findViewById(R.id.checkbox_owned);

        MediaAndNotes currentItem = currentList.get(position);
        titleTextView.setText(currentItem.mediaItem.getTitle());
        typeTextView.setText(listMediaViewModel.convertTypeToString(currentItem.mediaItem.getType()));

        checkBoxWatchedRead.setChecked(currentItem.mediaNotes.isWatchedRead());
        checkBoxWantToWatchRead.setChecked(currentItem.mediaNotes.isWantToWatchRead());
        checkBoxOwned.setChecked(currentItem.mediaNotes.isOwned());

        checkBoxWatchedRead.setTag(currentItem.mediaNotes);
        checkBoxWantToWatchRead.setTag(currentItem.mediaNotes);
        checkBoxOwned.setTag(currentItem.mediaNotes);

        checkBoxOwned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MediaNotes)view.getTag()).flipOwned();
                listMediaViewModel.update((MediaNotes)view.getTag());
            }
        });
        checkBoxWatchedRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MediaNotes)view.getTag()).flipWatchedRead();
                listMediaViewModel.update((MediaNotes)view.getTag());
            }
        });
        checkBoxWantToWatchRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MediaNotes)view.getTag()).flipWantToWatchRead();
                listMediaViewModel.update((MediaNotes)view.getTag());
            }
        });


        return convertView;
    }
}
