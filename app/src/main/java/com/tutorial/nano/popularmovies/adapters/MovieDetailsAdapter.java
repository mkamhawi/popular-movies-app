package com.tutorial.nano.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.models.MovieModel;
import com.tutorial.nano.popularmovies.data.models.ReviewModel;
import com.tutorial.nano.popularmovies.data.models.TrailerModel;
import com.tutorial.nano.popularmovies.interfaces.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsAdapter extends RecyclerView.Adapter<MovieDetailsAdapter.DetailsItemViewHolder> {

    private Context context;
    private MovieModel movieDetails;
    private OnItemClickListener trailerClickListener;
    private OnItemClickListener reviewClickListener;

    public MovieDetailsAdapter(OnItemClickListener trailerClickListener, OnItemClickListener reviewClickListener) {
        movieDetails = new MovieModel();
        this.trailerClickListener = trailerClickListener;
        this.reviewClickListener = reviewClickListener;
    }

    @NonNull
    @Override
    public MovieDetailsAdapter.DetailsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new DetailsItemViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.movie_details_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsItemViewHolder holder, int position) {

        if (position < movieDetails.getTrailers().size()) {
            TrailerModel trailer = movieDetails.getTrailers().get(position);

            holder.sectionHeader.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            holder.sectionHeader.setText(R.string.trailers);
            holder.itemIcon.setImageDrawable(context.getDrawable(trailer.getIcon()));
            holder.itemName.setText(trailer.getName());
            holder.itemDescription.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                trailerClickListener.onItemClicked(holder.getAdapterPosition(), v);
            });
        } else {
            ReviewModel review = movieDetails.getReviews().get(position - movieDetails.getTrailers().size());

            holder.sectionHeader.setVisibility(
                    position == movieDetails.getTrailers().size() ? View.VISIBLE : View.GONE);
            holder.sectionHeader.setText(R.string.reviews);
            holder.itemIcon.setImageDrawable(context.getDrawable(review.getIcon()));
            holder.itemName.setText(review.getAuthor());
            holder.itemDescription.setText(review.getContent());
            holder.itemView.setOnClickListener(v -> {
                reviewClickListener.onItemClicked(holder.getAdapterPosition() - movieDetails.getTrailers().size(), v);
            });
        }
    }

    @Override
    public int getItemCount() {
        return movieDetails.getTrailers().size() + movieDetails.getReviews().size();
    }

    public void setMovieDetails(MovieModel movieDetails) {
        this.movieDetails = movieDetails;
        notifyDataSetChanged();
    }

    static class DetailsItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.section_header)
        TextView sectionHeader;

        @BindView(R.id.item_icon)
        ImageView itemIcon;

        @BindView(R.id.item_name)
        TextView itemName;

        @BindView(R.id.item_description)
        TextView itemDescription;

        public DetailsItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
