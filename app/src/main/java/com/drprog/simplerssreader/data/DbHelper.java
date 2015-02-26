package com.drprog.simplerssreader.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.drprog.simplerssreader.data.DataContract.StoryEntry;

/**
 * A helper class fo managing the database.
 * It is Singleton class. Use {@link # getInstance} to instantiate.
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
            StoryEntry. COLUMN_AUTHOR + " TEXT, " +
            StoryEntry.COLUMN_IMG_URL + " TEXT, " +
            " UNIQUE(" + StoryEntry.COLUMN_LINK + ") ON CONFLICT REPLACE);";


    //private static DbHelper sInstance = null;
    //private final Object lock = new Object();

    //private
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Get existed instance of DbHelper class or create new one if exists.
     *
     * @para m context application context
     * @return Instance of DbHelper.
     */
//    public static synchronized DbHelper getInstance(Context context) {
//        if (sInstance == null) {
//            assert context != null;
//            sInstance = new DbHelper(context.getApplicationContext());
//        }
//        return sInstance;
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TABLE_STORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do nothing, this is the first version.
    }

    /**
     * This method check cursor to Null or Empty.
     * If cursor is Empty then execute close.
     * If cursor not Null and not Empty then execute moveToFirst.
     *
     * @param cur opened cursor.
     * @return False if cursor is null or empty, True otherwise.
     */
    public static boolean checkCursor(Cursor cur) {
        if (cur == null) return false;
        if (!cur.moveToFirst()) {
            cur.close();
            return false;
        }
        return true;
    }
}
