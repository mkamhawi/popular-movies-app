package com.tutorial.nano.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentMain extends Fragment {

    private ArrayAdapter<Movie> mImageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdapter = new ImageAdapter(getContext(), R.id.movies_grid_item);
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        moviesGrid.setAdapter(mImageAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mImageAdapter.getItem(position);
                Intent details = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("Movie", movie);
                startActivity(details);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragmentmain, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(sortOrder);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMoviesFromJson(String json) throws JSONException {
            final String RESULTS_KEY = "results";
            final String ID_KEY = "id";
            final String TITLE_KEY = "title";
            final String POSTER_PATH_KEY = "poster_path";
            final String PLOT_KEY = "overview";
            final String RELEASE_DATE_KEY = "release_date";
            final String VOTE_AVERAGE_KEY = "vote_average";

            JSONArray movieJsonArray = new JSONObject(json).getJSONArray(RESULTS_KEY);

            int numberOfMovies = movieJsonArray.length();
            Movie[] result = new Movie[numberOfMovies];
            for (int i = 0; i < movieJsonArray.length(); i++) {
                JSONObject movieJsonObject = movieJsonArray.getJSONObject(i);
                String posterUrl = getContext().getString(R.string.movies_poster_base_url)
                        + movieJsonObject.getString(POSTER_PATH_KEY);

                result[i] = new Movie(
                        movieJsonObject.getInt(ID_KEY),
                        movieJsonObject.getString(TITLE_KEY),
                        posterUrl,
                        movieJsonObject.getString(PLOT_KEY),
                        movieJsonObject.getString(RELEASE_DATE_KEY),
                        movieJsonObject.getDouble(VOTE_AVERAGE_KEY)
                );
            }

            return result;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonResult;

            try {
                final String BASE_URL = getString(R.string.movies_api_base_url);
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
                    return getMoviesFromJson(jsonResult);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "ERROR parsing json: ", e);
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

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mImageAdapter.clear();
                mImageAdapter.addAll(movies);
            }
        }
    }
}
