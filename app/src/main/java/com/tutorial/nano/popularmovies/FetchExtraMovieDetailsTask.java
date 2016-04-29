package com.tutorial.nano.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.tutorial.nano.popularmovies.data.MoviesContract;

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

public class FetchExtraMovieDetailsTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchExtraMovieDetailsTask.class.getSimpleName();
    private final Context mContext;

    public FetchExtraMovieDetailsTask(Context context) {
        mContext = context;
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
        int movieId = responseObject.getInt(MOVIE_ID_KEY);
        JSONArray trailersArray = responseObject.getJSONObject(TRAILERS_KEY).getJSONArray(TRAILER_RESULTS_KEY);
        JSONArray reviewsArray = responseObject.getJSONObject(REVIEWS_KEY).getJSONArray(REVIEW_RESULTS_KEY);

        int numberOfTrailers = trailersArray.length();
        Vector<ContentValues> trailersContentValues = new Vector<>(numberOfTrailers);
        for (int i = 0; i < numberOfTrailers; i++) {
            JSONObject trailerJsonObject = trailersArray.getJSONObject(i);
            ContentValues trailerDetails = new ContentValues();

            trailerDetails.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            trailerDetails.put(MoviesContract.TrailerEntry.COLUMN_NAME, trailerJsonObject.getString(TRAILER_NAME_KEY));
            trailerDetails.put(MoviesContract.TrailerEntry.COLUMN_SIZE, trailerJsonObject.getString(TRAILER_SIZE_KEY));
            trailerDetails.put(MoviesContract.TrailerEntry.COLUMN_SOURCE, trailerJsonObject.getString(TRAILER_SOURCE_KEY));
            trailerDetails.put(MoviesContract.TrailerEntry.COLUMN_TYPE, trailerJsonObject.getString(TRAILER_TYPE_KEY));

            trailersContentValues.add(trailerDetails);
        }

        int numberOfReviews = reviewsArray.length();
        Vector<ContentValues> reviewsContentValues = new Vector<>(numberOfReviews);
        for (int i = 0; i < numberOfReviews; i++) {
            JSONObject reviewJsonObject = reviewsArray.getJSONObject(i);
            ContentValues reviewDetails = new ContentValues();

            reviewDetails.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            reviewDetails.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, reviewJsonObject.getString(REVIEW_ID_KEY));
            reviewDetails.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_URL, reviewJsonObject.getString(REVIEW_URL_KEY));
            reviewDetails.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, reviewJsonObject.getString(REVIEW_AUTHOR_KEY));
            reviewDetails.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, reviewJsonObject.getString(REVIEW_CONTENT_KEY));

            reviewsContentValues.add(reviewDetails);
        }

        Uri trailersUri = MoviesContract.TrailerEntry.buildAllMovieTrailersUri(Integer.toString(movieId));
        int deletedTrailersCount = 0;
        int insertedTrailersCount = 0;

        Uri reviewsUri = MoviesContract.ReviewEntry.buildAllMovieReviewsUri(Integer.toString(movieId));
        int deletedReviewsCount = 0;
        int insertedReviewsCount = 0;

        if(trailersContentValues.size() > 0) {
            deletedTrailersCount = mContext.getContentResolver().delete(trailersUri, null, null);
            ContentValues[] fetchedTrailers = new ContentValues[trailersContentValues.size()];
            trailersContentValues.toArray(fetchedTrailers);
            insertedTrailersCount = mContext.getContentResolver().bulkInsert(trailersUri, fetchedTrailers);
        }

        if(reviewsContentValues.size() > 0) {
            deletedReviewsCount = mContext.getContentResolver().delete(reviewsUri, null, null);
            ContentValues[] fetchedReviews = new ContentValues[reviewsContentValues.size()];
            reviewsContentValues.toArray(fetchedReviews);
            insertedReviewsCount = mContext.getContentResolver().bulkInsert(reviewsUri, fetchedReviews);
        }

        Log.d(LOG_TAG, "FetchExtraMovieDataTask completed.");
        Log.d(LOG_TAG, deletedTrailersCount + " trailers deleted.");
        Log.d(LOG_TAG, insertedTrailersCount + " trailers inserted.");
        Log.d(LOG_TAG, deletedReviewsCount + " reviews deleted.");
        Log.d(LOG_TAG, insertedReviewsCount + " reviews inserted.");
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
            final String BASE_URL = mContext.getString(R.string.movies_api_base_url);
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
        }
    }
}
