package com.tutorial.nano.popularmovies.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.models.ReviewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleReviewFragment extends Fragment {

    @BindView(R.id.review_author_name)
    TextView mAuthorName;

    @BindView(R.id.review_content)
    TextView mReviewContent;

    ReviewModel reviewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_single_review, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if(arguments != null) {
            reviewModel = (ReviewModel) arguments.getSerializable("review");
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (reviewModel != null) {
            mAuthorName.setText(reviewModel.getAuthor());
            mReviewContent.setText(reviewModel.getContent());
        }
    }
}
