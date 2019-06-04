package com.minirogue.starwarsmediatracker;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.minirogue.starwarsmediatracker.database.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class SWMListAdapter extends BaseAdapter{


    private List<MediaAndNotes> currentList = new ArrayList<>();
    private HashMap<String, Drawable> cachedImgs = new HashMap<>();

    private MediaListViewModel mediaListViewModel;

    SWMListAdapter(MediaListViewModel mediaListViewModel){
        this.mediaListViewModel = mediaListViewModel;
    }

    public void setList(List<MediaAndNotes> currentList) {
        this.currentList = currentList;
        new Thread(this::cacheCoverArt).start();
        notifyDataSetChanged();
    }

    private void cacheCoverArt(){
        for (MediaAndNotes mediaAndNotes : currentList){
            String url = mediaAndNotes.mediaItem.imageURL;
            if (!cachedImgs.containsKey(url)) {
                cachedImgs.put(url, mediaListViewModel.getCoverImageFromURL(url));
            }
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
        return currentList.get(position).mediaItem.id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("Adapter", "getView called on "+getItem(position));
        if (convertView == null){
            //Log.d("Adapter", "convertView was null");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.media_list_item, parent, false);
            ((TextView)convertView.findViewById(R.id.text_watched_or_read)).setText(mediaListViewModel.getCheckboxText(1));
            ((TextView)convertView.findViewById(R.id.text_want_to_watch_or_read)).setText(mediaListViewModel.getCheckboxText(2));
            ((TextView)convertView.findViewById(R.id.text_owned)).setText(mediaListViewModel.getCheckboxText(3));
        }
        TextView titleTextView = convertView.findViewById(R.id.media_title);
        TextView typeTextView = convertView.findViewById(R.id.media_type);
        CheckBox checkBoxWatchedRead = convertView.findViewById(R.id.checkbox_watched_or_read);
        CheckBox checkBoxWantToWatchRead = convertView.findViewById(R.id.checkbox_want_to_watch_or_read);
        CheckBox checkBoxOwned = convertView.findViewById(R.id.checkbox_owned);
        ImageView coverImage = convertView.findViewById(R.id.image_cover);

        MediaAndNotes currentItem = currentList.get(position);
        titleTextView.setText(currentItem.mediaItem.title);
        typeTextView.setText(mediaListViewModel.convertTypeToString(currentItem.mediaItem.type));
        new SetImageViewFromURL(coverImage).execute(currentItem.mediaItem.imageURL);

        checkBoxWatchedRead.setChecked(currentItem.mediaNotes.isWatchedRead());
        checkBoxWantToWatchRead.setChecked(currentItem.mediaNotes.isWantToWatchRead());
        checkBoxOwned.setChecked(currentItem.mediaNotes.isOwned());

        checkBoxWatchedRead.setTag(currentItem.mediaNotes);
        checkBoxWantToWatchRead.setTag(currentItem.mediaNotes);
        checkBoxOwned.setTag(currentItem.mediaNotes);

        checkBoxOwned.setOnClickListener(view -> {
            ((MediaNotes)view.getTag()).flipOwned();
            mediaListViewModel.update((MediaNotes)view.getTag());
        });
        checkBoxWatchedRead.setOnClickListener(view -> {
            ((MediaNotes)view.getTag()).flipWatchedRead();
            mediaListViewModel.update((MediaNotes)view.getTag());
        });
        checkBoxWantToWatchRead.setOnClickListener(view -> {
            ((MediaNotes)view.getTag()).flipWantToWatchRead();
            mediaListViewModel.update((MediaNotes)view.getTag());
        });


        return convertView;
    }

    private class SetImageViewFromURL extends AsyncTask<String, Drawable, Drawable>{
        WeakReference<ImageView> imgView;

        SetImageViewFromURL(ImageView imgView){
            this.imgView = new WeakReference<>(imgView);
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            if (!cachedImgs.containsKey(strings[0])){
                publishProgress(mediaListViewModel.getApplication().getDrawable(R.mipmap.ic_launcher));
                cachedImgs.put(strings[0], mediaListViewModel.getCoverImageFromURL(strings[0]));
            }
            return cachedImgs.get(strings[0]);
        }

        @Override
        protected void onProgressUpdate(Drawable... values) {
            super.onProgressUpdate(values);
            imgView.get().setImageDrawable(values[0]);
        }

        @Override
        protected void onPostExecute(Drawable aBitmap) {
            if (aBitmap != null){
                imgView.get().setImageDrawable(aBitmap);
            }
        }
    }
}
