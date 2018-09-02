package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.MovieReviewsFragment;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;

public class MovieReviewsActivity extends AppCompatActivity implements MasterActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putLong("movieId", intent.getExtras().getLong("movieId"));

            MovieReviewsFragment detailFragment = new MovieReviewsFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_reviews_container, detailFragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(long entryId, long apiId, String sourceFragmentName) {
                Intent intent = new Intent(this, SingleReviewActivity.class)
                        .putExtra("entryId", entryId);
                startActivity(intent);
    }
}
