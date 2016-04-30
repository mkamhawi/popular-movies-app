package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.SingleReviewFragment;

public class SingleReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_review);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putLong("entryId", intent.getExtras().getLong("entryId"));

            SingleReviewFragment detailFragment = new SingleReviewFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.single_review_containter, detailFragment)
                    .commit();
        }
    }
}
