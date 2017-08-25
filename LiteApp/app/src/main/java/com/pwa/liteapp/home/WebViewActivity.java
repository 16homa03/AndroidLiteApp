package com.pwa.liteapp.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.pwa.liteapp.utils.AppConstants;
import com.pwa.liteapp.webview.PWAWebView;
import com.pwa.liteapp.webview.WebViewClientImpl;

import java.io.File;
import java.io.IOException;

public class WebViewActivity extends Activity {

    private static final String TAG = "WebViewActivity";
    private PWAWebView pwaWebView;
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        pwaWebView = new PWAWebView(this);

        pwaWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        WebSettings webSettings = pwaWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        Log.i("anukalp", pwaWebView.getSettings().getUserAgentString());


        progDailog = ProgressDialog.show(WebViewActivity.this, "Loading", "Please wait...", true);
        progDailog.setCancelable(false);
        progDailog.show();

        pwaWebView.setWebViewClient(new WebViewClientImpl(getApplicationContext()));

        // pwaWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        linearLayout.addView(pwaWebView);
        setContentView(linearLayout);
        String baseUrl = "https://www.example.com/";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        File internalFile = null;
        while (true) {
            internalFile = new File(this.getApplicationContext().getFilesDir(), "pwa/dist/v2/main/index.html");
            if (internalFile.exists()) {
                break;
            }
        }

        if (null != internalFile) {
            try {
                String data = AppConstants.readFile("pwa/dist/v2/main/index.html", getApplicationContext());
                String mimeType = "text/html";
                pwaWebView.loadDataWithBaseURL(baseUrl, data, mimeType, "UTF-8", null);
                progDailog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (pwaWebView.canGoBack()) {
            pwaWebView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
