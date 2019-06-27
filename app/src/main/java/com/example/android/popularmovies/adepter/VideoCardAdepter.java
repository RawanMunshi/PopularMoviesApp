package com.example.android.popularmovies.adepter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.VideoCard;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * the VideoCard RecyclerView adepter
 */

public class VideoCardAdepter extends RecyclerView.Adapter<VideoCardAdepter.VideoCardAdepterViewHolder> {

    private List<VideoCard> videoCards;
    private OnItemClickListener clickListener;

    public void setVideoCards(List<VideoCard> videoCards) {
        this.videoCards = videoCards;
        notifyDataSetChanged();
    }

    public List<VideoCard> getVideoCards() {
        return videoCards;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public interface OnItemClickListener {
        void onListItemClick(int position);
    }

    @NonNull
    @Override
    public VideoCardAdepterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_item, viewGroup, false);
        return new VideoCardAdepterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCardAdepterViewHolder holder, int position) {
        if (videoCards.size() == 0) {
            holder.thumbnailTv.setText("No Trailers");
            return;
        }
        // bind the data to the view
        VideoCard card = videoCards.get(position);
        Picasso.get().load(card.getThumbnailURL()).error(R.color.white).into(holder.thumbnailIv);
        holder.thumbnailTv.setText(card.getTitle());
    }

    @Override
    public int getItemCount() {
        if (null == videoCards) return 0;
        return videoCards.size();
    }

    public class VideoCardAdepterViewHolder extends RecyclerView.ViewHolder {

        private final ImageView thumbnailIv;
        private final TextView thumbnailTv;

        public VideoCardAdepterViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailIv = itemView.findViewById(R.id.thumbnail);
            thumbnailTv = itemView.findViewById(R.id.thumbnail_text);

            // An onClick handler for the RecyclerView item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        clickListener.onListItemClick(position);
                    }
                }
            });

        }
    }
}
