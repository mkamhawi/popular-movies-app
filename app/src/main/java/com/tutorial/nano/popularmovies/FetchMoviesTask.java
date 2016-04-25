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

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    private void saveMoviesToDb(String json) throws JSONException {
        final String RESULTS_KEY = "results";
        final String ID_KEY = "id";
        final String TITLE_KEY = "title";
        final String POSTER_PATH_KEY = "poster_path";
        final String PLOT_KEY = "overview";
        final String RELEASE_DATE_KEY = "release_date";
        final String VOTE_AVERAGE_KEY = "vote_average";

        JSONArray movieJsonArray = new JSONObject(json).getJSONArray(RESULTS_KEY);

        int numberOfMovies = movieJsonArray.length();
        Vector<ContentValues> contentValues = new Vector<>(numberOfMovies);
        String posterBaseUrl = mContext.getString(R.string.movies_poster_base_url);
        for (int i = 0; i < numberOfMovies; i++) {
            JSONObject movieJsonObject = movieJsonArray.getJSONObject(i);
            String posterUrl = posterBaseUrl
                    + movieJsonObject.getString(POSTER_PATH_KEY);

            ContentValues movieDetails = new ContentValues();

            movieDetails.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieJsonObject.getInt(ID_KEY));
            movieDetails.put(MoviesContract.MovieEntry.COLUMN_TITLE, movieJsonObject.getString(TITLE_KEY));
            movieDetails.put(MoviesContract.MovieEntry.COLUMN_POSTER_URL, posterUrl);
            movieDetails.put(MoviesContract.MovieEntry.COLUMN_PLOT, movieJsonObject.getString(PLOT_KEY));
            movieDetails.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movieJsonObject.getString(RELEASE_DATE_KEY));
            movieDetails.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieJsonObject.getDouble(VOTE_AVERAGE_KEY));

            contentValues.add(movieDetails);
        }

        int deletedCount = 0;
        int insertedCount = 0;

        if(contentValues.size() > 0) {
            // Remove all cached movie details to insert new fetched results.
            deletedCount = mContext.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, null, null);
            ContentValues[] fetchedMovies = new ContentValues[contentValues.size()];
            contentValues.toArray(fetchedMovies);
            insertedCount = mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, fetchedMovies);
        }

        Log.d(LOG_TAG, "FetchMoviesTask completed.");
        Log.d(LOG_TAG, deletedCount + " rows deleted.");
        Log.d(LOG_TAG, insertedCount + " rows inserted.");
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
            final String SORT_ORDER = params[0];
            final String API_KEY_PARAM = "api_key";

            Uri finalUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SORT_ORDER)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
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
                saveMoviesToDb(jsonResult);
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