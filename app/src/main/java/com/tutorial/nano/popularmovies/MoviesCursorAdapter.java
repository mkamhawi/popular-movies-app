package com.tutorial.nano.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MoviesCursorAdapter extends CursorAdapter {

    public MoviesCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movies_grid_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String posterUrl = cursor.getString(FragmentMain.COL_POSTER_URL_ID);
        Picasso.with(context).load(posterUrl).into((ImageView) view);
    }
}
