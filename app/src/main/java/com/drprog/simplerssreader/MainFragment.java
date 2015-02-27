package com.drprog.simplerssreader;

import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drprog.simplerssreader.data.DataContract;
import com.drprog.simplerssreader.sync.SyncManager;
import com.drprog.simplerssreader.sync.SyncStatusReceiver;
import com.drprog.simplerssreader.utils.Utils;

/**
 * Main screen with the List of Stories
 */
public class MainFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SyncStatusReceiver.OnSyncStatusListener {

    private static final int STORIES_LOADER = 201;
    private static final String[] STORY_COLUMNS = {
            DataContract.StoryEntry._ID,
            DataContract.StoryEntry.COLUMN_TITLE,
            DataContract.StoryEntry.COLUMN_PUB_DATE,
            DataContract.StoryEntry.COLUMN_AUTHOR,
            DataContract.StoryEntry.COLUMN_IMG_URL,
            DataContract.StoryEntry.COLUMN_LINK
    };

    private static final String KEY_SELECTED_POSITION = "KEY_SELECTED_POSITION";

    private SyncStatusReceiver mSyncStatusReceiver;
    private IntentFilter mIntentFilter = new IntentFilter(SyncManager.INTENT_SYNC_STATUS_ACTION);
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private TextView mEmptyView;
    private ImgCursorAdapter mCursorAdapter;
    private int mPosition = ListView.INVALID_POSITION;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback when an item has been selected.
         */
        public void onItemSelected(String detailUrl);
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        mListView = (ListView) rootView.findViewById(R.id.story_list);
        mEmptyView = (TextView) rootView.findViewById(android.R.id.empty);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        mCursorAdapter = new ImgCursorAdapter(getActivity(), null,
                                              SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mListView.setAdapter(mCursorAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                    long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndex(DataContract.StoryEntry.COLUMN_LINK);
                    String link = cursor.getString(columnIndex);
                    Log.d(Utils.LOG_TAG, "Link: " + link);
                    ((Callback) getActivity())
                            .onItemSelected(link);
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SELECTED_POSITION)) {
            mPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startSync();
            }
        });

        mSyncStatusReceiver = new SyncStatusReceiver(this);

        getLoaderManager().initLoader(STORIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mSyncStatusReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mSyncStatusReceiver);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear_data) {
            clearDatabase();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearDatabase() {
        mPosition = ListView.INVALID_POSITION;
        ((Callback) getActivity()).onItemSelected(null);
        getActivity().getContentResolver().delete(DataContract.StoryEntry.CONTENT_URI,
                                                  null,
                                                  new String[]{});
    }

    private void startSync() {
        SyncManager.startSync(getActivity().getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(KEY_SELECTED_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = DataContract.StoryEntry.COLUMN_PUB_DATE + " DESC";
        Uri storyListUri = DataContract.StoryEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                                storyListUri,
                                STORY_COLUMNS,
                                null,
                                null,
                                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        if (data.getCount() > 0) {
            mPosition = ListView.INVALID_POSITION;
            mEmptyView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSyncStarted() {
        //Do nothing
    }

    @Override
    public void onSyncFinished() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSyncFailed(String error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

}
