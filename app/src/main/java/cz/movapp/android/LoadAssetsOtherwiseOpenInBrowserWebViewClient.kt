package cz.movapp.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

open class LoadAssetsOtherwiseOpenInBrowserWebViewClient(context: Context) :
    WebViewClientCompat() {

    val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(context))
        .build()

    @RequiresApi(21)
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(request.url)
    }

    // to support API < 21
    override fun shouldInterceptRequest(
        view: WebView,
        url: String
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(Uri.parse(url))
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        return if (url != null && (url.startsWith("http://") || url.startsWith("https://"))
        ) {
            view.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            )
            true
        } else {
            false
        }
    }


}