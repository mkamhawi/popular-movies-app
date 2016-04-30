package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;

public class MovieDetailActivity extends AppCompatActivity implements MasterActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putLong("entryId", intent.getExtras().getLong("entryId"));
            arguments.putLong("movieId", intent.getExtras().getLong("movieId"));

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(long entryId, long apiId, String sourceFragmentName) {
        Intent reviews = new Intent(this, MovieReviewsActivity.class)
                .putExtra("movieId", apiId);
        startActivity(reviews);
    }
}
