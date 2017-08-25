package com.pwa.liteapp.utils;

import android.content.Context;
import android.webkit.WebResourceRequest;

import com.pwa.liteapp.data.sw.toolbox.DynamicContentCache;
import com.pwa.liteapp.data.sw.toolbox.StaticContentCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by anukalp on 16/1/17.
 */
public class AppConstants {
    public static final String LOG_TAG = "PWALiteApp";


    public static String readFile(String htmlPath, Context appContext) throws IOException {
        File html = new File(appContext.getFilesDir(), htmlPath);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(html), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

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
