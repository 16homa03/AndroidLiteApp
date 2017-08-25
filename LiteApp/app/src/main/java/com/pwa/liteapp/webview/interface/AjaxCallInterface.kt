package com.pwa.liteapp.webview.`interface`

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.pwa.liteapp.utils.AppConstants
import java.io.IOException

/**
 * Created by anukalp on 25/8/17.
 */

class AjaxCallInterface
/**
 * Instantiate the interface and set the context
 */
internal constructor(internal var mContext: Context) {

    @JavascriptInterface
    fun printLog(data: String) {
        Log.d("AjaxCallInterface", "data value :: [$data]")
    }

    @JavascriptInterface
    fun makeXHR(method: String, url: String, async: Boolean, contentType: String, data: String) {

    }

    @JavascriptInterface
    fun getTemplate(htmlpath: String, successCallback: String, errorCallback: String) {
        try {
            val htmlFile = AppConstants.readFile(htmlpath, mContext)
            val js = "javascript:$successCallback(\"$htmlpath,$htmlFile\")"
            //pwaWebView.loadUrl(js)
        } catch (e: IOException) {
            e.printStackTrace()
            val js = "javascript:$errorCallback(\"$htmlpath\")"
            //pwaWebView.loadUrl(js)
        }

    }

}