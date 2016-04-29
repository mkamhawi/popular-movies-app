package com.tutorial.nano.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MovieReviewsCursorAdapter extends CursorAdapter {

    public MovieReviewsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view  = LayoutInflater.from(context).inflate(R.layout.reviews_list_item, parent, false);
        view.setTag(new ReviewViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ReviewViewHolder review = (ReviewViewHolder) view.getTag();
        review.authorName.setText(cursor.getString(MovieReviewsFragment.COL_REVIEW_AUTHOR_INDEX));
        review.content.setText(cursor.getString(MovieReviewsFragment.COL_REVIEW_CONTENT_INDEX));
    }

    public static class ReviewViewHolder{
        public final TextView authorName;
        public final TextView content;
        public ReviewViewHolder(View view){
            authorName = (TextView) view.findViewById(R.id.review_author_name);
            content = (TextView) view.findViewById(R.id.review_content);
        }
    }
}
