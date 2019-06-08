package com.minirogue.starwarscanontracker;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.minirogue.starwarscanontracker.database.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SWMListAdapter extends BaseAdapter{


    private List<MediaAndNotes> currentList = new ArrayList<>();
    private ConcurrentHashMap<String, Drawable> cachedImgs = new ConcurrentHashMap<>();

    private MediaListViewModel mediaListViewModel;

    SWMListAdapter(MediaListViewModel mediaListViewModel){
        this.mediaListViewModel = mediaListViewModel;
    }

    public void setList(List<MediaAndNotes> currentList) {
        this.currentList = currentList;
        //TODO running the following thread precaches the images, which looks nice, but the iteration
        // in cacheCoverArt() is NOT thread safe :(
        //new Thread(this::cacheCoverArt).start();
        notifyDataSetChanged();
    }

    private void cacheCoverArt(){
        for (MediaAndNotes mediaAndNotes : currentList){
            String url = mediaAndNotes.mediaItem.imageURL;
            if (url != null && !cachedImgs.containsKey(url)) {
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
        coverImage.setTag(currentItem);
        new SetImageViewFromURL(coverImage, currentItem).execute(currentItem.mediaItem.imageURL);

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
        WeakReference<ImageView> imgRef;
        MediaAndNotes object;

        SetImageViewFromURL(ImageView imgView, MediaAndNotes object){
            this.imgRef = new WeakReference<>(imgView);
            this.object = object;
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            if (strings[0] == null){
                return mediaListViewModel.getApplication().getDrawable(R.mipmap.ic_launcher);
            }
            if (!cachedImgs.containsKey(strings[0])){
                publishProgress(mediaListViewModel.getApplication().getDrawable(R.mipmap.ic_launcher));
                cachedImgs.put(strings[0], mediaListViewModel.getCoverImageFromURL(strings[0]));
                return cachedImgs.get(strings[0]);
            }else{
                return cachedImgs.get(strings[0]);
            }
        }

        @Override
        protected void onProgressUpdate(Drawable... values) {
            super.onProgressUpdate(values);
            ImageView imgView = imgRef.get();
            if (imgView != null && values[0] != null) {
                imgView.setImageDrawable(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Drawable aBitmap) {
            ImageView imgView = imgRef.get();
            if (aBitmap != null && imgView != null && (imgView.getTag() == object)){
                imgView.setImageDrawable(aBitmap);
            }
        }
    }
}
