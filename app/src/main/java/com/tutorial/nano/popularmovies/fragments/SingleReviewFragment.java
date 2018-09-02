package com.tutorial.nano.popularmovies.fragments;


import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieReview;
import com.tutorial.nano.popularmovies.data.MovieReviewDao;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleReviewFragment extends Fragment {

    private long mReviewId;

    @BindView(R.id.review_author_name)
    TextView mAuthorName;

    @BindView(R.id.review_content)
    TextView mReviewContent;

    @Inject protected Application mApplication;

    @Inject MovieReviewDao mMovieReviewDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null) {
            mReviewId = arguments.getLong("entryId");
        }

        View rootView = inflater.inflate(R.layout.fragment_single_review, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetReviewFromDbTask().execute();
    }

    public class GetReviewFromDbTask extends AsyncTask<Void, Void, MovieReview> {
        @Override
        protected MovieReview doInBackground(Void... params) {
            return mMovieReviewDao.queryBuilder()
                    .where(MovieReviewDao.Properties.Id.eq(mReviewId))
                    .unique();
        }

        @Override
        protected void onPostExecute(MovieReview review) {
            mAuthorName.setText(review.getAuthor());
            mReviewContent.setText(review.getContent());
        }
    }
}
