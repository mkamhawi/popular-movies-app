package com.tutorial.nano.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    int id;
    String title;
    String posterUrl;
    String plotSynopsis;
    String releaseDate;
    double voteAverage;

    public Movie(
            int id,
            String title,
            String posterUrl,
            String plotSynopsis,
            String releaseDate,
            double voteAverage)
    {
        if(title == null) { throw new IllegalArgumentException("title can not be null."); }
        if(posterUrl == null) { throw new IllegalArgumentException("posterUrl can not be null."); }
        if(plotSynopsis == null) { throw new IllegalArgumentException("plotSynopsis can not be null."); }
        if(releaseDate == null) { throw new IllegalArgumentException("releaseDate can not be null."); }

        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.posterUrl);
        dest.writeString(this.plotSynopsis);
        dest.writeString(this.releaseDate);
        dest.writeDouble(this.voteAverage);
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.posterUrl = in.readString();
        this.plotSynopsis = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readDouble();
    }
}
