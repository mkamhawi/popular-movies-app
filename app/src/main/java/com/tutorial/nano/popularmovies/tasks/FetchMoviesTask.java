package com.tutorial.nano.popularmovies.tasks;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.tutorial.nano.popularmovies.BuildConfig;
import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.Movie;
import com.tutorial.nano.popularmovies.data.MovieDao;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.inject.Inject;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Application mApplication;
    @Inject MovieDao mMovieDao;
    private AsyncResponseNotification mDelegate;

    public FetchMoviesTask(Application application, AsyncResponseNotification delegate) {
        ((PopularMoviesApp) application).getAppComponent().inject(this);
        mApplication = application;
        mDelegate = delegate;
    }

    private void saveMoviesToDb(String json, String sortPreference) throws JSONException {
        final String RESULTS_KEY = "results";
        final String ID_KEY = "id";
        final String TITLE_KEY = "title";
        final String POSTER_PATH_KEY = "poster_path";
        final String PLOT_KEY = "overview";
        final String RELEASE_DATE_KEY = "release_date";
        final String VOTE_AVERAGE_KEY = "vote_average";

        JSONArray movieJsonArray = new JSONObject(json).getJSONArray(RESULTS_KEY);

        int numberOfMovies = movieJsonArray.length();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate;
        Vector<Movie> movies = new Vector<>(numberOfMovies);
        String posterBaseUrl = mApplication.getString(R.string.movies_poster_base_url);
        for (int i = 0; i < numberOfMovies; i++) {
            JSONObject movieJsonObject = movieJsonArray.getJSONObject(i);
            String posterUrl = posterBaseUrl
                    + movieJsonObject.getString(POSTER_PATH_KEY);

            try {
                releaseDate = dateFormat.parse(movieJsonObject.getString(RELEASE_DATE_KEY));
            } catch (ParseException e) {
                Log.d(LOG_TAG, Log.getStackTraceString(e));
                releaseDate = Calendar.getInstance().getTime();
            }

            Movie movie = new Movie();
            movie.setId(movieJsonObject.getLong(ID_KEY));
            movie.setTitle(movieJsonObject.getString(TITLE_KEY));
            movie.setPosterUrl(posterUrl);
            movie.setPlot(movieJsonObject.getString(PLOT_KEY));
            movie.setReleaseDate(releaseDate);
            movie.setVoteAverage(movieJsonObject.getDouble(VOTE_AVERAGE_KEY));
            movie.setSortPreferences(sortPreference);
            movies.add(movie);
        }

        mMovieDao.insertOrReplaceInTx(movies, false);
        Log.d(LOG_TAG, "FetchMoviesTask completed.");
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
            final String SORT_PREFERENCE = params[0];
            final String API_KEY_PARAM = "api_key";

            Uri finalUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SORT_PREFERENCE)
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
                saveMoviesToDb(jsonResult, SORT_PREFERENCE);
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