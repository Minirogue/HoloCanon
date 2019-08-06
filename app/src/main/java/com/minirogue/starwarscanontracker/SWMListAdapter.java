package com.minirogue.starwarscanontracker;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.minirogue.starwarscanontracker.database.*;

import java.util.ArrayList;
import java.util.List;

class SWMListAdapter extends RecyclerView.Adapter<SWMListAdapter.MediaViewHolder> {

    private final String TAG = "Adapter";

    private List<MediaAndNotes> currentList = new ArrayList<>();
    private OnItemClickedListener listener;
    private MediaListViewModel mediaListViewModel;

    SWMListAdapter(MediaListViewModel mediaListViewModel){
        this.mediaListViewModel = mediaListViewModel;
    }

    void setOnItemClickedListener(OnItemClickedListener newListener){
        listener = newListener;
    }

    interface OnItemClickedListener {
        void onItemClicked(int itemId);
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_list_item, parent, false);
        ((TextView)itemView.findViewById(R.id.text_watched_or_read)).setText(mediaListViewModel.getCheckboxText(0));
        ((TextView)itemView.findViewById(R.id.text_want_to_watch_or_read)).setText(mediaListViewModel.getCheckboxText(1));
        ((TextView)itemView.findViewById(R.id.text_owned)).setText(mediaListViewModel.getCheckboxText(2));
        return new MediaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaAndNotes currentItem = currentList.get(position);
        holder.itemView.setOnClickListener(view -> listener.onItemClicked(currentItem.mediaItem.id));
        holder.titleTextView.setText(currentItem.mediaItem.title);
        holder.typeTextView.setText(mediaListViewModel.convertTypeToString(currentItem.mediaItem.type));
        holder.coverImage.getHierarchy().setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE);
        String uriString = currentItem.mediaItem.imageURL;
        if (uriString != null && !uriString.equals("")) {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(currentItem.mediaItem.imageURL))
                    .setLowestPermittedRequestLevel(mediaListViewModel.isNetworkAllowed() ? ImageRequest.RequestLevel.FULL_FETCH : ImageRequest.RequestLevel.DISK_CACHE)
                    .build();
            holder.coverImage.setImageRequest(request);
            holder.coverImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        }else {
            holder.coverImage.setActualImageResource(R.drawable.ic_launcher_foreground);
            holder.coverImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        }

        holder.checkBoxWatchedRead.setChecked(currentItem.mediaNotes.isWatchedRead());
        holder.checkBoxWantToWatchRead.setChecked(currentItem.mediaNotes.isWantToWatchRead());
        holder.checkBoxOwned.setChecked(currentItem.mediaNotes.isOwned());

        holder.checkBoxWatchedRead.setTag(currentItem.mediaNotes);
        holder.checkBoxWantToWatchRead.setTag(currentItem.mediaNotes);
        holder.checkBoxOwned.setTag(currentItem.mediaNotes);

        holder.checkBoxOwned.setOnClickListener(view -> {
            ((MediaNotes)view.getTag()).flipOwned();
            mediaListViewModel.update((MediaNotes)view.getTag());
        });
        holder.checkBoxWatchedRead.setOnClickListener(view -> {
            ((MediaNotes)view.getTag()).flipWatchedRead();
            mediaListViewModel.update((MediaNotes)view.getTag());
        });
        holder.checkBoxWantToWatchRead.setOnClickListener(view -> {
            ((MediaNotes)view.getTag()).flipWantToWatchRead();
            mediaListViewModel.update((MediaNotes)view.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    public void setList(List<MediaAndNotes> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(this.currentList, newList));
        diffResult.dispatchUpdatesTo(this);
        currentList.clear();
        currentList.addAll(newList);
    }

    @Override
    public long getItemId(int position) {
        return currentList.get(position).mediaItem.id;
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView typeTextView;
        CheckBox checkBoxWatchedRead;
        CheckBox checkBoxWantToWatchRead;
        CheckBox checkBoxOwned;
        SimpleDraweeView coverImage;


        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.media_title);
            typeTextView = itemView.findViewById(R.id.media_type);
            checkBoxWatchedRead = itemView.findViewById(R.id.checkbox_watched_or_read);
            checkBoxWantToWatchRead = itemView.findViewById(R.id.checkbox_want_to_watch_or_read);
            checkBoxOwned = itemView.findViewById(R.id.checkbox_owned);
            coverImage = itemView.findViewById(R.id.image_cover);
        }
    }

    static class DiffCallback extends DiffUtil.Callback{

        List<MediaAndNotes> oldList;
        List<MediaAndNotes> newList;


        DiffCallback(List<MediaAndNotes> oldList, List<MediaAndNotes> newList){
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return this.oldList.size();
        }

        @Override
        public int getNewListSize() {
            return this.newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).mediaItem.id == newList.get(newItemPosition).mediaItem.id;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            MediaItem oldMedia = oldList.get(oldItemPosition).mediaItem;
            MediaItem newMedia = newList.get(newItemPosition).mediaItem;
            MediaNotes oldNotes = oldList.get(oldItemPosition).mediaNotes;
            MediaNotes newNotes = newList.get(newItemPosition).mediaNotes;
            boolean same = true;
            if (oldNotes == null && newNotes!= null){
                return false;
            }else if (oldNotes != null && newNotes != null){
                same = (oldNotes.isOwned() == newNotes.isOwned() &&
                        oldNotes.isWantToWatchRead() == newNotes.isWantToWatchRead() &&
                        oldNotes.isWatchedRead() == newNotes.isWatchedRead());
            }
            same &= (oldMedia.imageURL == null ? newMedia.imageURL == null : oldMedia.imageURL.equals(newMedia.imageURL) &&
                    oldMedia.title == null ? newMedia.title == null :oldMedia.title.equals(newMedia.title) &&
                    oldMedia.type == newMedia.type);
            return same;

//                    oldMedia.date.equals(newMedia.date) &&
//                    oldMedia.timeline == (newMedia.timeline)
        }
    }

}
