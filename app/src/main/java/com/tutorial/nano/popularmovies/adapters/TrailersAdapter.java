package com.tutorial.nano.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieTrailer;

import java.util.List;

public class TrailersAdapter extends ArrayAdapter<MovieTrailer> {
    public TrailersAdapter(Context context, int resource, List<MovieTrailer> trailers) {
        super(context, resource, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.trailer_list_item, parent, false);
        }

        TextView trailerName = (TextView) convertView.findViewById(R.id.trailer_name);
        trailerName.setText(getItem(position).getName());
        return convertView;
    }
}
