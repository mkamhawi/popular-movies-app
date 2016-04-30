package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.FragmentMain;
import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;

public class MainActivity extends AppCompatActivity implements FragmentMain.Callback {

    private String moviesCategory;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moviesCategory = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragmentmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if(id == R.id.action_refresh) {
            ((FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment_main)).updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String category = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        if(!category.equals(moviesCategory)) {
            FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if(fragmentMain != null) {
                fragmentMain.updateMovieList();
            }
            moviesCategory = category;
        }
    }

    @Override
    public void onItemSelected(long entryId, long movieId) {
        if(mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putLong("entryId", entryId);
            arguments.putLong("movieId", movieId);

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra("entryId", entryId)
                    .putExtra("movieId", movieId);
            startActivity(intent);
        }
    }
}
