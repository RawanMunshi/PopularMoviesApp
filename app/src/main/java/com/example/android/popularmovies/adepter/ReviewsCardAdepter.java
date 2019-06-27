package com.example.android.popularmovies.adepter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.ReviewCard;

import java.util.List;

/**
 * the ReviewsCard RecyclerView adepter
 */

public class ReviewsCardAdepter extends RecyclerView.Adapter<ReviewsCardAdepter.ReviewCardAdepterViewHolder> {

    private List<ReviewCard> reviewCards;

    @NonNull
    @Override
    public ReviewCardAdepterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, viewGroup, false);
        return new ReviewCardAdepterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewCardAdepterViewHolder holder, int position) {
        // bind the data to the view
        ReviewCard card = reviewCards.get(position);
        holder.reviewNumber.setText("Review #");
        holder.reviewNumber.append(String.valueOf(position + 1));
        holder.review.setText(card.getContent());
        holder.author.setText("by");
        holder.author.append(" ");
        holder.author.append(card.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (null == reviewCards) return 0;
        return reviewCards.size();
    }

    public void setReviewCards(List<ReviewCard> reviewCards) {
        this.reviewCards = reviewCards;
        notifyDataSetChanged();
    }

    public class ReviewCardAdepterViewHolder extends RecyclerView.ViewHolder {

        private final TextView reviewNumber;
        private final TextView review;
        private final TextView author;

        public ReviewCardAdepterViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewNumber = itemView.findViewById(R.id.review_number);
            review = itemView.findViewById(R.id.review);
            author = itemView.findViewById(R.id.author);
        }
    }

}
