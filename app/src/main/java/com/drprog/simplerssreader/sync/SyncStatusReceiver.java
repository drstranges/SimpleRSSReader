package com.drprog.simplerssreader.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.drprog.simplerssreader.R;

/**
 * Created on 27.02.2015.
 */
public class SyncStatusReceiver extends BroadcastReceiver {
    //public static final String EXTRA_ERROR = "EXTRA_ERROR";
    //public static final String EXTRA_EVENT = "EXTRA_EVENT";

    OnSyncStatusListener listener;

    /**
     * Interface to represent the listener to sync status updates.
     */
    public interface OnSyncStatusListener {
        public void onSyncStarted();

        public void onSyncFinished();

        public void onSyncFailed(String error);
    }

    public SyncStatusReceiver(OnSyncStatusListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            String error;
            SyncManager.SyncStatus syncStatus =
                    (SyncManager.SyncStatus) intent
                            .getSerializableExtra(SyncManager.INTENT_SYNC_STATUS_EXTRA_STATUS);
            switch (syncStatus) {
                case SYNC_START:
                    listener.onSyncStarted();
                    break;
                case SYNC_FINISH:
                    listener.onSyncFinished();
                    break;
                case SYNC_ERROR_NO_INTERNET:
                    error = context.getString(R.string.error_no_internet_connection);
                    listener.onSyncFailed(error);
                    break;
                case SYNC_ERROR:
                    error = intent.getStringExtra(SyncManager.INTENT_SYNC_STATUS_EXTRA_ERROR);
                    if (error == null) {
                        error = "Unknown error!";
                    }
                    listener.onSyncFailed(error);
                    break;
            }
        }
    }
}
