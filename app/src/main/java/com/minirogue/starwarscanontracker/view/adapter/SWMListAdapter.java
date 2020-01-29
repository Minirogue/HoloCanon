package com.minirogue.starwarscanontracker.view.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel;
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;

import java.util.ArrayList;
import java.util.List;

public class SWMListAdapter extends ListAdapter<MediaAndNotes, SWMListAdapter.MediaViewHolder> {

    //private final String TAG = "adapter";

    //private AsyncListDiffer<MediaAndNotes> listDiffer = new AsyncListDiffer<>(this, DiffCallback);
    //private List<MediaAndNotes> currentList = new ArrayList<>();
    private OnClickListener listener;
    private final MediaListViewModel mediaListViewModel;
    private String[] checkBoxText = new String[]{"", "", ""};
    private boolean[] isCheckBoxActive = new boolean[]{true, true, true};

    public SWMListAdapter(MediaListViewModel mediaListViewModel) {
        super(DiffCallback);
        this.mediaListViewModel = mediaListViewModel;
    }

    public void updateCheckBoxText(String[] newCheckBoxText) {
        checkBoxText = newCheckBoxText;
        notifyDataSetChanged();
    }

    public void updateCheckBoxVisible(boolean[] newIsCheckboxActive){
        this.isCheckBoxActive = newIsCheckboxActive;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener newListener) {
        listener = newListener;
    }

    public interface OnClickListener {
        void onItemClicked(int itemId);
        void onCheckbox1Clicked(MediaNotes mediaNotes);
        void onCheckbox2Clicked(MediaNotes mediaNotes);
        void onCheckbox3Clicked(MediaNotes mediaNotes);
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
                .inflate(R.layout.media_list_item, parent, false);
        return new MediaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaAndNotes currentItem = getItem(position);
        holder.itemView.setOnClickListener(view -> listener.onItemClicked(currentItem.mediaItem.id));
        holder.titleTextView.setText(currentItem.mediaItem.title);
        holder.dateTextView.setText(currentItem.mediaItem.date);
        //TODO get rid of ViewModel reference here.
        holder.typeTextView.setText(mediaListViewModel.convertTypeToString(currentItem.mediaItem.type));
        switch (currentItem.mediaItem.type){
//            case 10: //TV Episode TODO: do not hardcode this.
//                holder.extraInfoTextView.setVisibility(View.VISIBLE);
//                holder.extraInfoTextView.setText("Testing textview");
//                break;
            default:
                holder.extraInfoTextView.setVisibility((View.INVISIBLE));
                break;
        }

        //cover image
        holder.coverImage.getHierarchy().setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE);
        String uriString = currentItem.mediaItem.imageURL;
        if (uriString != null && !uriString.equals("")) {
            //TODO replace viewmodel reference here
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(currentItem.mediaItem.imageURL))
                    .setLowestPermittedRequestLevel(mediaListViewModel.isNetworkAllowed() ? ImageRequest.RequestLevel.FULL_FETCH : ImageRequest.RequestLevel.DISK_CACHE)
                    .build();
            holder.coverImage.setImageRequest(request);
            holder.coverImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        } else {
            holder.coverImage.setActualImageResource(R.drawable.ic_launcher_foreground);
            holder.coverImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        }

        holder.userDefinedView1.setVisibility(isCheckBoxActive[0]? View.VISIBLE: View.GONE);
        holder.userDefinedView2.setVisibility(isCheckBoxActive[1]? View.VISIBLE: View.GONE);
        holder.userDefinedView3.setVisibility(isCheckBoxActive[2]? View.VISIBLE: View.GONE);


        //Set checkbox text
        holder.textCheckBox1.setText(checkBoxText[0]);
        holder.textCheckBox2.setText(checkBoxText[1]);
        holder.textCheckBox3.setText(checkBoxText[2]);
        //Set checks
        holder.checkBoxWatchedRead.setChecked(currentItem.mediaNotes.isUserChecked1());
        holder.checkBoxWantToWatchRead.setChecked(currentItem.mediaNotes.isUserChecked2());
        holder.checkBoxOwned.setChecked(currentItem.mediaNotes.isUserChecked3());
        //Store the MediaNotes object in the View so it can be sent back to get updated in the database
        holder.checkBoxWatchedRead.setTag(currentItem.mediaNotes);
        holder.checkBoxWantToWatchRead.setTag(currentItem.mediaNotes);
        holder.checkBoxOwned.setTag(currentItem.mediaNotes);
        //Set onclicklistener to update checked status
        holder.checkBoxWatchedRead.setOnClickListener(view -> listener.onCheckbox1Clicked((MediaNotes) view.getTag()));
        holder.checkBoxWantToWatchRead.setOnClickListener(view -> listener.onCheckbox2Clicked((MediaNotes) view.getTag()));
        holder.checkBoxOwned.setOnClickListener(view -> listener.onCheckbox3Clicked((MediaNotes) view.getTag()));
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder {

        final TextView titleTextView;
        final TextView typeTextView;
        final TextView dateTextView;
        final TextView extraInfoTextView;
        final CheckBox checkBoxWatchedRead;
        final CheckBox checkBoxWantToWatchRead;
        final CheckBox checkBoxOwned;
        final SimpleDraweeView coverImage;
        final TextView textCheckBox1;
        final TextView textCheckBox2;
        final TextView textCheckBox3;
        final View userDefinedView1;
        final View userDefinedView2;
        final View userDefinedView3;


        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.media_title);
            typeTextView = itemView.findViewById(R.id.media_type);
            dateTextView = itemView.findViewById(R.id.date_textview);
            extraInfoTextView = itemView.findViewById(R.id.extra_info);
            checkBoxWatchedRead = itemView.findViewById(R.id.checkbox_1);
            checkBoxWantToWatchRead = itemView.findViewById(R.id.checkbox_2);
            checkBoxOwned = itemView.findViewById(R.id.checkbox_3);
            coverImage = itemView.findViewById(R.id.image_cover);
            textCheckBox1 = itemView.findViewById(R.id.text_checkbox_1);
            textCheckBox2 = itemView.findViewById(R.id.text_checkbox_2);
            textCheckBox3 = itemView.findViewById(R.id.text_checkbox_3);
            userDefinedView1 = itemView.findViewById(R.id.user_1_sublayout);
            userDefinedView2 = itemView.findViewById(R.id.user_2_sublayout);
            userDefinedView3 = itemView.findViewById(R.id.user_3_sublayout);
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
