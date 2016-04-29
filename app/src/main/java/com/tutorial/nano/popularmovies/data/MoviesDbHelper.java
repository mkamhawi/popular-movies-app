package com.tutorial.nano.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.tutorial.nano.popularmovies.data.MoviesContract.*;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // The Movies table is used for caching, while the Favorites table is used as a permanent storage
        // for the user's favorite movies.
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE "
                + MovieEntry.TABLE_NAME + " ("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE"
                + ");";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE "
                + FavoriteEntry.TABLE_NAME + " ("
                + FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_PLOT + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + "UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE"
                + ");";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE "
                + TrailerEntry.TABLE_NAME + " ("
                + TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + TrailerEntry.COLUMN_SIZE + " TEXT NOT NULL, "
                + TrailerEntry.COLUMN_SOURCE + " TEXT NOT NULL, "
                + TrailerEntry.COLUMN_TYPE + " TEXT NOT NULL"
                + ");";

        final String SQL_CREATE_TRAILERS_INDEX = "CREATE INDEX "
                + TrailerEntry.INDEX_MOVIE_ID + " ON "
                + TrailerEntry.TABLE_NAME + " (" + TrailerEntry.COLUMN_MOVIE_ID + ");";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE "
                + ReviewEntry.TABLE_NAME + " ("
                + ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, "
                + ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, "
                + ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL"
                + ");";

        final String SQL_CREATE_REVIEWS_INDEX = "CREATE INDEX "
                + ReviewEntry.INDEX_MOVIE_ID + " ON "
                + ReviewEntry.TABLE_NAME + " (" + ReviewEntry.COLUMN_MOVIE_ID + ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_INDEX);
        db.execSQL(SQL_CREATE_REVIEWS_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
