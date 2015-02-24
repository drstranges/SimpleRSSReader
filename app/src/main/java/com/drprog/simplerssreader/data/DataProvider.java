package com.drprog.simplerssreader.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * ContentProvider for access to the database
 */
public class DataProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    //Constants for UriMatcher
    static final int CODE_STORY = 100;
    static final int CODE_STORY_WITH_START_DATE = 101;

    private static final SQLiteQueryBuilder sStoryByDateQueryBuilder;

    static{
        sStoryByDateQueryBuilder = new SQLiteQueryBuilder();
        sStoryByDateQueryBuilder.setTables(DataContract.StoryEntry.TABLE_NAME);
        //Will be modified when there are more tables
    }

    private static final String sPubDateSelection =
            DataContract.StoryEntry.TABLE_NAME + "."
                    + DataContract.StoryEntry.COLUMN_PUB_DATE + " >= ? ";

    private Cursor getStoriesByStartDate(Uri uri, String[] projection, String sortOrder) {
        long startDate = DataContract.StoryEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = null;
            selectionArgs = new String[]{};
        } else {
            selectionArgs = new String[]{Long.toString(startDate)};
            selection = sPubDateSelection;
        }

        return sStoryByDateQueryBuilder.query(mDbHelper.getReadableDatabase(),
                                                           projection,
                                                           selection,
                                                           selectionArgs,
                                                           null,
                                                           null,
                                                           sortOrder
        );
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DataContract.StoryEntry.ENTRY_PATH, CODE_STORY);
        matcher.addURI(authority, DataContract.StoryEntry.ENTRY_PATH + "/#",
                       CODE_STORY_WITH_START_DATE);
        return matcher;
    }



    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CODE_STORY:
                return DataContract.StoryEntry.CONTENT_TYPE;
            case CODE_STORY_WITH_START_DATE:
                return DataContract.StoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Cursor cur;
        switch (sUriMatcher.match(uri)) {
            // "stories/#"
            case CODE_STORY_WITH_START_DATE:
            {
                cur = getStoriesByStartDate(uri, projection, sortOrder);
                break;
            }
            // "stories"
            case CODE_STORY: {
                cur = mDbHelper.getReadableDatabase().query(
                        DataContract.StoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cur.setNotificationUri(getContext().getContentResolver(), uri);
        return cur;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CODE_STORY: {
                //normalize Date if needed
                long id = db.insert(DataContract.StoryEntry.TABLE_NAME, null, values);
                if ( id > 0 )
                    returnUri = DataContract.StoryEntry.buildItemUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            //case another table
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // To remove all rows and get a count pass "1" as the whereClause.
        if ( null == selection ) selection = "1";
        switch (match) {
            case CODE_STORY:
                rowsDeleted = db.delete(
                        DataContract.StoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            //case another table
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CODE_STORY:
                //normalize Date if needed
                rowsUpdated = db.update(DataContract.StoryEntry.TABLE_NAME, values, selection,
                                        selectionArgs);
                break;
            //case another table
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_STORY:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalize Date if needed
                        long id = db.insert(DataContract.StoryEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
