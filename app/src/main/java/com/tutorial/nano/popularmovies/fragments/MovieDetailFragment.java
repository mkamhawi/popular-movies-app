package com.tutorial.nano.popularmovies.fragments;


import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.activities.SingleReviewActivity;
import com.tutorial.nano.popularmovies.adapters.MovieDetailsAdapter;
import com.tutorial.nano.popularmovies.data.FavoriteMovieDao;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.models.MovieModel;
import com.tutorial.nano.popularmovies.data.models.ReviewModel;
import com.tutorial.nano.popularmovies.data.models.TrailerModel;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment {
    private final String TAG = MovieDetailFragment.class.getSimpleName();

    private long mMovieId;

    @BindView(R.id.details_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.movie_details_list)
    RecyclerView mDetailsList;

    @BindView(R.id.movie_detail_title)
    TextView titleTextView;

    @BindView(R.id.movie_detail_poster)
    ImageView poster;

    @BindView(R.id.movie_detail_plot)
    TextView plotTextView;

    @BindView(R.id.movie_detail_release_date)
    TextView releaseDateTextView;

    @BindView(R.id.movie_detail_vote_average)
    RatingBar voteAverageRatingBar;

    @BindView(R.id.favorite_button)
    ImageButton favoriteButton;

    @Inject Application mApplication;
    @Inject MovieDao mMovieDao;
    @Inject FavoriteMovieDao mFavoriteMovieDao;
    @Inject EventBus mEventBus;
    @Inject NetworkJobManager mNetworkJobManager;

    MovieDetailsViewModel viewModel;
    MovieDetailsAdapter mDetailsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if(arguments != null) {
            mMovieId = arguments.getLong("movieId");
        }

        viewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
        viewModel.init(mMovieId, mMovieDao, mFavoriteMovieDao, mNetworkJobManager);

        voteAverageRatingBar.setNumStars(5);

        mDetailsAdapter = new MovieDetailsAdapter(
                (position, v) -> {
                    MovieModel model = viewModel.getMovieDetails().getValue();
                    if (model == null) return;
                    TrailerModel trailerModel = model.getTrailers().get(position);
                    if (trailerModel == null) return;
                    Uri videoUri = Uri.parse(getString(R.string.youtube_video_base_url))
                        .buildUpon()
                        .appendQueryParameter("v", trailerModel.getSource())
                        .build();
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(trailerIntent);
                }, (position, v) -> {
                    MovieModel model = viewModel.getMovieDetails().getValue();
                    if (model == null) return;
                    ReviewModel reviewModel = model.getReviews().get(position);
                    Intent intent = new Intent(getActivity(), SingleReviewActivity.class)
                        .putExtra("review", reviewModel);
                    startActivity(intent);
                });
        mDetailsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mDetailsList.setAdapter(mDetailsAdapter);

        mProgressBar.setVisibility(View.VISIBLE);
        viewModel.getMovieDetails().observe(this, movieModel -> {
            if (movieModel == null) return;

            mDetailsAdapter.setMovieDetails(movieModel);

            if (movieModel.getPosterPath() != null)
                Picasso.with(getContext()).load(getString(R.string.movies_poster_base_url) + movieModel.getPosterPath()).into(poster);

            titleTextView.setText(movieModel.getTitle());
            plotTextView.setText(movieModel.getPlot());

            String formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
                    .format(movieModel.getReleaseDate());
            releaseDateTextView.setText(formattedDate);
            voteAverageRatingBar.setRating(Float.valueOf(movieModel.getVoteAverage().toString()));
            mProgressBar.setVisibility(View.GONE);
        });

        viewModel.isFavorite().observe(this, isFavorite -> {
            favoriteButton.setSelected(isFavorite);
            mDetailsAdapter.notifyDataSetChanged();
        });

        favoriteButton.setOnClickListener(view -> {
            boolean isSelected = !view.isSelected();
            viewModel.setIsFavorite(isSelected);
            view.setSelected(isSelected);
        });

        return rootView;
    }
}
