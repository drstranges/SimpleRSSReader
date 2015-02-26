package com.drprog.simplerssreader;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;


public class DetailActivity extends ActionBarActivity {

    public static final String DETAIL_URL = "DETAIL_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            String storyUrl = getIntent().getStringExtra(DETAIL_URL);

            DetailFragment fragment = DetailFragment.newInstance(storyUrl);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }

}
