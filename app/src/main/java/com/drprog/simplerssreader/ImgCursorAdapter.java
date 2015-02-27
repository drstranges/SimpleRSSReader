package com.drprog.simplerssreader;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.drprog.simplerssreader.data.DataContract;
import com.drprog.simplerssreader.sync.ConnectionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom cursor adapter with image loading
 */
public class ImgCursorAdapter extends CursorAdapter {

    private static final SimpleDateFormat PUB_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM");
    private static final SimpleDateFormat PUB_DATE_FORMAT_TODAY = new SimpleDateFormat("HH:mm");

    private ViewHolder viewHolder;

    public ImgCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item, parent,false);

        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        viewHolder = (ViewHolder) view.getTag();

        String imageUrl =
                cursor.getString(cursor.getColumnIndex(DataContract.StoryEntry.COLUMN_IMG_URL));
        String title =
                cursor.getString(cursor.getColumnIndex(DataContract.StoryEntry.COLUMN_TITLE));
        long pubDate =
                cursor.getLong(cursor.getColumnIndex(DataContract.StoryEntry.COLUMN_PUB_DATE));
        String author =
                cursor.getString(cursor.getColumnIndex(DataContract.StoryEntry.COLUMN_AUTHOR));

        viewHolder.imageView.setImageUrl(imageUrl,
                                         ConnectionManager.getInstance(context).getImageLoader());
        viewHolder.imageView.setDefaultImageResId(android.R.drawable.picture_frame);
        viewHolder.titleView.setText(title);
        String prefAuthor = (author == null || author.trim().isEmpty()) ? "" : "@";
        viewHolder.authorView.setText(prefAuthor + author);
        viewHolder.dateView.setText(formatDisplayDate(pubDate));
    }

    private String formatDisplayDate(long pubDate) {
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(pubDate, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        if (julianDay == currentJulianDay) {
            return PUB_DATE_FORMAT_TODAY.format(new Date(pubDate));
        } else {
            return PUB_DATE_FORMAT.format(new Date(pubDate));
        }
    }

    static class ViewHolder {
        NetworkImageView imageView;
        TextView titleView;
        TextView dateView;
        TextView authorView;

        public ViewHolder(View view) {
            imageView = (NetworkImageView) view.findViewById(R.id.imageView);
            titleView = (TextView) view.findViewById(R.id.titleView);
            dateView = (TextView) view.findViewById(R.id.dateView);
            authorView = (TextView) view.findViewById(R.id.authorView);
        }
    }


}
