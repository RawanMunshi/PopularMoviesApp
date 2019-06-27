package com.example.android.popularmovies.adepter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.MovieCard;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * the RecyclerView adepter
 */
public class MovieCardAdepter extends RecyclerView.Adapter<MovieCardAdepter.MovieCardAdepterViewHolder> {

    private List<MovieCard> movieCards;
    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @Override
    public MovieCardAdepterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item, viewGroup, false);
        return new MovieCardAdepterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieCardAdepterViewHolder holder, int position) {
        // bind the data to the view
        MovieCard card = movieCards.get(position);
        holder.moviePosterIv.setContentDescription(card.getMTitle());
        Picasso.get().load(card.getMPoster()).error(R.color.white).into(holder.moviePosterIv);
    }

    @Override
    public int getItemCount() {
        if (null == movieCards) return 0;
        return movieCards.size();
    }

    public void setMovieCards(List<MovieCard> movieCards) {
        this.movieCards = movieCards;
        notifyDataSetChanged();
    }

    public List<MovieCard> getMovieCard() {
        if (movieCards != null) {
            return movieCards;
        }
        return null;
    }

    public interface OnItemClickListener {
        void onListItemClick(int position);
    }

    public class MovieCardAdepterViewHolder extends RecyclerView.ViewHolder {

        private final ImageView moviePosterIv;

        MovieCardAdepterViewHolder(View itemView) {
            super(itemView);
            moviePosterIv = itemView.findViewById(R.id.movie_card_poster);

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
