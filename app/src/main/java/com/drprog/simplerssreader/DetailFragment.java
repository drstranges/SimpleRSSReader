package com.drprog.simplerssreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created on 26.02.2015.
 */
public class DetailFragment extends Fragment{
    public static final String TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URL = "URL";
    private String mUrl;

    public static DetailFragment newInstance(String detailUrl){
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(DetailFragment.DETAIL_URL, detailUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUrl = arguments.getString(DetailFragment.DETAIL_URL);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        WebView webView = (WebView)rootView.findViewById(R.id.webView);


        //getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);

        final Activity activity = getActivity();
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);

        if (mUrl != null){
            webView.loadUrl(mUrl);
        }


        return rootView;
    }
}
