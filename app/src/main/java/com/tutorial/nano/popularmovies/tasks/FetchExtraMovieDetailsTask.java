package com.tutorial.nano.popularmovies.tasks;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.tutorial.nano.popularmovies.BuildConfig;
import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieReview;
import com.tutorial.nano.popularmovies.data.MovieReviewDao;
import com.tutorial.nano.popularmovies.data.MovieTrailer;
import com.tutorial.nano.popularmovies.data.MovieTrailerDao;
import com.tutorial.nano.popularmovies.interfaces.AsyncResponseNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javax.inject.Inject;

public class FetchExtraMovieDetailsTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchExtraMovieDetailsTask.class.getSimpleName();
    private Application mApplication;
    @Inject MovieTrailerDao mTrailerDao;
    @Inject MovieReviewDao mReviewDao;
    private AsyncResponseNotification mDelegate;

    public FetchExtraMovieDetailsTask(Application application, AsyncResponseNotification delegate) {
        ((PopularMoviesApp) application).getAppComponent().inject(this);
        mApplication = application;
        mDelegate = delegate;
    }

    private void saveTrailersAndReviewsToDb(String json) throws JSONException {
        final String MOVIE_ID_KEY = "id";

        final String TRAILERS_KEY = "trailers";
        final String TRAILER_RESULTS_KEY = "youtube";
        final String TRAILER_NAME_KEY = "name";
        final String TRAILER_SIZE_KEY = "size";
        final String TRAILER_SOURCE_KEY = "source";
        final String TRAILER_TYPE_KEY = "type";

        final String REVIEWS_KEY = "reviews";
        final String REVIEW_RESULTS_KEY = "results";
        final String REVIEW_ID_KEY = "id";
        final String REVIEW_AUTHOR_KEY = "author";
        final String REVIEW_CONTENT_KEY = "content";
        final String REVIEW_URL_KEY = "url";

        JSONObject responseObject = new JSONObject(json);
        Long movieId = responseObject.getLong(MOVIE_ID_KEY);
        JSONArray trailersArray = responseObject.getJSONObject(TRAILERS_KEY).getJSONArray(TRAILER_RESULTS_KEY);
        JSONArray reviewsArray = responseObject.getJSONObject(REVIEWS_KEY).getJSONArray(REVIEW_RESULTS_KEY);

        int numberOfTrailers = trailersArray.length();
        Vector<MovieTrailer> trailers = new Vector<>(numberOfTrailers);
        for (int i = 0; i < numberOfTrailers; i++) {
            JSONObject trailerJsonObject = trailersArray.getJSONObject(i);
            MovieTrailer trailer = new MovieTrailer();

            trailer.setMovieId(movieId);
            trailer.setName(trailerJsonObject.getString(TRAILER_NAME_KEY));
            trailer.setSize(trailerJsonObject.getString(TRAILER_SIZE_KEY));
            trailer.setSource(trailerJsonObject.getString(TRAILER_SOURCE_KEY));
            trailer.setType(trailerJsonObject.getString(TRAILER_TYPE_KEY));

            trailers.add(trailer);
        }

        int numberOfReviews = reviewsArray.length();
        Vector<MovieReview> reviews = new Vector<>(numberOfReviews);
        for (int i = 0; i < numberOfReviews; i++) {
            JSONObject reviewJsonObject = reviewsArray.getJSONObject(i);
            MovieReview review = new MovieReview();

            review.setMovieId(movieId);
            review.setReviewId(reviewJsonObject.getString(REVIEW_ID_KEY));
            review.setReviewURL(reviewJsonObject.getString(REVIEW_URL_KEY));
            review.setAuthor(reviewJsonObject.getString(REVIEW_AUTHOR_KEY));
            review.setContent(reviewJsonObject.getString(REVIEW_CONTENT_KEY));

            reviews.add(review);
        }

        mTrailerDao.insertOrReplaceInTx(trailers);
        mReviewDao.insertOrReplaceInTx(reviews);
        Log.d(LOG_TAG, "FetchExtraMovieDataTask completed.");
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonResult;
        try {
            final String BASE_URL = mApplication.getString(R.string.movies_api_base_url);
            final String MOVIE_ID = params[0];
            final String API_KEY_PARAM = "api_key";
            final String APPEND_TO_RESPONSE = "append_to_response";
            final String APPENDED_VALUES = "trailers,reviews";

            Uri finalUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(MOVIE_ID)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .appendQueryParameter(APPEND_TO_RESPONSE, APPENDED_VALUES)
                    .build();

            URL url = new URL(finalUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if(buffer.length() == 0) {
                return null;
            }

            jsonResult = buffer.toString();
            try {
                saveTrailersAndReviewsToDb(jsonResult);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "ERROR parsing json: ", e);
                e.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            mDelegate.notifyTaskCompleted();
        }
    }
}
