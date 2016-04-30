package com.tutorial.nano.popularmovies.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.activities.MovieReviewsActivity;
import com.tutorial.nano.popularmovies.adapters.MovieDetailsCursorAdapter;
import com.tutorial.nano.popularmovies.data.MoviesContract;
import com.tutorial.nano.popularmovies.tasks.FetchExtraMovieDetailsTask;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int MOVIE_DETAILS_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private static final int FAVORITES_LOADER = 2;
    private boolean fetchedExtraMovieDetails;
    private long movieId;
    private long movieEntryId;

    private MovieDetailsCursorAdapter mMovieDetailsCursorAdapter;

    private static final String[] MOVIE_PROJECTION = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_POSTER_URL,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_PLOT,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    static final int COL_ID_INDEX = 0;
    static final int COL_MOVIE_ID_INDEX = 1;
    static final int COL_TITLE_INDEX = 2;
    static final int COL_POSTER_URL_INDEX = 3;
    static final int COL_PLOT_INDEX = 4;
    static final int COL_RELEASE_DATE_INDEX = 5;
    static final int COL_VOTE_AVERAGE_INDEX = 6;

    private static final String[] TRAILERS_PROJECTION = {
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry.COLUMN_NAME,
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry.COLUMN_SOURCE
    };

    static final int COL_TRAILER_ID_INDEX = 0;
    public static final int COL_TRAILER_NAME_INDEX = 1;
    static final int COL_TRAILER_SOURCE_INDEX = 2;

    private static final String[] FAVORITE_MOVIE_PROJECTION = {
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_TITLE,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_POSTER_URL,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_PLOT,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE
    };

    static final int FAVORITE_COL_ID = 0;
    static final int FAVORITE_COL_MOVIE_ID = 1;
    static final int FAVORITE_COL_TITLE_INDEX = 2;
    static final int FAVORITE_COL_POSTER_URL_INDEX = 3;
    static final int FAVORITE_COL_PLOT_INDEX = 4;
    static final int FAVORITE_COL_RELEASE_DATE_INDEX = 5;
    static final int FAVORITE_COL_VOTE_AVERAGE_INDEX = 6;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fetchedExtraMovieDetails = false;
        Intent intent = getActivity().getIntent();
        movieId = intent.getExtras().getLong("movieId");
        movieEntryId = intent.getExtras().getLong("movieEntryId");

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ListView trailersList = (ListView) rootView.findViewById(R.id.movie_trailers_list);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.movie_details_header, container, false);
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(header);
        rootView.setTag(headerViewHolder);
        trailersList.addHeaderView(header);

        final ImageButton favoriteButton = (ImageButton) header.findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (favoriteButton.isSelected()) {
                    Uri favoriteMovieUri = MoviesContract.FavoriteEntry.buildFavoriteWithMovieIdUri(Long.toString(movieId));
                    int deletedCount = getContext().getContentResolver().delete(favoriteMovieUri, null, null);
                    Log.d(LOG_TAG, deletedCount + " rows deleted.");
                    favoriteButton.setSelected(false);
                } else {
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) getView().getTag();
                    if (headerViewHolder == null || headerViewHolder.title.isEmpty()) {
                        return;
                    }
                    ContentValues movieDetails = new ContentValues();

                    movieDetails.put(MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);
                    movieDetails.put(MoviesContract.FavoriteEntry.COLUMN_TITLE, headerViewHolder.title);
                    movieDetails.put(MoviesContract.FavoriteEntry.COLUMN_POSTER_URL, headerViewHolder.posterUrl);
                    movieDetails.put(MoviesContract.FavoriteEntry.COLUMN_PLOT, headerViewHolder.plot);
                    movieDetails.put(MoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, headerViewHolder.releaseDate);
                    movieDetails.put(MoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, headerViewHolder.voteAverage);

                    Uri insertedFavoriteUri = getContext().getContentResolver().insert(MoviesContract.FavoriteEntry.CONTENT_URI, movieDetails);
                    if(insertedFavoriteUri != null) {
                        Log.d(LOG_TAG, insertedFavoriteUri + " inserted.");
                        favoriteButton.setSelected(true);
                    }
                }
            }
        });

        View reviewsButton = inflater.inflate(R.layout.display_reviews_button, container, false);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviews = new Intent(getContext(), MovieReviewsActivity.class)
                        .putExtra("movieId", movieId);
                startActivity(reviews);
            }
        });
        trailersList.addFooterView(reviewsButton);

        mMovieDetailsCursorAdapter = new MovieDetailsCursorAdapter(getContext(), null, 0);
        trailersList.setAdapter(mMovieDetailsCursorAdapter);
        trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = (Cursor) parent.getItemAtPosition(position);
                if (item == null) {
                    return;
                }
                if (item.getColumnName(COL_TRAILER_SOURCE_INDEX).equals(MoviesContract.TrailerEntry.COLUMN_SOURCE)) {
                    Uri videoUri = Uri.parse(getString(R.string.youtube_video_base_url))
                            .buildUpon()
                            .appendQueryParameter("v", item.getString(COL_TRAILER_SOURCE_INDEX))
                            .build();
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(trailerIntent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(FAVORITES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == MOVIE_DETAILS_LOADER) {
            Uri movieDetailsUri = MoviesContract.MovieEntry.buildMovieUri(movieEntryId);

            return new CursorLoader(
                    getContext(),
                    movieDetailsUri,
                    MOVIE_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        if (id == TRAILERS_LOADER) {
            Uri movieTrailersUri = MoviesContract.TrailerEntry.buildAllMovieTrailersUri(Long.toString(movieId));
            String sortOrder = MoviesContract.TrailerEntry._ID + " ASC";
            return new CursorLoader(
                    getContext(),
                    movieTrailersUri,
                    TRAILERS_PROJECTION,
                    null,
                    null,
                    sortOrder
            );
        }

        if (id == FAVORITES_LOADER) {
            Uri favoriteMovieUri = MoviesContract.FavoriteEntry.buildFavoriteWithMovieIdUri(Long.toString(movieId));
            return new CursorLoader(
                    getContext(),
                    favoriteMovieUri,
                    FAVORITE_MOVIE_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();

        HeaderViewHolder headerViewHolder = (HeaderViewHolder) getView().getTag();
        if (loaderId == MOVIE_DETAILS_LOADER && data.moveToFirst() && headerViewHolder != null) {
            String posterUrl = data.getString(COL_POSTER_URL_INDEX);
            Picasso.with(getContext()).load(posterUrl).into(headerViewHolder.poster);

            headerViewHolder.setValues(
                    data.getString(COL_TITLE_INDEX),
                    posterUrl,
                    data.getString(COL_PLOT_INDEX),
                    data.getString(COL_RELEASE_DATE_INDEX),
                    data.getDouble(COL_VOTE_AVERAGE_INDEX)
            );
        }

        if (loaderId == TRAILERS_LOADER) {
            if(!data.moveToFirst() && !fetchedExtraMovieDetails) {
                updateDetailsList();
            }
            mMovieDetailsCursorAdapter.swapCursor(data);
        }

        if (loaderId == FAVORITES_LOADER && data.moveToFirst()) {
            String posterUrl = data.getString(FAVORITE_COL_POSTER_URL_INDEX);
            Picasso.with(getContext()).load(posterUrl).into(headerViewHolder.poster);

            headerViewHolder.setValues(
                data.getString(FAVORITE_COL_TITLE_INDEX),
                posterUrl,
                data.getString(FAVORITE_COL_PLOT_INDEX),
                data.getString(FAVORITE_COL_RELEASE_DATE_INDEX),
                data.getDouble(FAVORITE_COL_VOTE_AVERAGE_INDEX)
            );

            headerViewHolder.favoriteButton.setSelected(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieDetailsCursorAdapter.swapCursor(null);
    }

    private void updateDetailsList() {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }
        fetchedExtraMovieDetails = true;
        FetchExtraMovieDetailsTask extraMovieDetailsTask = new FetchExtraMovieDetailsTask(getContext());
        extraMovieDetailsTask.execute(Long.toString(movieId));
    }

    public static class HeaderViewHolder{
        public final TextView titleTextView;
        public final ImageView poster;
        public final TextView plotTextView;
        public final TextView releaseDateTextView;
        public final TextView voteAverageTextView;
        public final ImageButton favoriteButton;

        public String title;
        public String posterUrl;
        public String plot;
        public String releaseDate;
        public Double voteAverage;

        public void setValues(String title, String posterUrl, String plot, String releaseDate, Double voteAverage) {
            this.title = title;
            titleTextView.setText(title);

            this.posterUrl = posterUrl;

            this.plot = plot;
            plotTextView.setText(plot);
            this.releaseDate = releaseDate;
            releaseDateTextView.setText(releaseDate);
            this.voteAverage = voteAverage;
            voteAverageTextView.setText(Double.toString(voteAverage) + "/10");
        }

        public HeaderViewHolder(View view){
            titleTextView = (TextView) view.findViewById(R.id.movie_detail_title);
            poster = (ImageView) view.findViewById(R.id.movie_detail_poster);
            plotTextView = (TextView) view.findViewById(R.id.movie_detail_plot);
            releaseDateTextView = (TextView) view.findViewById(R.id.movie_detail_release_date);
            voteAverageTextView = (TextView) view.findViewById(R.id.movie_detail_vote_average);
            favoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
        }
    }
}
