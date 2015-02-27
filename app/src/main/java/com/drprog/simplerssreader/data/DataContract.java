package com.drprog.simplerssreader.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class that defines tables and column names
 * for the database and ContentProvider.
 */
public class DataContract {

    public static final String CONTENT_AUTHORITY = "com.drprog.simplerssreader";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Class that defines the table contents of the story table.
     */
    public static final class StoryEntry implements BaseColumns {
        public static final String ENTRY_PATH = "stories";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(ENTRY_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + ENTRY_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + ENTRY_PATH;


        // Database Table name
        public static final String TABLE_NAME = "Stories";
        // Column names
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_AUTHOR = "Author";
        public static final String COLUMN_PUB_DATE = "PubDate";
        public static final String COLUMN_IMG_URL = "ImageUrl";
        public static final String COLUMN_LINK = "Link";


        // Helper methods
        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriWithStartDate(long startDate) {
            //normalizeDate if needed
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_PUB_DATE, Long.toString(startDate)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_PUB_DATE);
            if (null != dateString && dateString.length() > 0) {
                return Long.parseLong(dateString);
            } else { return 0; }
        }
    }


}
