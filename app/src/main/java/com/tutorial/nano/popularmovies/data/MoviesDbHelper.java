package com.tutorial.nano.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.tutorial.nano.popularmovies.data.MoviesContract.*;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

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

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
