package cz.movapp.android

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

/**
 * @return null if failed
 */
fun playSound(
    context: Context,
    assetFileName: String
): MediaPlayer? {
    val afd: AssetFileDescriptor = context.assets.openFd(assetFileName)
    var player: MediaPlayer? = MediaPlayer()
    player?.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());

    player?.setOnCompletionListener { mp ->
        mp?.reset()
        mp?.release()
        player = null
    }
    player?.prepare()
    player?.start()

    return player
}

fun hideKeyboard(view: View, activity: FragmentActivity?) {
    val inputMethodManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}