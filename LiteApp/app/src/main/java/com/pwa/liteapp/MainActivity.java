package com.pwa.liteapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
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


        progDailog = ProgressDialog.show(MainActivity.this, "Loading", "Please wait...", true);
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
                String data = readFile("pwa/dist/v2/main/index.html");
                String mimeType = "text/html";
                pwaWebView.loadDataWithBaseURL(baseUrl, data, mimeType, "UTF-8", null);
                progDailog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class AjaxCallInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        AjaxCallInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void printLog(String data) {
            Log.d(TAG, "data value :: [" + data + "]");
        }

        @JavascriptInterface
        public void makeXHR(String method, String url, boolean async, String contentType, String data) {

        }

        @JavascriptInterface
        public void getTemplate(String htmlpath, String successCallback, String errorCallback) {
            try {
                String htmlFile = readFile(htmlpath);
                String js = "javascript:" + successCallback + "(\"" + htmlpath + ',' + htmlFile + "\")";
                pwaWebView.loadUrl(js);
            } catch (IOException e) {
                e.printStackTrace();
                String js = "javascript:" + errorCallback + "(\"" + htmlpath + "\")";
                pwaWebView.loadUrl(js);
            }
        }

        private String readFile(String htmlpath) throws IOException {
            File html = new File(getApplicationContext().getFilesDir(), htmlpath);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(html), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }


    }

    private String readFile(String htmlpath) throws IOException {
        File html = new File(getApplicationContext().getFilesDir(), htmlpath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(html), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
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
