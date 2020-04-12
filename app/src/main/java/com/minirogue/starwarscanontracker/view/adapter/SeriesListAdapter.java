package com.minirogue.starwarscanontracker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes;

import java.util.ArrayList;
import java.util.List;

public class SeriesListAdapter extends ListAdapter<MediaAndNotes, SeriesListAdapter.MediaViewHolder> {

    //private final String TAG = "adapter";

    //private AsyncListDiffer<MediaAndNotes> listDiffer = new AsyncListDiffer<>(this, DiffCallback);
    //private List<MediaAndNotes> currentList = new ArrayList<>();
    private OnItemClickedListener listener;


    public SeriesListAdapter() {
        super(DiffCallback);
    }

    public void setOnItemClickedListener(OnItemClickedListener newListener) {
        listener = newListener;
    }

    interface OnItemClickedListener {
        void onItemClicked(int itemId);
    }

    @Override
    public void submitList(@Nullable List<MediaAndNotes> list) {
        if (list != null) {
            super.submitList(new ArrayList<>(list));
        } else {
            super.submitList(null);
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seriesmedia_list_item, parent, false);
        return new MediaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaAndNotes currentItem = getItem(position);
        holder.itemView.setOnClickListener(view -> listener.onItemClicked(currentItem.mediaItem.id));
        holder.titleTextView.setText(currentItem.mediaItem.title);
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder {

        final TextView titleTextView;

        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.media_title);
        }
    }

    private static final DiffUtil.ItemCallback<MediaAndNotes> DiffCallback = new DiffUtil.ItemCallback<MediaAndNotes>() {
        @Override
        public boolean areItemsTheSame(@NonNull MediaAndNotes oldItem, @NonNull MediaAndNotes newItem) {
            return oldItem.mediaItem.id == newItem.mediaItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MediaAndNotes oldItem, @NonNull MediaAndNotes newItem) {
            MediaItem oldMedia = oldItem.mediaItem;
            MediaItem newMedia = newItem.mediaItem;
            MediaNotes oldNotes = oldItem.mediaNotes;
            MediaNotes newNotes = newItem.mediaNotes;
            boolean same = true;
            if (oldNotes == null && newNotes != null) {
                return false;
            } else if (oldNotes != null && newNotes != null) {
                same = (oldNotes.isUserChecked3() == newNotes.isUserChecked3() &&
                        oldNotes.isUserChecked2() == newNotes.isUserChecked2() &&
                        oldNotes.isUserChecked1() == newNotes.isUserChecked1());
            }
            same &= (oldMedia.imageURL == null ? newMedia.imageURL == null : oldMedia.imageURL.equals(newMedia.imageURL) &&
                    oldMedia.title == null ? newMedia.title == null : oldMedia.title.equals(newMedia.title) &&
                    oldMedia.type == newMedia.type &&
                    oldMedia.date.equals(newMedia.date));
            return same;
        }
    };

}
