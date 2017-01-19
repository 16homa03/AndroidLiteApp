package com.mmt.liteapp;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by anukalp on 19/1/17.
 */

public class StaticContentCache {

    private static final String LOG_TAG = "StaticContentCache";

    private static final String ENCODING_SCHEME = "UTF-8";

    private static final String MIME_TEXT_HTML = "text/html";
    private static final String MIME_TEXT_CSS = "text/css";
    private static final String MIME_IMAGE_PNG = "image/png";
    private static final String MIME_IMAGE_GIF = "image/gif";
    private static final String MIME_IMAGE_JPEG = "image/jpeg";


    private final Context appContext;

    public StaticContentCache(Context appContext) {
        this.appContext = appContext;
    }

    /**
     * Static resources should always be served from cache if not present then will fallback to okhttp client for network request
     *
     * @param filepath
     * @param request
     * @param mimeType
     * @return
     */
    public WebResourceResponse load(String filepath, WebResourceRequest request, String mimeType) {
        String url = request.getUrl().toString();
        Log.d(LOG_TAG, "Starting cache: " + filepath);
        File cachedFile = new File(appContext.getFilesDir(), filepath);

        if (cachedFile.exists()) {
            //cached file exists and is not too old. Return file.
            Log.d(LOG_TAG, "Loading from cache: " + url);
            try {
                WebResourceResponse response = new WebResourceResponse(
                        mimeType, ENCODING_SCHEME, new FileInputStream(cachedFile));
                Map<String, String> headers = new HashMap<>();
                headers.put("Access-Control-Allow-Origin", "http://www.makemytrip.com");
                response.setResponseHeaders(headers);
                return response;
            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "Error loading cached file: " + cachedFile.getPath() + " : "
                        + e.getMessage(), e);
            }

        } else {
            try {
                downloadAndStore(request, filepath);

                //now the file exists in the cache, so we can just call this method again to read it.
                return load(filepath, request, mimeType);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error reading file over network: " + cachedFile.getPath(), e);
            }
        }

        return null;
    }


    public void downloadAndStore(WebResourceRequest request, String filepath)
            throws IOException {
        File internalFile = new File(appContext.getFilesDir(), filepath);
        if (!internalFile.getParentFile().exists()) {
            internalFile.getParentFile().mkdirs();
        }

        Response response = HttpClient.getInstance().getHttpResponse(request);

        InputStream is = response.body().byteStream();
        BufferedInputStream input = new BufferedInputStream(is);
        OutputStream output = new FileOutputStream(internalFile);

        byte[] data = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }
        output.flush();
        output.close();

        // Make Sure you close your response body
        response.body().close();

        Log.d(LOG_TAG, "Cache file: " + filepath + " stored. ");
    }
}
