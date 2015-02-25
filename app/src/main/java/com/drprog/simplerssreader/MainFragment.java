package com.drprog.simplerssreader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.drprog.simplerssreader.data.DataContract;

/**
 * Created on 25.02.2015.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STORIES_LOADER = 201;
    private static final String[] STORY_COLUMNS = {
            DataContract.StoryEntry.TABLE_NAME + "." + DataContract.StoryEntry._ID,
            DataContract.StoryEntry.COLUMN_TITLE,
            DataContract.StoryEntry.COLUMN_PUB_DATE,
            DataContract.StoryEntry.COLUMN_AUTHOR,
            DataContract.StoryEntry.COLUMN_IMG_URL
    };


    private Toolbar mToolbar;
    //private RecyclerView recyclerView;
    private ListView mListView;
    private SimpleCursorAdapter mCursorAdapter;
    //private LinearLayoutManager mLinearLayoutManager;


    public MainFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mToolbar != null) {
            ((ActionBarActivity) getActivity()).setSupportActionBar(mToolbar);
        }

        String[] from = new String[]{
                DataContract.StoryEntry.COLUMN_TITLE,
                DataContract.StoryEntry.COLUMN_PUB_DATE,
                DataContract.StoryEntry.COLUMN_AUTHOR
        };
        int[] to = new int[]{
                R.id.titleView,
                R.id.timeView,
                R.id.authorView
        };
        mCursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.list_item,
                                                null,from,to,
                                                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mListView.setAdapter(mCursorAdapter);
        //mViewAdapter = new StoryAdapter(this, null);
        //recyclerView.setAdapter(mViewAdapter);
//        recyclerView
//                .addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //recyclerView.setHasFixedSize(true);
        //mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        getLoaderManager().initLoader(STORIES_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.app_bar);

        //recyclerView = (RecyclerView) rootView.findViewById(R.id.story_list);
        mListView = (ListView) rootView.findViewById(R.id.story_list);

        return rootView;
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
//        if (mPosition != ListView.INVALID_POSITION) {
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}