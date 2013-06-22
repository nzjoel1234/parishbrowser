package com.patriot.restprocessor.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public abstract class TableContentProviderBase extends ContentProvider
{
    private SQLiteOpenHelper mDatabase;

    private static final int ITEMS = 1;
    private static final int ITEM = 2;

    private final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private String mBasePath;
    private Uri mContentUri;
    private String mTableName;
    private String mIdColumn;
    private String mItemsType;
    private String mItemType;
 
    protected void initialise(SQLiteOpenHelper database, String authority, String basePath, Uri contentUri, String tableName, String idColumn, String itemsType, String itemType)
    {
        mBasePath = basePath;
        mContentUri = contentUri;
        mTableName = tableName;
        mIdColumn = idColumn;
        mItemsType = itemsType;
        mItemType = itemType;

        mUriMatcher.addURI(authority, basePath, ITEMS);
        mUriMatcher.addURI(authority, basePath + "/#", ITEM);

        mDatabase = database;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(mTableName);

        int uriType = mUriMatcher.match(uri);
        switch (uriType)
        {
        case ITEMS:
            break;

        case ITEM:
            // Adding the ID to the original query
            queryBuilder.appendWhere(mIdColumn + "="
                    + uri.getLastPathSegment());
            break;

        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabase.getReadableDatabase();

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (mUriMatcher.match(uri)) {
        case ITEMS:
            return mItemsType;

        case ITEM:
            return mItemType;

        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        int uriType = mUriMatcher.match(uri);

        SQLiteDatabase db = mDatabase.getWritableDatabase();

        long id = 0;
        switch (uriType)
        {
        case ITEMS:
            id = db.insert(mTableName, null, values);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(mBasePath + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int uriType = mUriMatcher.match(uri);
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType)
        {
        case ITEMS:
            rowsDeleted = db.delete(mTableName, selection,
                    selectionArgs);
            break;

        case ITEM:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsDeleted = db.delete(mTableName,
                        mIdColumn + "=" + id,
                        null);
            }
            else
            {
                rowsDeleted = db.delete(mTableName,
                        mIdColumn + "=" + id
                        + " and " + selection,
                        selectionArgs);
            }
            // We should notify this individual URI but we will also notify that the list has changed
            getContext().getContentResolver().notifyChange(uri, null);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(mContentUri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        int uriType = mUriMatcher.match(uri);
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType)
        {
        case ITEMS:
            rowsUpdated = db.update(mTableName,
                    values,
                    selection,
                    selectionArgs);
            break;

        case ITEM:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsUpdated = db.update(mTableName,
                        values,
                        mIdColumn + "=" + id,
                        null);
            }
            else
            {
                rowsUpdated = db.update(mTableName,
                        values,
                        mIdColumn + "=" + id
                        + " and "
                        + selection,
                        selectionArgs);
            }
            // We should notify this individual URI but we will also notify that the list has changed
            getContext().getContentResolver().notifyChange(uri, null);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(mContentUri, null);
        return rowsUpdated;
    }

    protected abstract String[] getAvailableColumns();

    private void checkColumns(String[] projection)
    {
        String[] available = getAvailableColumns();

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
