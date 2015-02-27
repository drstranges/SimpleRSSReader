package com.drprog.simplerssreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.drprog.simplerssreader.data.DataContract.StoryEntry;

/**
 * A helper class fo managing database creation and version management.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;

    private static final String SCRIPT_CREATE_TABLE_STORY = "CREATE TABLE " +
            StoryEntry.TABLE_NAME + " ( " +
            StoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            StoryEntry.COLUMN_PUB_DATE + " INTEGER NOT NULL, " +
            StoryEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            StoryEntry.COLUMN_LINK + " TEXT NOT NULL, " +
            StoryEntry.COLUMN_AUTHOR + " TEXT, " +
            StoryEntry.COLUMN_IMG_URL + " TEXT, " +
            " UNIQUE(" + StoryEntry.COLUMN_LINK + ") ON CONFLICT REPLACE);";


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TABLE_STORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do nothing, this is the first version.
    }

}
