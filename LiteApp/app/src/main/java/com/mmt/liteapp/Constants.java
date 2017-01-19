package com.mmt.liteapp;

import android.webkit.WebResourceRequest;

/**
 * Created by anukalp on 16/1/17.
 */
public class Constants {
    public static final String LOG_TAG = "MMTLiteApp";

    public static boolean shouldServeNetworkRequest(WebResourceRequest request) {
        return request.getUrl().getAuthority().equals("pwav2.makemytrip.com") || request.getUrl().getAuthority().equals("www.makemytrip.com") || request.getUrl().getAuthority().equals("imgak.mmtcdn.com") ||
                request.getUrl().getAuthority().equals("cssak.mmtcdn.com") || request.getUrl().getAuthority().equals("jsak.mmtcdn.com");
    }
}
