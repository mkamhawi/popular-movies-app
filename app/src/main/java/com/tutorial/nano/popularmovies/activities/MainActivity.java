package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.FragmentMain;
import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;
import com.tutorial.nano.popularmovies.fragments.MovieReviewsFragment;
import com.tutorial.nano.popularmovies.fragments.SingleReviewFragment;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MasterActivityCallback {

    private String moviesCategory;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private DrawerLayout mDrawerLayout;

    @Inject protected SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        moviesCategory = mSharedPreferences.getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));

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

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                updateSortingOrder(item.getTitle().toString());
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(long entryId, long apiId, String sourceFragmentName) {
        if(mTwoPane) {
            if(sourceFragmentName.equals(FragmentMain.class.getSimpleName())) {
                Bundle arguments = new Bundle();
                arguments.putLong("entryId", entryId);
                arguments.putLong("movieId", apiId);

                MovieDetailFragment detailFragment = new MovieDetailFragment();
                detailFragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            }

            if(sourceFragmentName.equals(MovieDetailFragment.class.getSimpleName())) {
                Bundle arguments = new Bundle();
                arguments.putLong("movieId", apiId);

                MovieReviewsFragment detailFragment = new MovieReviewsFragment();
                detailFragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            }

            if(sourceFragmentName.equals(MovieReviewsFragment.class.getSimpleName())) {
                Bundle arguments = new Bundle();
                arguments.putLong("entryId", entryId);

                SingleReviewFragment detailFragment = new SingleReviewFragment();
                detailFragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            if(sourceFragmentName.equals(FragmentMain.class.getSimpleName())) {
                Intent intent = new Intent(this, MovieDetailActivity.class)
                        .putExtra("entryId", entryId)
                        .putExtra("movieId", apiId);
                startActivity(intent);
            }
        }
    }

    private void updateSortingOrder(String selectedSortOrder) {
        selectedSortOrder = selectedSortOrder.toLowerCase().replace(" ", "_");

        if (!selectedSortOrder.equals(moviesCategory)) {
            FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if(fragmentMain != null) {
                fragmentMain.updateMovieList(selectedSortOrder);
            }
            moviesCategory = selectedSortOrder;
        }
    }
}
