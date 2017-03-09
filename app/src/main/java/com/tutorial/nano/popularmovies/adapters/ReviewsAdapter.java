package com.tutorial.nano.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieReview;

import java.util.List;

public class ReviewsAdapter extends ArrayAdapter<MovieReview> {
    public ReviewsAdapter(Context context, int resource, List<MovieReview> reviews) {
        super(context, resource, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.reviews_list_item, parent, false);
        }
        MovieReview review = getItem(position);

        TextView authorName = (TextView) convertView.findViewById(R.id.review_author_name);
        authorName.setText(review.getAuthor());

        TextView content = (TextView) convertView.findViewById(R.id.review_content);
        content.setText(review.getContent());

        return convertView;
    }
}
