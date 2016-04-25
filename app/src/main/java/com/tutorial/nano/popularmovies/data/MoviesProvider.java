package com.tutorial.nano.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITES = 200;
    static final int FAVORITE_WITH_ID = 201;

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        Cursor resultCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                resultCursor =  db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID: {
                String entryId = MoviesContract.MovieEntry.getEntryIdFromUri(uri);
                String selectionStr = MoviesContract.MovieEntry.TABLE_NAME
                        + "."
                        + MoviesContract.MovieEntry._ID
                        + " = ? ";
                String[] selectionArguments = {entryId};
                resultCursor =  db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selectionStr,
                        selectionArguments,
                        null,
                        null,
                        null
                );
                break;
            }
            case FAVORITES: {
                resultCursor = db.query(
                        MoviesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITE_WITH_ID: {
                String entryId = MoviesContract.FavoriteEntry.getEntryIdFromUri(uri);
                String selectionStr = MoviesContract.FavoriteEntry.TABLE_NAME
                        + "."
                        + MoviesContract.FavoriteEntry._ID
                        + " = ? ";
                String[] selectionArguments = {entryId};
                resultCursor = db.query(
                        MoviesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selectionStr,
                        selectionArguments,
                        null,
                        null,
                        null
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MoviesContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        Uri insertedUri;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if(id > -1) {
                    insertedUri = MoviesContract.MovieEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case FAVORITES: {
                long id = db.insert(MoviesContract.FavoriteEntry.TABLE_NAME, null, values);
                if(id > -1) {
                    insertedUri = MoviesContract.FavoriteEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] bulkValues) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues bulkValue: bulkValues) {
                        long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, bulkValue);
                        if(id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, bulkValues);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITES: {
                rowsDeleted = db.delete(MoviesContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID: {
                String entryId = MoviesContract.FavoriteEntry.getEntryIdFromUri(uri);
                String selectionStr = MoviesContract.FavoriteEntry.TABLE_NAME
                        + "."
                        + MoviesContract.FavoriteEntry._ID
                        + " = ? ";
                String[] selectionArguments = {entryId};
                rowsDeleted = db.delete(MoviesContract.FavoriteEntry.TABLE_NAME, selectionStr, selectionArguments);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case FAVORITES: {
                rowsUpdated = db.update(MoviesContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return matcher;
    }
}