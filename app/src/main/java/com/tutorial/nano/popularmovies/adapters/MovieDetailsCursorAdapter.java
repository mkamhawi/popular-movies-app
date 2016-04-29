package com.tutorial.nano.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;
import com.tutorial.nano.popularmovies.R;

public class MovieDetailsCursorAdapter extends CursorAdapter {

    public MovieDetailsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view  = LayoutInflater.from(context).inflate(R.layout.trailers_list_item, parent, false);
        view.setTag(new TrailerViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerViewHolder trailerView = (TrailerViewHolder) view.getTag();
        trailerView.trailerName.setText(cursor.getString(MovieDetailFragment.COL_TRAILER_NAME_INDEX));
    }

    public static class TrailerViewHolder{
        public final TextView trailerName;
        public TrailerViewHolder(View view){
            trailerName = (TextView) view.findViewById(R.id.trailer_name);
        }
    }
}
