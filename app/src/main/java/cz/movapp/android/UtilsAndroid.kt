package cz.movapp.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.webkit.WebView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

fun RecyclerView.getSavableScrollState(): Int {
    return when (this.layoutManager) {
        null -> throw UnsupportedOperationException("RecyclerView: No LayoutManager set")
        is LinearLayoutManager -> (this.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        else -> throw UnsupportedOperationException("RecyclerView: Can't save scroll state. Unknown LayoutManager")
    }
}

fun RecyclerView.restoreSavableScrollState(position: Int) {
    val layoutManager = this.layoutManager as LinearLayoutManager?
    layoutManager?.scrollToPositionWithOffset(position, 0)
}

fun WebView.enableDarkMode() {
    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
        when (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF)
            }
            else -> {
                //
            }
        }
    }
}


fun openUri(context: Context, textUri: String) {
    val queryUrl: Uri = Uri.parse(textUri)
    val intent = Intent(Intent.ACTION_VIEW, queryUrl)
    context.startActivity(intent)
}