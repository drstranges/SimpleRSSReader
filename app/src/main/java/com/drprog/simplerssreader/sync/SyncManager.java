package com.drprog.simplerssreader.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.drprog.simplerssreader.data.DataContract;
import com.drprog.simplerssreader.utils.Utils;

import org.xmlpull.v1.XmlPullParserException;

/**
 * Synchronization Manager
 */
public class SyncManager {

    private static final String TARGET_URL = "http://www.cbc.ca/cmlink/rss-topstories";
    private static final String INTENT_SYNC_STATUS_ACTION =
            "com.drprog.simplerssreader.SYNC_STATUS";
    private static final String INTENT_SYNC_STATUS_EXTRA_STATUS = "INTENT_SYNC_STATUS_EXTRA_STATUS";
    private static final String INTENT_SYNC_STATUS_EXTRA_ERROR = "INTENT_SYNC_STATUS_EXTRA_ERROR";
    private static final String REQUEST_TAG = "REQUEST_TAG";


    // Enum class for representing sync status
    public static enum SyncStatus {
        SYNC_START,
        SYNC_FINISH,
        SYNC_ERROR,
        SYNC_ERROR_NO_INTERNET,
        NONE
    }

    /**
     * Method for initiating the process of synchronization.
     * @param context    application context
     */
    public static void startSync(Context context) {
        if (!ConnectionManager.isOnline(context)) {
            //Internet connection is not available
            sendSyncStatusBroadcast(context,SyncStatus.SYNC_ERROR_NO_INTERNET,null);
            return;
        }

        Request request = getRequest(context, TARGET_URL);
        request.setTag(REQUEST_TAG);

        ConnectionManager cManager = ConnectionManager.getInstance(context);
        cManager.cancelAllRequests(REQUEST_TAG);
        sendSyncStatusBroadcast(context, SyncStatus.SYNC_START, null);
        cManager.addToRequestQueue(request);
    }

    /**
     * Method for building the Volley Request.
     * @param context    application context
     * @param url        the target url
     * @return the Volley Request
     */
    private static Request getRequest(Context context, String url) {
        final Context ctx = context;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(ctx, response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processError(ctx, error);
            }
        };
        return new StringRequest(Request.Method.GET, url, responseListener, errorListener);
    }

    /**
     * Method for processing the response from the host
     * @param context     application context
     * @param response    String witch was taken as response on request to the host.
     */
    private static void processResponse(Context context, String response) {
        Log.d(Utils.LOG_TAG, response);
        ContentValues[] cvArray = new ContentValues[0];
        try {
            cvArray = ParserHelper.parseXML(context, response);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            sendSyncStatusBroadcast(context, SyncStatus.SYNC_ERROR, new VolleyError(e.getMessage()));
            return;
        }
        if (cvArray.length > 0) {
            context.getContentResolver().bulkInsert(DataContract.StoryEntry.CONTENT_URI, cvArray);
        }
        sendSyncStatusBroadcast(context, SyncStatus.SYNC_FINISH, null);
    }

    /**
     * Method for processing any error witch were taken in process of requesting.
     * @param context    application context
     * @param error      {@link VolleyError} witch was taken in process of requesting
     */
    private static void processError(Context context, VolleyError error) {
        sendSyncStatusBroadcast(context, SyncStatus.SYNC_ERROR, error);
    }

    /**
     * Method for sending SyncStatusBroadcast
     * @param context    application context
     * @param status     {@link SyncStatus} Can be set as SYNC_START, SYNC_FINISH or SYNC_ERROR.
     * @param error      {@link VolleyError} witch was taken in process of preparing
     *   or making the request. Need if {@param status} is set as SYNC_ERROR, can be NULL otherwise.
     */
    public static void sendSyncStatusBroadcast(Context context, SyncStatus status,
            VolleyError error) {
        Intent intent = new Intent(INTENT_SYNC_STATUS_ACTION);
        intent.putExtra(INTENT_SYNC_STATUS_EXTRA_STATUS, status);
        if (error != null) {
            String errorMessage = ConnectionManager.getErrorMessage(context, error);
            intent.putExtra(INTENT_SYNC_STATUS_EXTRA_ERROR, errorMessage);
        }
        context.sendBroadcast(intent);
    }


}
