package com.minirogue.starwarscanontracker;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.minirogue.starwarscanontracker.database.*;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SWMListAdapter extends BaseAdapter{

    private final String TAG = "Adapter";

    private List<MediaAndNotes> currentList = new ArrayList<>();
    private ConcurrentHashMap<String, Bitmap> cachedImgs = new ConcurrentHashMap<>();
    private MediaListViewModel mediaListViewModel;
    private Thread cacheThread;

    SWMListAdapter(MediaListViewModel mediaListViewModel){
        this.mediaListViewModel = mediaListViewModel;
        cacheThread = new Thread(this::manageCache);
        cacheThread.start();
    }

    public void setList(List<MediaAndNotes> currentList) {
        this.currentList = currentList;
        cachedImgs.clear();
        notifyDataSetChanged();
    }

    private void manageCache(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) mediaListViewModel.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        while (true){
            manager.getMemoryInfo(memoryInfo);
            if (memoryInfo.lowMemory){
                Log.d(TAG, "Low Memory, dumping image cache");
                cachedImgs.clear();
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
//        ImageView coverImage = convertView.findViewById(R.id.image_cover);
        SimpleDraweeView coverImage = convertView.findViewById(R.id.image_cover);

        MediaAndNotes currentItem = currentList.get(position);
        titleTextView.setText(currentItem.mediaItem.title);
        typeTextView.setText(mediaListViewModel.convertTypeToString(currentItem.mediaItem.type));
//        coverImage.setTag(currentItem);
//            new SetImageViewFromURL(coverImage, currentItem).execute(currentItem.mediaItem.imageURL);
        coverImage.getHierarchy().setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE);
        ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(currentItem.mediaItem.imageURL))
                    .setLowestPermittedRequestLevel(mediaListViewModel.isNetworkMetered() ? ImageRequest.RequestLevel.DISK_CACHE : ImageRequest.RequestLevel.FULL_FETCH)
                    .build();
            coverImage.setImageRequest(request);
            coverImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);

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

    private class SetImageViewFromURL extends AsyncTask<String, Bitmap, Bitmap>{
        WeakReference<ImageView> imgRef;
        MediaAndNotes object;

        SetImageViewFromURL(ImageView imgView, MediaAndNotes object){
            this.imgRef = new WeakReference<>(imgView);
            this.object = object;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            if (strings[0] == null){
                return null;
            }
            if (!cachedImgs.containsKey(strings[0])){
                publishProgress(null);
                ImageView imgView = imgRef.get();
                Bitmap thumbnail = null;
                if (imgView != null) {
                    thumbnail = mediaListViewModel.getCoverImageFromURL(strings[0], imgView.getHeight(), imgView.getWidth());
                }
                if (thumbnail != null){
                    cachedImgs.put(strings[0], thumbnail);
                }
                return thumbnail;
            }else{
                return cachedImgs.get(strings[0]);
            }
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            ImageView imgView = imgRef.get();
            if (imgView != null) {
                imgView.setImageDrawable(ContextCompat.getDrawable(imgView.getContext(), R.drawable.ic_launcher_foreground));
            }
        }

        @Override
        protected void onPostExecute(Bitmap aBitmap) {
            ImageView imgView = imgRef.get();
            if (aBitmap != null && imgView != null && (imgView.getTag() == object)){
                imgView.setImageBitmap(aBitmap);
            }
        }
    }
}
