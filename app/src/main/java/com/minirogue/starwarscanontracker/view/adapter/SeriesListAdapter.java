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
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotesDto;

import java.util.ArrayList;
import java.util.List;

public class SeriesListAdapter extends ListAdapter<MediaAndNotesDto, SeriesListAdapter.MediaViewHolder> {
    private final OnItemClickedListener listener;

    public SeriesListAdapter(OnItemClickedListener listener) {
        super(DiffCallback);
        this.listener = listener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(int itemId);
    }

    @Override
    public void submitList(@Nullable List<MediaAndNotesDto> list) {
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
        MediaAndNotesDto currentItem = getItem(position);
        holder.itemView.setOnClickListener(view -> listener.onItemClicked(currentItem.getMediaItemDto().getId()));
        holder.titleTextView.setText(currentItem.getMediaItemDto().getTitle());
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder {

        final TextView titleTextView;

        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.media_title);
        }
    }

    private static final DiffUtil.ItemCallback<MediaAndNotesDto> DiffCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull MediaAndNotesDto oldItem, @NonNull MediaAndNotesDto newItem) {
            return oldItem.getMediaItemDto().getId() == newItem.getMediaItemDto().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MediaAndNotesDto oldItem, @NonNull MediaAndNotesDto newItem) {
            return oldItem.equals(newItem);
        }
    };

}
