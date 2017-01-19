package com.mmt.liteapp;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by anukalp on 16/1/17.
 */

public class WebViewClientImpl extends WebViewClient {

    private Context appContext = null;
    private StaticContentCache staticCache = null;
    private DynamicContentCache dynamicContentCache = null;

    public WebViewClientImpl(Context appContext) {
        this.appContext = appContext;
        this.staticCache = new StaticContentCache(appContext);
        this.dynamicContentCache = new DynamicContentCache(appContext);
        // Register for network caching
        // this.dynamicContentCache.register();

    }

    /**
     * Notify the host application that an SSL error occurred while loading a
     * resource. The host application must call either handler.cancel() or
     * handler.proceed(). Note that the decision may be retained for use in
     * response to future SSL errors. The default behavior is to cancel the
     * load.
     *
     * @param view    The WebView that is initiating the callback.
     * @param handler An SslErrorHandler object that will handle the user's
     *                response.
     * @param error   The SSL error object.
     */
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        handler.proceed();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.d("anukalp", "request.getUrl()" + request.getUrl());
        if (request.getUrl().toString().indexOf("makemytrip.com") > -1) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
        appContext.startActivity(intent);
        return true;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      WebResourceRequest request) {
        Log.d(Constants.LOG_TAG, "shouldInterceptRequest :: " + request.getUrl() + " , " + request.getMethod() + ", " + request.getRequestHeaders());
        String mimeType = "text/html";
        if (request.getMethod().equals("GET")) {
            if (Constants.shouldServeNetworkRequest(request)) {
                String filePath = request.getUrl().getPath().toString();
                if (filePath.contains("screen")) {
                    return this.dynamicContentCache.cacheAjaxResponse(request);
                }
                if ("/".equals(filePath)) {
                    filePath = "pwa/dist/v2/main/index.html";
                    return this.staticCache.load(filePath, request, mimeType);
                }
                if (filePath.contains("global.js")) {
                    return null;
                }
                if (filePath.contains(".js")) {
                    mimeType = "application/javascript";
                } else if (filePath.contains(".css")) {
                    mimeType = "text/css";
                } else if (filePath.contains(".png")) {
                    mimeType = "image/png";
                } else if (filePath.contains(".gif")) {
                    mimeType = "image/gif";
                } else if (filePath.contains(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (filePath.contains(".html")) {
                    mimeType = "text/html";
                } else {
                    return null;
                }
                return this.staticCache.load(filePath, request, mimeType);
            }
        }
        return null;
    }
}
