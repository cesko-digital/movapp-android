package cz.movapp.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.util.Locale

fun RecyclerView.getSavableScrollState(): Int {
    return when (this.layoutManager) {
        null -> throw UnsupportedOperationException("RecyclerView: No LayoutManager set")
        is LinearLayoutManager -> (this.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        else -> throw UnsupportedOperationException("RecyclerView: Can't save scroll state. Unknown LayoutManager")
    }
}

fun RecyclerView.restoreSavableScrollState(position: Int) {
    val layoutManager = this.layoutManager as LinearLayoutManager?
    layoutManager?.scrollToPositionWithOffset(position, 1)
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

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}


class CapitalizedTextView: androidx.appcompat.widget.AppCompatTextView  {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    override fun setText(text: CharSequence?, type: BufferType?) {
        val capitalized = text.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        super.setText(capitalized , type)
    }
}