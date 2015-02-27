package com.drprog.simplerssreader.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.drprog.simplerssreader.R;

/**
 * Helper class for managing the Internet connections.
 * This is Singleton class. Use {@link #getInstance} to instantiate.
 */
public class ConnectionManager {
    private static ConnectionManager mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private ConnectionManager(Context context) {
        // getApplicationContext() is key, it keeps you from leaking the
        // Activity if someone passes one in.
        // See https://developer.android.com/training/volley/requestqueue.html#singleton
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                                       new ImageLoader.ImageCache() {
                                           private final LruCache<String, Bitmap>
                                                   cache = new LruCache<String, Bitmap>(20);

                                           @Override
                                           public Bitmap getBitmap(String url) {
                                               return cache.get(url);
                                           }

                                           @Override
                                           public void putBitmap(String url, Bitmap bitmap) {
                                               cache.put(url, bitmap);
                                           }
                                       });
    }

    public synchronized static ConnectionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ConnectionManager(context);
        }
        return mInstance;
    }

    /**
     * Method for taking existing {@link RequestQueue}.
     * If it is not exist then it will be created a new.
     *
     * @return existing {@link RequestQueue}
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Check the Internet connection.
     *
     * @param context application context
     * @return true if the Internet connected or in process of connecting, false otherwise.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Check if the WiFi connected.
     *
     * @param context application context
     * @return true if the WiFi connected, false otherwise.
     */
    public static boolean isWiFiConnection(Context context) {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    /**
     * Helper method for retrieving a message, which will be shown to user
     * if {@param error} has been taken.
     *
     * @param context application context
     * @param error   {@link VolleyError} witch was taken in process of preparing
     *                or making the request to the Internet.
     * @return a message to show in Error Dialog.
     */
    public static String getErrorMessage(Context context, VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            return context.getString(R.string.error_connection_timeout);
        } else if (error instanceof AuthFailureError) {
            return context.getString(R.string.error_authentication_failure);
        } else if (error instanceof ServerError) {
            return context.getString(R.string.error_response);
        } else if (error instanceof NetworkError) {
            return context.getString(R.string.error_network);
        } else if (error instanceof ParseError) {
            return context.getString(R.string.error_server_response);
        } else {
            return error.getMessage();
        }
    }

    /**
     * Method for adding request to existing queue.
     *
     * @param req {@link Request} for getting data from the Internet.
     * @param <T> The type of parsed response this request expects.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Method for cancelling all requests witch have the same tag as {@param requestTag}.
     *
     * @param requestTag tag of the request to cancel.
     */
    public void cancelAllRequests(String requestTag) {
        getRequestQueue().cancelAll(requestTag);
    }
}
