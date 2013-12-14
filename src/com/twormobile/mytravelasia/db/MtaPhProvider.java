package com.twormobile.mytravelasia.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.twormobile.mytravelasia.model.Poi;

/**
 * Content provider for MyTravelAsia Philippines. It handles queries, updates, inserts, and deletes, but most
 * importantly, it notifies listeners such as Loaders for data change events.
 *
 * @author avendael
 */
public class MtaPhProvider extends ContentProvider {
    private static final String TAG = MtaPhProvider.class.getSimpleName();

    private static final String SCHEME = "content://";
    private static final String AUTHORITY = "com.twormobile.mytravelasia.db.MtaPhProvider";

    private static final int POI_TABLE = 0;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final Uri POI_URI = Uri.parse(SCHEME + AUTHORITY + "/" + Poi.TABLE_NAME);

    static {
        sUriMatcher.addURI(AUTHORITY, Poi.TABLE_NAME, POI_TABLE);
    }

    private DatabaseHandler mDbHandler;

    @Override
    public boolean onCreate() {
        mDbHandler = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case POI_TABLE:
                queryBuilder.setTables(Poi.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDbHandler.getReadableDatabase(), projection, selection, selectionArgs,
                null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (values == null) throw new IllegalArgumentException("Values cannot be null");

        Uri resultUri = null;
        long id;
        SQLiteDatabase db = mDbHandler.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case POI_TABLE:
                id = db.insertWithOnConflict(Poi.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(POI_URI, id);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                }

                break;
        }

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHandler.getWritableDatabase();
        int count = 0;

        switch (sUriMatcher.match(uri)) {
            case POI_TABLE:
                count = db.delete(Poi.TABLE_NAME, selection, selectionArgs);

                getContext().getContentResolver().notifyChange(POI_URI, null);

                break;
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHandler.getWritableDatabase();
        int count = 0;

        switch (sUriMatcher.match(uri)) {
            case POI_TABLE:
                count = db.update(Poi.TABLE_NAME, values, selection, selectionArgs);
                break;
        }

        return count;
    }
}
