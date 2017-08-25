package com.pwa.liteapp.data.sw.toolbox;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.pwa.liteapp.data.network.HttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by anukalp on 19/1/17.
 */

public class DynamicContentCache {

    private static final String LOG_TAG = "DynamicContentCache";
    private final OkHttpClient client;

    public DynamicContentCache(Context appContext) {
        File internalFile = new File(appContext.getFilesDir(), LOG_TAG);
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(internalFile, cacheSize);
        client = HttpClient.getInstance().buildCachedClient(cache);
    }

    /**
     * @param webResourceRequest
     * @return
     */
    public WebResourceResponse cacheAjaxResponse(WebResourceRequest webResourceRequest) {

        String url = webResourceRequest.getUrl().toString();
        Log.i(LOG_TAG, "cacheAjaxResponse url:" + url);
        CacheEntry cacheEntry = this.cacheEntries.get(url);
        if(null == cacheEntry)
            return null;

        Request.Builder builder = null;
        if (null == cacheEntry) {
            builder = new Request.Builder().cacheControl(new CacheControl.Builder().noCache().build()).url(url);
        } else {
            builder = new Request.Builder().cacheControl(new CacheControl.Builder()
                    .maxStale(cacheEntry.maxAgeSeconds, TimeUnit.SECONDS)
                    .build()).url(url);
        }

        Headers.Builder headerBuilder = new Headers.Builder();
        Map<String, String> map = webResourceRequest.getRequestHeaders();
        if (null != map) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        builder.headers(headerBuilder.build());
        Request request = builder.build();


        Response okResponse = null;
        try {
            okResponse = client.newCall(request).execute();
        } catch (IOException e) {
            return null;
        }

        if (okResponse != null) {
            int statusCode = okResponse.code();
            String encoding = "UTF-8";
            String mimeType = "application/json";
            String reasonPhrase = "OK";
            Map<String, String> responseHeaders = new HashMap<String, String>();
            if (okResponse.headers() != null) {
                if (okResponse.headers().size() > 0) {
                    for (int i = 0; i < okResponse.headers().size(); i++) {
                        String key = okResponse.headers().name(i);
                        String value = okResponse.headers().value(i);
                        responseHeaders.put(key, value);
                    }
                }
            }
            responseHeaders.put("Access-Control-Allow-Origin", "https://www.makemytrip.com");
            InputStream data = okResponse.body().byteStream();

            Log.i(LOG_TAG, "okResponse code:" + data);

            return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhrase, responseHeaders, data);
        }
        return null;
    }

    private static class CacheEntry {
        public String url;
        public String mimeType;
        public String encoding;
        public int maxAgeSeconds;

        private CacheEntry(String url,
                           String mimeType, String encoding, int maxAgeSeconds) {

            this.url = url;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.maxAgeSeconds = maxAgeSeconds;
        }
    }


    protected Map<String, CacheEntry> cacheEntries = new HashMap<String, CacheEntry>();

    public void register(String url, String mimeType, String encoding,
                         int maxAgeSeconds) {

        CacheEntry entry = new CacheEntry(url, mimeType, encoding, maxAgeSeconds);

        this.cacheEntries.put(url, entry);
    }

}
