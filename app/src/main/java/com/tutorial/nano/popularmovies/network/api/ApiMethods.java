package com.tutorial.nano.popularmovies.network.api;

import com.tutorial.nano.popularmovies.data.dtos.MovieCollectionDto;
import com.tutorial.nano.popularmovies.data.dtos.MovieDetailsDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiMethods {
    @GET("3/movie/{sortPreference}")
    Call<MovieCollectionDto> getMovies(@Path("sortPreference") String sortPreference);

    @GET("3/movie/{movieId}")
    Call<MovieDetailsDto> getMovieDetails(
            @Path("movieId") String movieId,
            @Query("append_to_response") String appendToResponse
    );
}
