package com.tutorial.nano.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.fragments.FragmentMain;

public class MainActivity extends AppCompatActivity {

    private String moviesCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moviesCategory = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
