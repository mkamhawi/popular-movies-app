package com.tutorial.nano.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.Movie;

import java.util.List;

public class MoviesAdapter extends ArrayAdapter<Movie> {
    public MoviesAdapter(Context context, int resource, List<Movie> movies) {
        super(context, resource, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movies_grid_item, parent, false);
        }
        ImageView posterView = (ImageView) convertView.findViewById(R.id.movies_grid_item);
        Picasso.with(getContext()).load(getItem(position).getPosterUrl()).into(posterView);
        return convertView;
    }
}
