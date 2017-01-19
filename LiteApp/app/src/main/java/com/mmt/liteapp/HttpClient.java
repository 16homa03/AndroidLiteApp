package com.mmt.liteapp;

import android.webkit.WebResourceRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by anukalp on 19/1/17.
 */

/**
 * HTTP Client serves http request with okhttp3
 * Request must be served synchronously as it's already running in separate thread.
 */
public class HttpClient {

    private OkHttpClient client;


    /**
     * Create a new connection pool with tuning parameters appropriate for a single-user application.
     * The tuning parameters in this pool are subject to change in future OkHttp releases. Currently
     * this pool holds up to 5 idle connections which will be evicted after 5 minutes of inactivity.
     */
    private HttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new RequestLoggingInterceptor());
        client = builder.build();
    }

    public OkHttpClient buildCachedClient(Cache cache) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new RequestLoggingInterceptor());
        builder.cache(cache);
        return builder.build();
    }

    public Response getHttpResponse(WebResourceRequest webResourceRequest) throws IOException {
        Request.Builder builder = new Request.Builder();
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
        Request request = builder.url(webResourceRequest.getUrl().toString())
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response;
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class HttpClientHolder {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    public static HttpClient getInstance() {
        return HttpClientHolder.INSTANCE;
    }

}