package com.tutorial.nano.popularmovies.fragments;


import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.TrailersAdapter;
import com.tutorial.nano.popularmovies.data.FavoriteMovie;
import com.tutorial.nano.popularmovies.data.FavoriteMovieDao;
import com.tutorial.nano.popularmovies.data.Movie;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.MovieTrailer;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;
import com.tutorial.nano.popularmovies.network.events.MovieDetailsRetrievedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class MovieDetailFragment extends Fragment {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private boolean fetchedExtraMovieDetails;
    private long mMovieId;
    private long entryId;
    public List<MovieTrailer> mTrailers;
    private ProgressBar mProgressBar;

    @Inject Application mApplication;

    TrailersAdapter mTrailersAdapter;

    @Inject MovieDao mMovieDao;
    @Inject FavoriteMovieDao mFavoriteMovieDao;
    @Inject EventBus mEventBus;
    @Inject NetworkJobManager mNetworkJobManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getActivity().getApplication()).getAppComponent().inject(this);
        mTrailers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fetchedExtraMovieDetails = false;
        Bundle arguments = getArguments();
        if(arguments != null) {
            entryId = arguments.getLong("entryId");
            mMovieId = arguments.getLong("movieId");
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.details_progress_bar);

        ListView trailersList = (ListView) rootView.findViewById(R.id.movie_trailers_list);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.movie_details_header, null, false);
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(header);
        rootView.setTag(headerViewHolder);
        trailersList.addHeaderView(header);
        trailersList.setEmptyView(rootView.findViewById(R.id.no_movie_selected));

        final ImageButton favoriteButton = (ImageButton) header.findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (favoriteButton.isSelected()) {
                    mFavoriteMovieDao.queryBuilder()
                            .where(FavoriteMovieDao.Properties.MovieId.eq(mMovieId))
                            .unique().delete();
                    favoriteButton.setSelected(false);
                } else {
                    mFavoriteMovieDao.insertInTx(new FavoriteMovie(null, mMovieId));
                    favoriteButton.setSelected(true);
                }
            }
        });

        View reviewsButton = inflater.inflate(R.layout.display_reviews_button, null, false);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MasterActivityCallback) getActivity()).onItemSelected(
                        entryId,
                        mMovieId,
                        MovieDetailFragment.class.getSimpleName()
                );
            }
        });
        trailersList.addFooterView(reviewsButton);

        mTrailersAdapter = new TrailersAdapter(getContext(), R.id.movie_trailers_list, mTrailers);
        trailersList.setAdapter(mTrailersAdapter);
        trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieTrailer item = (MovieTrailer) parent.getItemAtPosition(position);
                if (item == null) {
                    return;
                }
                Uri videoUri = Uri.parse(getString(R.string.youtube_video_base_url))
                        .buildUpon()
                        .appendQueryParameter("v", item.getSource())
                        .build();
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW, videoUri);
                startActivity(trailerIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reloadData();
        super.onActivityCreated(savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    private void updateDetailsList() {
        if (mMovieId == 0) {
            return;
        }
        fetchedExtraMovieDetails = true;
        mProgressBar.setVisibility(View.VISIBLE);
        mNetworkJobManager.requestMovieDetails(Long.toString(mMovieId));
    }

    private void reloadData() {
        new GetMovieFromDbTask().execute();
        new GetTrailersFromDbTask().execute();
        new CheckIfFavoriteTask().execute();
    }

    @Subscribe
    public void onMovieDetailsRetrieved(MovieDetailsRetrievedEvent moviesDetailsRetrievedEvent) {
        mMovieDao.detachAll();
        reloadData();
    }

    public static class HeaderViewHolder{
        public final TextView titleTextView;
        public final ImageView poster;
        public final TextView plotTextView;
        public final TextView releaseDateTextView;
        public final RatingBar voteAverageRatingBar;
        public final ImageButton favoriteButton;

        public String title;
        public String posterUrl;
        public String plot;
        public String releaseDate;
        public Double voteAverage;

        public void setValues(
                String title,
                String posterUrl,
                String plot,
                Date releaseDate,
                double voteAverage
        ) {
            try {
                String formattedDate = new SimpleDateFormat("dd MMMM yyyy").format(releaseDate);
                this.releaseDate = formattedDate;
                releaseDateTextView.setText(formattedDate);
            } catch (Exception e) {
                Log.d("HeaderViewHolder", e.toString());
                this.releaseDate = releaseDate.toString();
                releaseDateTextView.setText(this.releaseDate);
            }
            this.title = title;
            titleTextView.setText(title);

            this.posterUrl = posterUrl;

            this.plot = plot;
            plotTextView.setText(plot);
            this.voteAverage = voteAverage;
            voteAverageRatingBar.setRating((float) voteAverage * 5/10);
        }

        public HeaderViewHolder(View view){
            titleTextView = (TextView) view.findViewById(R.id.movie_detail_title);
            poster = (ImageView) view.findViewById(R.id.movie_detail_poster);
            plotTextView = (TextView) view.findViewById(R.id.movie_detail_plot);
            releaseDateTextView = (TextView) view.findViewById(R.id.movie_detail_release_date);
            voteAverageRatingBar = (RatingBar) view.findViewById(R.id.movie_detail_vote_average);
            favoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
        }
    }

    public class GetMovieFromDbTask extends AsyncTask<Void, Void, Movie> {
        @Override
        protected Movie doInBackground(Void... params) {
            return mMovieDao.queryBuilder()
                    .where(MovieDao.Properties.Id.eq(mMovieId))
                    .unique();

        }

        @Override
        protected void onPostExecute(Movie movie) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) getView().getTag();
            if(movie != null && headerViewHolder != null) {
                String posterUrl = movie.getPosterUrl();
                Picasso.with(getContext()).load(posterUrl).into(headerViewHolder.poster);

                headerViewHolder.setValues(
                        movie.getTitle(),
                        posterUrl,
                        movie.getPlot(),
                        movie.getReleaseDate(),
                        movie.getVoteAverage()
                );
            }
        }
    }

    public class GetTrailersFromDbTask extends AsyncTask<Void, Void, List<MovieTrailer>> {
        @Override
        protected List<MovieTrailer> doInBackground(Void... params) {
            Movie movie = mMovieDao.queryBuilder()
                    .where(MovieDao.Properties.Id.eq(mMovieId))
                    .unique();

            return movie == null ? new ArrayList<MovieTrailer>() : movie.getTrailers();
        }

        @Override
        protected void onPostExecute(List<MovieTrailer> trailers) {
            if(trailers.isEmpty() && !fetchedExtraMovieDetails) {
                updateDetailsList();
            }
            mTrailers.clear();
            mTrailers.addAll(trailers);
            mTrailersAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public class CheckIfFavoriteTask extends AsyncTask<Void, Void, FavoriteMovie> {
        @Override
        protected FavoriteMovie doInBackground(Void... params) {
            return mFavoriteMovieDao.queryBuilder()
                    .where(FavoriteMovieDao.Properties.MovieId.eq(mMovieId))
                    .unique();
        }

        @Override
        protected void onPostExecute(FavoriteMovie favoriteEntry) {
            if(favoriteEntry != null) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) getView().getTag();
                headerViewHolder.favoriteButton.setSelected(true);
                mTrailersAdapter.notifyDataSetChanged();
            }
        }
    }
}
