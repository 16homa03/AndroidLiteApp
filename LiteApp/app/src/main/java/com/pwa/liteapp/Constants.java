package com.pwa.liteapp;

import android.webkit.WebResourceRequest;

/**
 * Created by anukalp on 16/1/17.
 */
public class Constants {
    public static final String LOG_TAG = "PWALiteApp";

    /**
     * shouldInterceptRequest all url's that you want to intercept and cache {@link DynamicContentCache} or {@link StaticContentCache}
     *
     * @param request
     * @return
     */
    public static boolean shouldInterceptNetworkRequest(WebResourceRequest request) {
        return request.getUrl().getAuthority().equals("pwav2.example.com") || request.getUrl().getAuthority().equals("www.example.com") || request.getUrl().getAuthority().equals("imgak.examplecdn.com") ||
                request.getUrl().getAuthority().equals("cssak.examplecdn.com") || request.getUrl().getAuthority().equals("jsak.examplecdn.com");
    }
}
