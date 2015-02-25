package com.drprog.simplerssreader;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.drprog.simplerssreader.data.DataContract;

import java.util.Date;
import java.util.Vector;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        //test
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.test){
            test();
        }

        return super.onOptionsItemSelected(item);
    }

    private void test() {
        Vector<ContentValues> vector = new Vector<ContentValues>();
        long date = (new Date()).getTime();
        for (int i=0; i<10; i++) {
            ContentValues cv = new ContentValues();

            cv.put(DataContract.StoryEntry.COLUMN_TITLE, "TestTitle" + i);
            cv.put(DataContract.StoryEntry.COLUMN_PUB_DATE, date + i*1000*60*15);
            cv.put(DataContract.StoryEntry.COLUMN_AUTHOR, "TestAuthor");
            cv.put(DataContract.StoryEntry.COLUMN_IMG_URL,
                   "http://i.cbc.ca/1.2970039.1424812686!/fileImage/httpImage/image.jpg_gen/derivatives/16x9_460/parliament-hill-security.jpg");
            cv.put(DataContract.StoryEntry.COLUMN_LINK,
                   "http://www.cbc.ca/news/world/isis-recruited-canadian-woman-to-join-fight-in-syria-1.2970535?cmp=rss" +i);

            vector.add(cv);
        }

        ContentValues[] cvArray = new ContentValues[vector.size()];
        vector.toArray(cvArray);
        getContentResolver().bulkInsert(DataContract.StoryEntry.CONTENT_URI, cvArray);
    }

}
