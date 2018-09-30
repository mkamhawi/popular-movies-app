package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putLong("movieId", intent.getExtras().getLong("movieId"));

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, detailFragment)
                    .commit();
        }
    }
}
