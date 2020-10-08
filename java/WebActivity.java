package com.abhigam.www.foodspot;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends AppCompatActivity {

    WebView wb;
    ImageView back;
    ProgressBar mProgressBar;
    private static final String WEB_URL_INTENT = "web_url_intent_crony";
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        url = getIntent().getStringExtra(WEB_URL_INTENT);

        back = findViewById(R.id.back_arrow_image);
        mProgressBar = findViewById(R.id.progress_bar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        wb=findViewById(R.id.webView1);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        wb.setWebViewClient(new HelloWebViewClient());
        wb.loadUrl(url);
    }
    private class HelloWebViewClient extends WebViewClient {

        public HelloWebViewClient(){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
