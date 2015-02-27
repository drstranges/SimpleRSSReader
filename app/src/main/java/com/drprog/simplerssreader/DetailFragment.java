package com.drprog.simplerssreader;

import android.graphics.Bitmap;
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
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Detail Screen
 */
public class DetailFragment extends Fragment {
    public static final String TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URL = "URL";
    private String mUrl;
    private WebView mWebView;
    private ProgressBar progressBar;


    public static DetailFragment newInstance(String detailUrl) {
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
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        //final Activity activity = getActivity();
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //activity.setProgress(progress * 1000);
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);

        mWebView.requestFocusFromTouch();


        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //String newUA= "Mozilla/5.0";
        //webSettings.setUserAgentString(newUA);

        if (mUrl != null) {
            mWebView.loadUrl(mUrl);
        }


        return rootView;
    }


}
