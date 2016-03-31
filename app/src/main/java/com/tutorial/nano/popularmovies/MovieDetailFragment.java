package com.tutorial.nano.popularmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Movie movie = (Movie) this.getActivity().getIntent().getExtras().getParcelable("Movie");

        TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
        title.setText(movie.title);

        TextView releaseDate = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
        releaseDate.setText(movie.releaseDate);

        TextView voteAverage = (TextView) rootView.findViewById(R.id.movie_detail_vote_average);
        voteAverage.setText(Double.toString(movie.voteAverage));

        ImageView poster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
        Picasso.with(getContext()).load(movie.posterUrl).into(poster);

        TextView plot = (TextView) rootView.findViewById(R.id.movie_detail_plot);
        plot.setText(movie.plotSynopsis);
        return rootView;
    }

}
