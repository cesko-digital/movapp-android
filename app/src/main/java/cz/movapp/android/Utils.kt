package cz.movapp.android

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import cz.movapp.app.data.LanguagePair
import java.text.Normalizer


/**
 * @return null if failed
 */
fun playSound(
    context: Context,
    assetFileName: String,
    playbackSpeed: Float = 1.0f
): MediaPlayer? {
    val afd: AssetFileDescriptor = context.assets.openFd(assetFileName)
    var player: MediaPlayer? = MediaPlayer()
    player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
    afd.close()

    player?.setOnCompletionListener { mp ->
        mp?.reset()
        mp?.release()
        player = null
    }
    player?.prepare()
    // FIXME will not work on lower Android versions
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        player?.playbackParams?.apply {
            speed = playbackSpeed
            player?.playbackParams = this
        }
    }
    player?.start()

    return player
}

fun hideKeyboard(view: View, activity: FragmentActivity?) {
    val inputMethodManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createLangAssetsString(langPair: LanguagePair): String {
    return if (langPair.isReversed) {
        "${langPair.from.langCode}-${langPair.to.langCode}"
    } else {
        "${langPair.to.langCode}-${langPair.from.langCode}"
    }
}

fun stripDiacritics(input: String): String {
    var output = Normalizer.normalize(input, Normalizer.Form.NFD)
    return output.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}