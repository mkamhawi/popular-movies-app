package com.tutorial.nano.popularmovies.fragments;


import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.ReviewsAdapter;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.MovieReview;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MovieReviewsFragment extends Fragment {
    private long mMovieId;
    public List<MovieReview> mReviews;

    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.movie_reviews_list)
    ListView mReviewsList;

    @Inject protected Application mApplication;

    @Inject MovieDao mMovieDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getActivity().getApplication()).getAppComponent().inject(this);
        mReviews = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null) {
            mMovieId = arguments.getLong("movieId");
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        ButterKnife.bind(this, rootView);

        mReviewsAdapter = new ReviewsAdapter(getContext(), R.id.movie_reviews_list, mReviews);
        mReviewsList.setAdapter(mReviewsAdapter);
        mReviewsList.setEmptyView(rootView.findViewById(R.id.no_reviews_message));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetReviewsFromDbTask().execute();
    }

    @OnItemClick((R.id.movie_reviews_list))
    public void onReviewClicked(AdapterView<?> parent, View view, int position, long id) {

        MovieReview review = (MovieReview) parent.getItemAtPosition(position);

        if (review == null) {
            return;
        }

        ((MasterActivityCallback) getActivity()).onItemSelected(
                review.getId(),
                mMovieId,
                MovieReviewsFragment.class.getSimpleName()
        );
    }

    public class GetReviewsFromDbTask extends AsyncTask<Void, Void, List<MovieReview>> {
        @Override
        protected List<MovieReview> doInBackground(Void... params) {
            return mMovieDao.queryBuilder()
                    .where(MovieDao.Properties.Id.eq(mMovieId))
                    .unique().getReviews();
        }

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            mReviews.clear();
            mReviews.addAll(reviews);
            mReviewsAdapter.notifyDataSetChanged();
        }
    }
}
