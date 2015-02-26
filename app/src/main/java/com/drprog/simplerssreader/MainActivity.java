package com.drprog.simplerssreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;


public class MainActivity extends ActionBarActivity implements MainFragment.Callback{

    private Toolbar mToolbar;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        if (findViewById(R.id.detail_container) != null) {
            // The detail container will be present only in the large-screen layouts
            // (res/layout-sw600dp).
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DetailFragment.TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
            //getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public void onItemSelected(String detailUrl) {
        if (mTwoPane) {

            DetailFragment fragment = DetailFragment.newInstance(detailUrl);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DETAIL_URL, detailUrl);
            startActivity(intent);
        }
    }

}
