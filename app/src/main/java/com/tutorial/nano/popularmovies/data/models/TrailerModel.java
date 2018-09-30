package com.tutorial.nano.popularmovies.data.models;


import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieTrailer;
import com.tutorial.nano.popularmovies.data.dtos.TrailerDto;

public class TrailerModel {

    private String name;
    private String source;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getIcon() {
        return R.drawable.ic_play_circle_outline;
    }

    public void fill(TrailerDto trailerDto) {
        name = trailerDto.getName();
        source = trailerDto.getSource();
    }
}
